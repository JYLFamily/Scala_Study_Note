package LinZiYu.Rdd

import org.apache.spark.sql.SparkSession

object KeyValueRdd {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder
      .master("local[1]")
      .appName("KeyValueRdd")
      .getOrCreate()

    /**
      * 自己实现不具有推广性
      * */
    // LinZiYu.RDD[(String, Int)] k-v Rdd k string v Int
    val rdd = spark.sparkContext.makeRDD(Array(("spark",2),("hadoop",6),("hadoop",4),("spark",6)))
    val rddOutputRdd = rdd.reduceByKey((intOne, intTwo) => intOne + intTwo)
      .mapValues(int => int/2)

    println("------------------------------------")
    rddOutputRdd.foreach(println)

    /**
      * 老师实现
      * */
    val rddTeacher = spark.sparkContext.makeRDD(Array(("spark",2),("hadoop",6),("hadoop",4),("spark",6)))

    val rddOutputRddTeacher = rddTeacher.mapValues(int => (int, 1))
      .reduceByKey((tupleOne, tupleTwo) => (tupleOne._1 + tupleTwo._1, tupleOne._2 + tupleOne._2))
      .mapValues(tuple => tuple._1/tuple._2)

    println("------------------------------------")
    rddOutputRddTeacher.foreach(println)
  }
}
