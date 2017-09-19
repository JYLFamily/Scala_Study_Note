package Basic

class Basic4 {

}

object Basic4 extends App {
  var variable: Int = 0
  // _ 类似 default
  val result1 = variable match {
    case 0 => "zero"
    case 1 => "one"
    case 2 => "two"
    case _ => "other number"
  }
  // _ 类似 default
  val result2 = variable match {
    case i if (i == 0) => "zero"
    case i if (i == 1) => "one"
    case i if (i == 2) => "two"
    case _ => "other number"
  }
  // Any 接受任何类型的参数
  def result3(obj: Any): Unit = {
    obj match {
      case i: Int => println("Int")
      case i: String => println("String")
      case _ => println("Other type")
    }
  }

  println("result1: " + result1)
  println("result2: " + result2)
  result3(variable)
}
