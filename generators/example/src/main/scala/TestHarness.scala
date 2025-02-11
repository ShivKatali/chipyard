package example

import chisel3._
import chisel3.experimental._

import firrtl.transforms.{BlackBoxResourceAnno, BlackBoxSourceHelper}

import freechips.rocketchip.diplomacy.LazyModule
import freechips.rocketchip.config.{Field, Parameters}
import freechips.rocketchip.util.GeneratorApp

import sifive.blocks.devices.uart._

// --------------------------
// BOOM and/or Rocket Test Harness
// --------------------------

case object BuildBoomRocketTop extends Field[(Clock, Bool, Parameters) => BoomRocketTopModule[BoomRocketTop]] //   with HasPeripheryPWMModuleImp]// with HasPeripheryUARTModuleImp]

class BoomRocketTestHarness(implicit val p: Parameters) extends Module {
  val io = IO(new Bundle {
    val success = Output(Bool())
  })

  // force Chisel to rename module
  override def desiredName = "TestHarness"

  val dut = p(BuildBoomRocketTop)(clock, reset.toBool, p)
  dut.debug := DontCare

 // UART.loopback(dut.uart(0))                          //  foruart in loopback mode


 // PWM.getout(dut.pwm(0))

  dut.connectSimAXIMem()
  dut.connectSimAXIMMIO()
  dut.dontTouchPorts()
  dut.tieOffInterrupts()
  dut.l2_frontend_bus_axi4.foreach(axi => {
    axi.tieoff()
    experimental.DataMirror.directionOf(axi.ar.ready) match {
      case core.ActualDirection.Input =>
        axi.r.bits := DontCare
        axi.b.bits := DontCare
      case core.ActualDirection.Output =>
        axi.aw.bits := DontCare
        axi.ar.bits := DontCare
        axi.w.bits := DontCare
    }
  })
  io.success := dut.connectSimSerial()
}
