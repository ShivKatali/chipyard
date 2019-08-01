package example


import chisel3._
import chisel3.util._
import freechips.rocketchip.config.Parameters
import freechips.rocketchip.diplomacy._
import freechips.rocketchip.interrupts._
import freechips.rocketchip.regmapper._
import freechips.rocketchip.tilelink._
import freechips.rocketchip.util._
import sifive.blocks.util.BasicBusBlocker

class PWMModule(val w: Int) extends Module {
  val io = IO(new Bundle {
    val pwmout = Output(Bool())
    val period = Input(UInt(w.W))
    val duty = Input(UInt(w.W))
    val numpulse = Input(UInt(w.W))
    val kill = Input(Bool())
    val master_enable = Input(Bool())                              
    val align = Input(UInt(w.W))
    val shift = Input(UInt(w.W))
  
  })

io.pwmout := false.B

val maxpulse : UInt = (1.U << w) 

val numpulsecounter = RegInit(1.U(w.W))
val counter = RegInit(0.U(w.W))
val alignamt = RegInit(1.U(16.W))                                    
val alignsel = RegInit(0.U(16.W)) 
val cflag  =  RegInit(false.B)

alignamt := io.align(31,16)  
alignsel := io.align(15,0)                                                                    

when(io.shift >= (io.period)){
    counter := 0.U
}
.otherwise{
    counter := io.shift
}


when(io.master_enable && (io.period > io.duty)){


 when(~io.kill && (numpulsecounter <= (io.period * io.numpulse)) &&  (io.numpulse <= maxpulse )   && ((alignsel === 0x1.U) || (alignsel === 0x2.U)) ){
    when (counter >= (io.period - 1.U)) {
      counter := 0.U
    } .otherwise {
      counter := counter + 1.U
    }

     when(alignsel === 0x1.U){
       io.pwmout := ~io.kill && (counter < io.duty)
    }.otherwise{
       io.pwmout := ~(~io.kill && (counter < io.duty))
    }

    numpulsecounter := numpulsecounter + 1.U

  }
  .elsewhen(~io.kill && io.numpulse > maxpulse  && ((alignsel === 0x1.U) || (alignsel === 0x2.U)) ){
      when (counter >= (io.period - 1.U)) {
        counter := 0.U
      } .otherwise {
        counter := counter + 1.U
      }

     
     when(alignsel === 0x1.U){
       io.pwmout := ~io.kill && (counter < io.duty)
    }.elsewhen( alignsel === 0x2.U) {
       io.pwmout := ~(~io.kill && (counter < io.duty))
    }
  }

  .elsewhen(~io.kill && (numpulsecounter <= (io.period * io.numpulse)) && (io.numpulse <= maxpulse) && ((alignsel === 0x3.U) || (alignsel === 0x4.U))  ){
    when (counter >= (io.period -1.U)) {
      counter := 0.U
    }.otherwise {
      counter := counter + 1.U
    }

    when(alignsel === 0x3.U){
          io.pwmout := ~io.kill && (counter >= io.period - io.duty ) 
    }.elsewhen(alignsel === 0x4.U) {
         io.pwmout := ~(~io.kill && (counter >= io.period - io.duty ) )
    }  
      
        numpulsecounter := numpulsecounter + 1.U

  }


  .elsewhen(~io.kill && (io.numpulse > maxpulse)  && ((alignsel === 0x3.U) || (alignsel === 0x4.U))  ){
      when (counter >= (io.period - 1.U)) {
        counter := 0.U
      } .otherwise {
        counter := counter + 1.U
      }
       
    when(alignsel === 0x3.U){
          io.pwmout := ~io.kill && (counter >= io.period - io.duty ) 
    }.elsewhen(alignsel === 0x4.U) {
         io.pwmout := ~(~io.kill && (counter >= io.period - io.duty ) )
    }         

  }



  .elsewhen(~io.kill && (numpulsecounter <= (2.U * io.period * io.numpulse)) && (io.numpulse <= maxpulse) && ( (alignsel === 0x5.U) ||  (alignsel === 0x6.U) )  && ((alignamt > 0.U) &&  ( alignamt < io.period ))     ){
    when ((counter >= io.period ) || (cflag === true.B)) {
      cflag := true.B
      counter := counter - 1.U
      when(counter === 1.U ){
         cflag := false.B
      }

      } .elsewhen(cflag === false.B) {
          counter := counter + 1.U
      }
    
       when(alignsel === 0x5.U){
        io.pwmout := ~io.kill && (counter >= io.align(31,16))
      }
      .elsewhen(alignsel === 0x6.U){
          io.pwmout := ~(~io.kill && (counter >= io.align(31,16)))
      }

       numpulsecounter := numpulsecounter + 1.U

  }



 }.elsewhen(~io.kill  && (io.numpulse > maxpulse) && ( (alignsel === 0x5.U) ||  (alignsel === 0x6.U) )  && ((alignamt > 0.U) &&  ( alignamt < io.period ))     ){
    when ((counter >= io.period )|| (cflag ===true.B)) {
      cflag := true.B
      counter := counter - 1.U
      when(counter === 1.U ){
         cflag := false.B
      }

      } .elsewhen(cflag === false.B) {
          counter := counter + 1.U
      }
    
       when(alignsel === 0x5.U){
        io.pwmout := ~io.kill && (counter >= io.align(31,16))
      }
      .elsewhen(alignsel === 0x6.U){
          io.pwmout := ~(~io.kill && (counter >= io.align(31,16)))
      }

   

}.elsewhen(io.master_enable && (io.period <= io.duty)){
      io.pwmout := true.B
  }

}


case class PWMParams(
  address: BigInt,
  numout : Int  = 8)


class PWMPortIO(val c :PWMParams) extends Bundle {
  val pwmout = Output(Vec(c.numout,Bool()))
  
}


abstract class PWM(busWidthBytes: Int, c: PWMParams)(implicit p: Parameters)
    extends IORegisterRouter(
      RegisterRouterParams(
        name = "PWM",
        compat = Seq("sifive,pwm0"),
        base = c.address,
        beatBytes = busWidthBytes),
      new PWMPortIO(c))
    with HasInterruptSources {

  def nInterrupts = 1
 

  lazy val module = new LazyModuleImp(this) {

   val basetest = Vec(Seq.fill(c.numout){ Module(new PWMModule(32)).io })          

   val period = Reg(Vec(c.numout, UInt()))      

   val duty = RegInit(Vec(Seq.fill(c.numout)(1.U(32.W))))      
                                          
   val kill = RegInit(VecInit(Seq.fill(c.numout){false.B}))     

   val master_enable = RegInit(false.B)

   val numpulse = Reg(Vec(c.numout, UInt()))

   val align = Reg(Vec(c.numout, UInt()))

   val shift = Reg(Vec(c.numout, UInt()))



 for(i <-0 until c.numout){

  port.pwmout(i)  := basetest(i).pwmout
  basetest(i).period := period(i)
  basetest(i).duty := duty(i)
  basetest(i).align := align(i)
  basetest(i).shift := shift(i)
  basetest(i).numpulse := numpulse(i)
  basetest(i).kill := kill(i)
  basetest(i).master_enable := master_enable                                               
  

 }                                                                    

 val tupleperiod = period.zipWithIndex.map { case (period, i) =>
       (0x00 + (i * 0x04)) -> Seq(RegField(32,period)) }

 val tupleduty = duty.zipWithIndex.map { case (dut, i) =>
       (0x00 + c.numout*4 + (i * 0x04)) -> Seq(RegField(32,dut))  }

 val tuplenumpulse = numpulse.zipWithIndex.map { case (num, i) =>
       (0x00 + c.numout*4*2 + (i * 0x04)) -> Seq(RegField(32,num))  }

 val tuplealign = align.zipWithIndex.map { case (ali, i) =>
       (0x00 + c.numout*4*3 + (i * 0x04)) -> Seq(RegField(32,ali))  }  

 val tupleshift= shift.zipWithIndex.map { case (shf, i) =>
       (0x00 + c.numout*4*4 + (i * 0x04)) -> Seq(RegField(32,shf))  }

 val tuplekill= kill.zipWithIndex.map { case (kl, i) =>
       (0x00 + c.numout*4*5 +  (i * 0x04)) -> Seq(RegField(32,kl))  }




  regmap(tupleperiod :_*)
  regmap( tupleduty :_*)
  regmap(tuplealign :_*)
  regmap( tupleshift :_*)                                                    
  regmap(tuplenumpulse :_*)
  regmap(tuplekill :_*)
  regmap(0x0900 -> Seq(RegField(1,master_enable)))

  }
}


class TLPWM(busWidthBytes: Int, params: PWMParams)(implicit p: Parameters)
  extends PWM(busWidthBytes, params) with HasTLControlRegMap


case class PWMAttachParams(
  pwm: PWMParams,
  controlBus: TLBusWrapper,
  intNode: IntInwardNode,
  blockerAddr: Option[BigInt] = None,
  mclock: Option[ModuleValue[Clock]] = None,
  mreset: Option[ModuleValue[Bool]] = None,
  controlXType: ClockCrossingType = NoCrossing,
  intXType: ClockCrossingType = NoCrossing)
  (implicit val p: Parameters)

object PWM {
  val nextId = { var i = -1; () => { i += 1; i} }

  def attach(params: PWMAttachParams): TLPWM = {
    implicit val p = params.p
    val name = s"pwm_${nextId()}"
    val cbus = params.controlBus
    val pwm = LazyModule(new TLPWM(cbus.beatBytes, params.pwm))
    pwm.suggestName(name)
    cbus.coupleTo(s"device_named_$name") { bus =>
      val blockerNode = params.blockerAddr.map(BasicBusBlocker(_, cbus, cbus.beatBytes, name))
      (pwm.controlXing(params.controlXType)
        := TLFragmenter(cbus)
        := blockerNode.map { _ := bus } .getOrElse { bus })
    }
    params.intNode := pwm.intXing(params.intXType)
    InModuleBody { pwm.module.clock := params.mclock.map(_.getWrappedValue).getOrElse(cbus.module.clock) }
    InModuleBody { pwm.module.reset := params.mreset.map(_.getWrappedValue).getOrElse(cbus.module.reset) }

    pwm
  }

  def attachAndMakePort(params: PWMAttachParams): ModuleValue[PWMPortIO] = {
    val pwm = attach(params)
    val pwmNode = pwm.ioNode.makeSink()(params.p)
    InModuleBody { pwmNode.makeIO()(ValName(pwm.name)) }
  }




}





