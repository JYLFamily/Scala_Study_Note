class applyTest {
  println("class applyTest")

  def apply(): Unit = {
    println("class applyTest apply")
  }
}

object applyTest {
  def apply(): applyTest = {
    new applyTest()
  }

  def staticMethod(): Unit = {
    println("staticMethod")
  }
}

class Basic2 {

}

object Basic2 extends App{
  // 单例对象 , 静态方法
  applyTest.staticMethod()
  println("------------------------------------")
  // 调用 object applyTest 中的 apply() 方法 ， apply() applyTest() 都要加括号
  // 返回 "class applyTest" 调用了 class applyTest 的主构造器
  val at = applyTest()
  // 调用 class applyTest 的 apply 方法
  at()
  println("------------------------------------")
  import scala.util.{Random => r}
  println(r.nextInt())
}