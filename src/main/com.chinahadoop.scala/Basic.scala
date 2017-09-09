class Basic {

}

trait logger {
  def log(msg: String) {
    println("log: " + msg)
  }
}

trait terminalLogger extends logger {
  // 这里需要加上 override 关键字
  // 说明一下：只有抽象类、trait中的函数可以是没有函数体（函数实现），没有函数体的子类继承覆盖重写可以不加 override 其余情况必须加上 override 关键字 ，索性都加上
  override def log(msg: String) {
    println("terminallog: " + msg)
  }
}

abstract class Account {
  def save
}

class myAccount extends Account with logger {
  override def save: Unit = {
    log("100")
  }
}

object Basic extends App {
  val ma1 = new myAccount
  ma1.save
  println("------------------------------------")
  // 前提 terminalLogger extends logger
  val ma2 = new myAccount with terminalLogger
  ma2.save
}
