package Advance

// Upper Bounds
// T 一定是 Comparable 的子类
// 如果没有  <: Comparable[T] 报错  value compareTo is not a member of type parameter T
class Pair[T <: Comparable[T]](val first: T, val second: T) {
  def smaller(): T ={
    if (first.compareTo(second) < 0)
      first
    else
      second
  }
}

class Person(val name: String)
class Student(name: String) extends Person(name)

// Lower Bounds
// R 一定是 T 的超类
class newPair[T](val first: T, val second: T) {

  def replaceFirst[R >: T](newFirst: R) = new newPair(newFirst, second)

  override def toString: String = {
    s"first: $first" + " " + s"second: $second"
  }
}

// View Bounds
// T <% V 存在 T 到 V 的隐式转换
class viewPair[T <% Comparable[T]] (val first: T, val second: T) {
  def smaller(): T = {
    if (first.compareTo(second) < 0)
      first
    else
      second
  }
}

// Context Bounds
// [T:M] 存在 M[T] 的隐式值

object Bounds {
  def main(args: Array[String]): Unit = {
    /**
      * Upper Bounds
      * */
    println("------------------ Upper Bounds ------------------")
    val p = new Pair("one", "two")
    println(p.smaller())

    // 必须添加隐式转换才可以 , 由 <: 变为 <% 由 Upper Bounds 变为了 View Bounds
    // 原因在于 Int 没有实现 Comparable 接口 , RichInt 实现了 Compareble 接口
    // 由 Int 隐式转换成 RichInt
//    val p2 = new Pair(1, 2)
//    println(p2.smaller())

    /**
      * Lower Bounds
      * */
    println("------------------ Lower Bounds ------------------")
    val s1 = new Student("A")
    val s2 = new Student("B")
    val p1 = new Person("C")

    val np = new newPair(s1, s2)
    println(np.toString)

    println(np.replaceFirst(p1).toString)

    /**
      * View Bounds
      * */
    println("------------------ View Bounds ------------------")
    // 原因在于 Int 没有实现 Comparable 接口 , RichInt 实现了 Compareble 接口
    // 由 Int 隐式转换成 RichInt
    val vp = new viewPair(1, 2)
    println(vp.smaller())

  }
}
