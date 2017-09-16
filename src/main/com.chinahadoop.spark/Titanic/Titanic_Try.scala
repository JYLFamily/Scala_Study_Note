package Titanic

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.ml.feature._
import org.apache.spark.ml.classification.LogisticRegression

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
      .load("data/Titanic/train.csv")

    var test = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("data/Titanic/test.csv")

    var testLabel = spark.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .load("data/Titanic/result.csv")

    train = train.select("Survived", "Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked")
    test = test.join(testLabel, test("PassengerId") === testLabel("PassengerId"), "left_outer")
    test = test.select("Survived", "Pclass", "Sex", "Age", "SibSp", "Parch", "Fare", "Embarked")



    /**
      * 数据预处理
      * */


    // 删除缺失值的行
    train = train.na.drop()
    test = test.na.drop()

    // One hot encoder train test
    var indexer = new StringIndexer()
      .setInputCol("Sex")
      .setOutputCol("SexIndex")
      .fit(train)

    // indexer 由 Sex 生成 SexIndex
    val sexIndexedTrain = indexer.transform(train)
    val sexIndexedTest = indexer.transform(test)

    val sexEncoder = new OneHotEncoder()
      .setInputCol("SexIndex")
      .setOutputCol("SexVec")
    // sexEncoder 由 SexIndex 生成 SexVec
    train = sexEncoder.transform(sexIndexedTrain)
    test = sexEncoder.transform(sexIndexedTest)

    indexer = new StringIndexer()
      .setInputCol("Embarked")
      .setOutputCol("EmbarkedIndex")
      .fit(train)

    val embarkedIndexedTrain = indexer.transform(train)
    val embarkedIndexedTest = indexer.transform(test)

    val embarkedEncoder = new OneHotEncoder()
      .setInputCol("EmbarkedIndex")
      .setOutputCol("EmbarkedVec")

    train = embarkedEncoder.transform(embarkedIndexedTrain)
    test = embarkedEncoder.transform(embarkedIndexedTest)

    //
    val assembler = new VectorAssembler()
      .setInputCols(Array("Pclass","SexVec", "Age", "SibSp", "Parch", "Fare", "EmbarkedVec"))
      .setOutputCol("features")

    train = assembler.transform(train).select("features", "Survived")
    test = assembler.transform(test).select("features", "Survived")

    // Standard train test
    val scaler = new StandardScaler()
      .setInputCol("features")
      .setOutputCol("scaledFeatures")
      .setWithStd(true)
      .setWithMean(true)
      .fit(train)

    train = scaler.transform(train).select("features", "scaledFeatures", "Survived")
    test = scaler.transform(test).select("features", "scaledFeatures", "Survived")

    // pca train test
    val pca = new PCA()
      .setInputCol("scaledFeatures")
      .setOutputCol("pcaedFeatures")
      .setK(3)
      .fit(train)

    // pca 是 PCAModel 类 对象
    // pca.explainedVariance 返回 DenseVector 对象
    // DenseVector.values 返回 Array[Double] 对象
    println("------------------------------------")
    // 前面 PCA().setK() 设置几个主成分 , 这里就会打印出几个主成分方差占比
    pca.explainedVariance.values.foreach(println)
    println("------------------------------------")

    train = pca.transform(train).select("features", "scaledFeatures", "pcaedFeatures","Survived")
    test = pca.transform(test).select("features", "scaledFeatures", "pcaedFeatures", "Survived")

    // Logistics Regression
    // LogisticRegression().fit() 返回 LogisticRegressionModel 对象
    val lr = new LogisticRegression()
      .setFeaturesCol("pcaedFeatures")
      .setLabelCol("Survived")
      .setPredictionCol("SurvivedHat")
      .setStandardization(true)
      .setMaxIter(10)
      .setRegParam(0.3)
      .setElasticNetParam(0.8)
      .fit(train)

    // testSurvivedHat LogisticRegressionSummary 对象
    val testSurvivedHat = lr.evaluate(test)
    var predictions = testSurvivedHat.predictions
//    train.show(5)
//    test.show(5)
    println(testSurvivedHat.featuresCol)
    println(testSurvivedHat.labelCol)
    print(testSurvivedHat.probabilityCol)

    // debug
//    predictions.select("probability").write.format("json").save("data/Titanic/json/")

    spark.stop()
  }
}
