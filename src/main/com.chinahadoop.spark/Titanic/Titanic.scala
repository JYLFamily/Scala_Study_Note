package Titanic

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._

object Titanic {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("Titanic")
      .getOrCreate()

    /**
      * 数据导入
      * */

    var train = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("C:/Users/Dell/IdeaProjects/Scala_Study_Note/data/Titanic/train.csv")

    var test = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("C:/Users/Dell/IdeaProjects/Scala_Study_Note/data/Titanic/test.csv")

    /**
      * 数据探索性分析
      * */

    // 查看 DataFrame train 结构
    train.printSchema()

    // 查看前 5 行数据 , 类似 head(5)
    train.show(5)

    // 计算概述统计量 , 类型可以是 numeric 或者 string
    // numeric count mean stddev min max
    // string count
    // 类似 summary()
    train.describe("Sex").show()
    train.describe("Age").show()

    /**
      * 数据预处理
      * */
    train = train.select("Survived", "Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked")
    test = test.select("Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked")

    // train 是 variable 所以能够通过 replacing Sex 列后重新赋值给 train
    // Returns a new Dataset by adding a column or replacing the existing column that has the same name.
    train = train.withColumn("Sex", when(train("Sex") === "male", 1)
      .when(train("Sex") === "female", 0))
    test = test.withColumn("Sex", when(test("Sex") === "male", 1)
      .when(test("Sex") === "female", 0))

    train = train.withColumn("Embarked", when(train("Embarked") === "S", 0)
      .when(train("Embarked") === "C", 1)
      .when(train("Embarked") === "Q", 2))
    test = test.withColumn("Embarked", when(test("Embarked") === "S", 0)
      .when(test("Embarked") === "C", 1)
      .when(test("Embarked") === "Q", 2))

    // Age 特征的缺失值用 30 填充 , 均值填充
    train = train.na.fill(Map("Age" -> 30))
    test = test.na.fill(Map("Age" -> 30))

    println("------------------ mean ------------------")
    print(mean(train("Age")))
    println("------------------ mean ------------------")

    // Embarked 特征使用如下方式过滤 null
    train.filter(train("Embarked").isNotNull).groupBy("Embarked").agg(count("Embarked")).show()
    // Embarked 特征的缺失值用 0 填充 , 众数填充
    train = train.na.fill(Map("Embarked" -> 0))
    test = test.na.fill(Map("Embarked" -> 0))

    spark.stop()
  }
}
