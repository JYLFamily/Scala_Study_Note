package Basic

class Basic {

}

object Basic extends App {
  val ma1 = new myAccount
  ma1.save
  println("------------------------------------")
  // 前提 terminalLogger extends logger
  val ma2 = new myAccount with terminalLogger
  ma2.save
}