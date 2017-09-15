package Titanic

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature._

object Titanic_Try {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("TitanicDataAnalysis")
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

    train = train.select("Survived", "Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked")
    // 删除缺失值的行

    test = test.select("Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked")


    /**
      * 数据预处理
      * */


    // 删除缺失值的行
    train = train.na.drop()
    test = test.na.drop()

    // Sex Embarked OneHotEncoder train
    var indexer = new StringIndexer()
      .setInputCol("Sex")
      .setOutputCol("SexIndex")
      .fit(train)

    var indexed = indexer.transform(train)

    var encoder = new OneHotEncoder()
      .setInputCol("SexIndex")
      .setOutputCol("SexVec")

    train = encoder.transform(indexed)

    indexer = new StringIndexer()
      .setInputCol("Embarked")
      .setOutputCol("EmbarkedIndex")
      .fit(train)

    indexed = indexer.transform(train)

    encoder = new OneHotEncoder()
      .setInputCol("EmbarkedIndex")
      .setOutputCol("EmbarkedVec")

    train = encoder.transform(indexed)

    // Sex Embarked OneHotEncoder test
    indexer = new StringIndexer()
      .setInputCol("Sex")
      .setOutputCol("SexIndex")
      .fit(train.select("Sex")) // 要使用 train.select("Sex") 使用训练集对测试机进行处理

    indexed = indexer.transform(test) // 生成 index 列指代原先的水平

    encoder = new OneHotEncoder()
      .setInputCol("SexIndex")
      .setOutputCol("SexVec")
      .setDropLast(true) // 是否保留最后一列 , 也就是默认 true 是 dummy variable , 设置 false 是 one hot encoder

    test = encoder.transform(indexed)

    indexer = new StringIndexer()
      .setInputCol("Embarked")
      .setOutputCol("EmbarkedIndex")
      .fit(train.select("Embarked"))

    indexed = indexer.transform(test)

    encoder = new OneHotEncoder()
      .setInputCol("EmbarkedIndex")
      .setOutputCol("EmbarkedVec")
      .setDropLast(true)

    test = encoder.transform(indexed)
  }
}
