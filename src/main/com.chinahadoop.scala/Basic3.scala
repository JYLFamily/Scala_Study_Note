class Basic3 {

}

object Basic3 extends App {
  // 包在任何地方都可以引入、作用域至该语句所在块的结束
  // 重命名引入成员
  import scala.util.{Random => R}
  R.nextInt()
  println("------------------------------------")
  import scala.math._
  import scala.math.{random => _}
  println(random())
  println(abs(-1))
}
