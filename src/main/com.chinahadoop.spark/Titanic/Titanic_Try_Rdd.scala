package Titanic

import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.ml.linalg.Vectors
import scala.util.control.Breaks._

object Titanic_Try_Rdd {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("Titanic_Try_Rdd")
      .getOrCreate()

    /**
      * 数据导入 , 通过读取文本的方式
      *
      * 注意数据有以下一个问题会导致程序出错 , 均缺少最后一个特征 , 导致 string.split(",") 后与别的样本特征数量不一致 , 以下程序是删除了这两行的结果
      * 62,1,1,"Icard, Miss. Amelie",female,38,0,0,113572,80,B28,
      * 830,1,1,"Stone, Mrs. George Nelson (Martha Evelyn)",female,62,0,0,113572,80,B28,
      * */


   // +--------------------+
   // |               value|
   // +--------------------+
   // |PassengerId,Survi...|
   // |1,0,3,"Braund, Mr...|
   // |2,1,1,"Cumings, M...|
   // +--------------------+

    val train = spark.read
     .text("C:/Users/Dell/Desktop/week/train.csv")
     .rdd // 返回 RDD[Row] RDD 带泛型 RDD[T] 这里的 T 是 Row

    // train RDD[Row] 每个元素都是 Row 类型的 RDD , 可以使用 RDD 的很多方法
    // map、reduce、foreach 等等
    // Scala 是静态语言不是动态语言
    val trainPassengerIdLabelFeatures = train.map(row => row.toString())
      .map(string => string.substring(1, string.length-1)) // Row 是类似 [30,0,3,,S] 结构 , 变为 String 后 "[30,0,3,,S]" 需要去掉 "["、"]"
      .filter(string => (string(0).toInt >= 48) && (string(0).toInt <= 57)) // 判断字符串第一个元素是否是数字 , 如果不是过滤掉 , 去掉表头
      .map(string => {
        val stringSplit = string.split(',')
        val passengerId = stringSplit(0) // 一个样本的 passengerId
        val survived = stringSplit(1) // 一个样本的 survived
        var features: String = new String("") // 一个样本的所有 features
        for (i <- 2 until stringSplit.length) {
          if (stringSplit(i) == "") stringSplit(i) = "null" // 如果是缺失值变为 null 注 Titanic 文件中缺失值是 null
          features = features + stringSplit(i)
          if (i != stringSplit.length - 1) features = features + ","
        }
       (passengerId, survived, features)
    }).collect() // 通过 collect() 方法由 RDD[(String, String)] 变为 Array[(String, String)] , RDD[T] 变为 Array[T]

    trainPassengerIdLabelFeatures.foreach(println)

    /**
      * 数据处理 , 分别得到三个 DataFrame
      * */

    println("------------------ Label ------------------")

    // Label
    val trainLabel = trainPassengerIdLabelFeatures.map(tuple => tuple._2)
      .map(string => string.toDouble)

    // Label DenseVector
    val trainLabelVector = Vectors.dense(trainLabel).toDense

    println(s"Positive: ${trainLabelVector.numNonzeros}")
    println(s"Negative: ${trainLabelVector.values.length - trainLabelVector.numNonzeros}")

    // Label DataFrame Array[Double] map 每个元素由 Double => Tuple1[Double] 就能够创建 DataFrame 了
    val trainLabelDataFrame = spark.createDataFrame(trainLabel
        .map(label => Tuple1(label)))
      .toDF("Label")

    trainLabelDataFrame.show()

    println("------------------ Features ------------------")
    // Array[String] String 中既包含 "3" 又包含 "mike" 这种字符串非常麻烦
    val trainFeatures = trainPassengerIdLabelFeatures.map(tuple => tuple._3)
      .filter(string => !string.contains("null"))
      .map(string => { // 删除列
        val stringSplit = string.split(",")
        var features = new String("")
        for (i <- 0 until stringSplit.length) {
          breakable {
            if (i == 1 || i == 2 || i == 7 || i == 9) {
              break()
            }
            features = features + stringSplit(i)
            if (i != stringSplit.length - 1) features = features + ","
          }
        }
        features
      })
      .map(string => {
        var stringSplit = string.split(",")

        stringSplit(1) = stringSplit(1) match {
          case "female" => "0"
          case "male" => "1"
        }

        stringSplit(6) = stringSplit(6) match {
          case "C" => "0"
          case "S" => "1"
          case "Q" => "2"
        }
        stringSplit
      })
      .map(arraryString => {
        var features: Array[Double] = Array(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        for (i <- 0 until arraryString.length) {
          features(i) = arraryString(i).toDouble
        }
        features
      })

    // Array[Array[Double]] map 每个元素由 Array[Double] => Tuple1[Array[Double]]
    val trainFeaturesDataFrame = spark.createDataFrame(trainFeatures
      .map(features => Tuple1(features)))
      .toDF("features")

    trainFeaturesDataFrame.show()
  }
}
