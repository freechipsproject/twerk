package tpu_pkg

import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import Cfg._

import scalaz.zio._

import org.la4j.matrix.{DenseMatrix}


class GridUnitTest(c: GridShell) extends PeekPokeTester(c) {


  val uut  = c.io
 
  // Gen input data 
  val seed  = 0
  val r = new java.util.Random(seed)
  
  //val A,B,C,D  = DenseMatrix.random(xlen, ylen, r)
  val A,B,C,D  = DenseMatrix.zero(xlen, ylen)

  val MaxVal  = 8
  

  for {
    i <- 0 until xlen
    j <- 0 until ylen
    } yield {
    A.set(i,j, r.nextInt(MaxVal))
    B.set(i,j, r.nextInt(MaxVal))
    C.set(i,j, r.nextInt(MaxVal))
    D.set(i,j, r.nextInt(MaxVal))
  }
  

  def driveA () = {

    for {
      i <- 0 until xlen
      j <- 0 until ylen
      } yield {
        poke (uut.adin(0).valid, 1)
        poke (uut.adin(0).bits.data, A.get(i,j).toInt)
        step(1)
    }
    poke (uut.adin(0).valid, 0)
    poke (uut.adin(0).bits.data, 0)
    
  }

  def driveB () = {

    for {
      i <- 0 until xlen
      j <- 0 until ylen
      } yield {
        poke (uut.adin(1).valid, 1)
        poke (uut.adin(1).bits.data, B.get(i,j).toInt)
        step(1)
    }
    poke (uut.adin(1).valid, 0)
    poke (uut.adin(1).bits.data, 0)
    
  }

  val runtime = new DefaultRuntime {}

  val t0:Task[Unit]  = ZIO.effect(driveA ())
  val t1:Task[Unit]  = ZIO.effect(driveB ())
  
  // run sequentially
  runtime.unsafeRun(t0)
  runtime.unsafeRun(t1)

  def runpar0 () = {

    val res = for {

      f0  <- t0.fork
      f1  <- t1.fork

      r0  <- f0.join
      r1  <- f1.join
  
    //} yield(f0.join, f1.join)
    } yield()
  
    res
  }
  
  def runpar1 () = {

    val res = for {

      f0  <- t0.fork
      f1  <- t1.fork

      fib  = f0 zip f1
      r <- fib.join
  
    } yield(r)

    res
  }
  
  def runpar2 () = {
    val res  = t0 race t1 
    res
  }

  step (5)
  runtime.unsafeRun(runpar0())
  
  step (5)
  runtime.unsafeRun(runpar1())
  
  step (5)
  runtime.unsafeRun(runpar2())

  step (20)
  finish

}

class GridTester extends ChiselFlatSpec {
  
  // From the top config
  testType match {

    case "native"  =>

      "GRID" should "run in a native mode" in {

        iotesters.Driver.execute(
          Array("--target-dir", "out/grid", "--top-name", "top"), 
          () => new GridShell) {
            c => new GridUnitTest(c)
        } should be(true)
      }
    
    case "debug"    => 

      "GRID" should "run in a debug mode" in {

        iotesters.Driver.execute(
          Array("--is-verbose","--generate-vcd-output", saveDump, "--target-dir", "out/grid", "--top-name", "top"), 
          () => new GridShell) {
            c => new GridUnitTest(c)
        } should be(true)
      }


    case "verilog"   =>

      "GRID" should "run in Verilator" in {

        iotesters.Driver.execute(
          Array("--generate-vcd-output", saveDump, "--target-dir", "out/grid", "--top-name", "top", "--backend-name", "verilator"), 
          () => new GridShell) {
            c => new GridUnitTest(c)
        } should be(true)
      }

    case _ => throw new RuntimeException(s"Invalid testType provided: $testType")
  }
}

object GridMain extends App {

  //val runtime = new DefaultRuntime {}

  def run(args: List[String]) =
    appmain.fold(_ => 1, _ => 0)

  val appmain = IO.succeed(iotesters.Driver.execute ( Array[String](""), () => new GridShell){
    c => new GridUnitTest(c)} )

  //val top  = runtime.unsafeRun(appmain)
  
}


