// See LICENSE for license details.
package example

import Chisel._
import chisel3.experimental.{withClockAndReset}
import sifive.blocks.devices.pinctrl.{Pin}

class PWMSignals[T <: Data](private val pingen: () => T) extends Bundle {
  val pwmout = pingen()
}


class PWMPins[T <: Pin](pingen: () => T) extends PWMSignals[T](pingen)


object PWMPinsFromPort {
  
  def apply[T <: Pin] (pins: PWMSignals[T], port: PWMPortIO, clock: Clock, reset: Bool, c : PWMParams): Unit = {              
    withClockAndReset(clock, reset){
    		for(i <- 0 until c.numout){                                                                                     
      pins.pwmout.outputPin(port.pwmout(i))
      }
    }
  }


  def apply[T <: Pin] (pins: PWMSignals[T], port: PWMPortIO, c: PWMParams): Unit = {                                                                                                          
  	for(i <- 0 until c.numout){
    pins.pwmout.outputPin(port.pwmout(i))
    }
  }
}



