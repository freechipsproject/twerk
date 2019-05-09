package tpu_pkg

import chisel3._
import chisel3.util.{Valid}

class Link[A <: Data] (dType:A) extends Bundle {
  val data = Output (dType)
  override def cloneType = (new Link(dType)).asInstanceOf[this.type]
}

final object Link {
  def apply[A <: Data] (dType:A) = new Link (dType)
}

//--------------------------------------------------------------------------------------------
// Top level interface
//--------------------------------------------------------------------------------------------

class GridIO[A <: Data, B <: Data] (inType:A, outType:B, xlen:Int, ylen:Int) extends Bundle {
  val adin  = Vec (xlen, Flipped(Valid(Link[A](inType))))
  val bdin  = Vec (ylen, Flipped(Valid(Link[A](inType))))
  val dout  = Vec (ylen, Valid(Link[B](outType)))
  
  override def cloneType = (new GridIO(inType, outType, xlen, ylen)).asInstanceOf[this.type]
}

