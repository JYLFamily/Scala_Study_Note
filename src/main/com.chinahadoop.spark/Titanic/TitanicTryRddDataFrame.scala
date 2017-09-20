package Titanic

import org.apache.spark.sql.SparkSession

object TitanicTryRddDataFrame {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
     .builder
     .appName("TitanicTryRddDataFrame")
     .getOrCreate()

    case class Sample(PassengerId: Double,
                      Survived: Double,
                      Pclass: Double,
                      firstName: String,
                      lastName: String,
                      Sex: String,
                      Age: Double,
                      SibSp: Double,
                      Parch: Double,
                      Ticket: String,
                      Fare: Double,
                      Cabin: String,
                      Embarked: String)

    import spark.implicits._
    val trainRdd = spark.sparkContext.textFile("data/Titanic/train.csv") // textFile() 返回 RDD[String]
      .map(string => string.split(","))
      .filter(string => (string(0).toInt >= 48) && (string(0).toInt <= 57)) // 过滤 header
      .map(stringSplit => {
        var features = stringSplit.toBuffer
        if (features.length < 13) features += "null"
        for (i <- 0 until features.length) {
          if (features(i) == "") features(i) == "null"
        }
        features.toArray
    })
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
          stringSplit(12))
    })

    val trainDataFrame = trainRdd.toDF()

    trainDataFrame.show()

    spark.stop()
  }
}
