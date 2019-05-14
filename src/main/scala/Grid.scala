

package tpu_pkg

import chisel3._
import chisel3.util.{Valid}
import Cfg._
import Zero.ops._

// 2D grid of Nodes
class Grid[A <: Data, B <: Data] (inType:A, outType:B, xlen:Int, ylen:Int) extends Module {

  val io  = IO (new GridIO(inType, outType, xlen, ylen))
  val zeroval  = 0.U(outType.getWidth.W).asTypeOf(outType)

  for (i <- 0 until ylen) {
    io.dout(i).valid := false.B 
    io.dout(i).bits.data := Zero[UInt].zero 
  }
  
}

// Verilator wrapper for Grid 

class GridShell extends Module {
  
  val io  = IO (new GridIO(inType, outType, xlen, ylen))
  require (xlen > 1 && ylen > 1, "Invalid grid size")
  
  val grid  = Module (new Grid(inType, outType, xlen, ylen))
  val gio   = grid.io
 
  for (i <- 0 until xlen){
    gio.adin(i) <> RegNext(io.adin(i))
  }

  for (i <- 0 until ylen) {
    gio.bdin(i) <> RegNext(io.bdin(i))
    io.dout(i)  <> RegNext(gio.dout(i))
  }
  
}


