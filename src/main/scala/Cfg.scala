// This pkg contains global parameters, functions and implicits

import chisel3._
import chisel3.core.{FixedPoint => FP}
import simulacrum._

package object Cfg {

  // Test type
  val testType  = "native" // "native", "debug" , "verilog"
  val saveDump  = "on"  // "on", "off"

  // Grid Setup 
  val xlen  = 4
  val ylen  = 4

  // Full bit size
  val dwin  = 14
  val dwout = 20  
 
  // Fractional bit size
  val qin   = 8
  val qout  = 12
  val dtype = "uint"

  println (s"Datatype = $dtype")
  
  //--------------------------------------------------------------------------------------------
  // Define a Zero element typeclass
  //--------------------------------------------------------------------------------------------
  @typeclass trait Zero[A] {
    def zero:A
  }

  object Zero {

    implicit val UIntZero: Zero[UInt] = new Zero[UInt] {
      def zero = 0.U
    }
    
    implicit val SIntZero: Zero[SInt] = new Zero[SInt] {
      def zero = 0.S
    }
  }
  
  
  val types  = dtype match {

    case s:String if (s == "uint")        => (UInt(dwin.W), UInt(dwout.W))
    case s:String if (s == "sint")        => (SInt(dwin.W), SInt(dwout.W))
    case s:String if (s == "fixedpoint")  => (FP(dwin.W, qin.BP), FP(dwout.W, qout.BP))

    case _ => throw new RuntimeException(s"Invalid datatype provided: $dtype")

  }

  // FIXME: How to derive types from the tupple ???
  //def inType  = types._1
  //def outType = types._2
  val inType  = UInt(dwin.W)
  val outType = UInt(dwout.W)
  
}
