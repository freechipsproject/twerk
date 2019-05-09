// See README.md for license details.

package unit

import Cfg.{xlen, ylen}
import chisel3._
import chisel3.iotesters.TesterOptionsManager
import chisel3.tester._
import org.la4j.matrix.DenseMatrix
import org.scalatest.FreeSpec
import tpu_pkg.GridShell

import scala.util.Random

class GridTestUsingTesters2 extends FreeSpec with ChiselScalatestTester {
  "parallel events should work" in {
    val manager = new TesterOptionsManager {
      testerOptions = testerOptions.copy(generateVcdOutput = "on")
      treadleOptions = treadleOptions.copy(writeVCD = true)
    }
    (new TestBuilder(() => new GridShell, Some(manager), None)) { dut =>
      val uut = dut.io

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

      def driveA (): Unit = {

        for {
          i <- 0 until xlen
          j <- 0 until ylen
        } yield {
          uut.adin(0).valid.poke(true.B)
          uut.adin(0).bits.data.poke( A.get(i,j).toInt.U)
          dut.clock.step(1)
        }
        uut.adin(0).valid.poke(true.B)
        uut.adin(0).bits.data.poke(0.U)

      }

      def driveB (): Unit = {

        for {
          i <- 0 until xlen
          j <- 0 until ylen
        } yield {
          uut.adin(1).valid.poke(true.B)
          uut.adin(1).bits.data.poke( B.get(i,j).toInt.U)
          dut.clock.step(1)
        }
        uut.adin(1).valid.poke(true.B)
        uut.adin(1).bits.data.poke(0.U)
      }

      def runpar0(): Unit = {
        fork {
          driveA()
        }.fork {
          driveB()
        }.join
      }

      dut.clock.step(5)

      timescope {
        runpar0()
      }

    }
  }
}
