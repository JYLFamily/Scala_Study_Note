package Advance

//class Person(val name: String)
//
//class Student(name: String) extends Person(name)

// covariant 协变
// 类 X 对象的继承关系同传入对象的类继承关系相同
//class X[+T](val x: T)

// contravariant 逆变
// 类 Y 对象的继承关系同传入对象的类继承关系相反
//class Y[-T](val y: T)

// 协变用来做方法参数 , 逆变用来做方法返回值 , 大概就是下面注释的意思
//class Z[-T](val z: T) {
//  def Z(z: T): +R = {
//
//  }
//}

object TypeContraints {
  def main(args: Array[String]): Unit = {

    // xOne 对象是 xTwo 对象的子类
    // 对象是否有继承关系 , 是否是这样 new X(Person("people")) 、new X(Student("student")) 是两个新类 , 两个新类的继承关系与传入对象
    // /类的继承关系相同
//    val xOne = new X(new Person("people"))
//    val xTwo = new X(new Student("student"))

    // yOne 对象是 yTwo 对象的父类
//    val yOne = new Y(new Person("people"))
//    val yTwo = new Y(new Student("student"))
  }
}
