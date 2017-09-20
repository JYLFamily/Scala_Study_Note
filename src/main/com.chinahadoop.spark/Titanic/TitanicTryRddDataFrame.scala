package Titanic

import org.apache.spark.sql.SparkSession

/**
  * RDD 转 DataFrame
  * 1、case class 的定义需要放在方法的作用域之外 ( 即 java 的成员变量位置 ） 我直接单写了一个 .scala 文件
  * 2、import spark.implicits._ 写在初始化 spark 变量之后
  * */

object TitanicTryRddDataFrame {
  def main(args: Array[String]): Unit = {

    val spark = SparkSession
     .builder
     .master("local")
     .appName("TitanicTryRddDataFrame")
     .getOrCreate()

    import spark.implicits._

    val trainRdd = spark.sparkContext.textFile("data/Titanic/train.csv") // textFile() 返回 RDD[String]
      .filter(string => (string(0).toInt >= 48) && (string(0).toInt <= 57)) // 过滤 header
      .map(string => {
        val stringSplit = string.split(',').toBuffer
        if (stringSplit.length < 13) stringSplit += "null" // 若最后一个特征缺失 , 填充 null
        var features: String = new String("")
        for (i <- 0 until stringSplit.length) {
          if (stringSplit(i) == "") stringSplit(i) = "null" // 如果是缺失值变为 null 注 Titanic 文件中缺失值是 null
          features = features + stringSplit(i)
          if (i != stringSplit.length - 1) features = features + ","
      }
      features
    }).filter(string => !string.contains("null"))
      .map(string => string.split(","))
      .map(stringSplit => {
        Sample(stringSplit(0).trim.toDouble,
          stringSplit(1).trim.toDouble,
          stringSplit(2).trim.toDouble,
          stringSplit(3),
          stringSplit(4),
          stringSplit(5),
          stringSplit(6).trim.toDouble,
          stringSplit(7).trim.toDouble,
          stringSplit(8).trim.toDouble,
          stringSplit(9),
          stringSplit(10).trim.toDouble,
          stringSplit(11),
          stringSplit(12))})

    val trainDataFrame = trainRdd.toDF()

    spark.stop()
  }
}
