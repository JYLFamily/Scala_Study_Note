package Advance

// 泛型类
// class A[T]
class B[T, S](val paramOne: T, val paramTwo: S)

// 泛型特质
// trait C[T]

// object 没有泛型
object Generics {

  // 泛型函数
  def print[A](content: A): Unit ={
    println(content)
  }

  def main(args: Array[String]): Unit = {
    /**
      * 泛型方法
      * */
    print[String]("JYLFamily")
    print[Int](1992720)
    // 可省略 , scala 类型推断
    print("JYLFamily")
    print(1992720)
    // 不省略 , 进行类型检查 , 下面会报错
    // print[String](1992720)
    // print[Int]("JYLFamily")

    /**
      * 泛型类
      * */
    val valueBOne = new B[String, String]("JJ", "JH")
    // 可省略 , scala 类型推断
    val valueBTwo = new B("JJ", "JH")
    // 不省略 , 进行类型检查 , 下面会报错
    // val valueBThree = new B[String, String](1992, 0270)
  }
}
