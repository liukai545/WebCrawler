package crawler.datahandling.sparksql

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.{SparkContext, SparkConf}

/**
  * load from CSV And save to Hive using spark SQL
  * Created by liukai on 2015/11/29.
  */
class CSVReader(path: String) {
  val schemaString = "url author title labels question content imgLinks"

  val sc = new SparkContext(new SparkConf())
  sc.hadoopConfiguration.set("textinputformat.record.delimiter", """@@@@""")
  val sqlContext = new HiveContext(sc)

  import sqlContext.implicits._

  def load(): Unit = {
    val schema =
      StructType(
        schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))
    val map1: RDD[Row] = sc.textFile(path).map(_.split("\\*\\#\\*\\#")).map(p => Row(p(0), p(1), p(2), p(3), p(4), p(5), p(6)))
    val map = map1
    val dataFrame = sqlContext.createDataFrame(map, schema)

    dataFrame.registerTempTable("answer")

    val results: DataFrame = sqlContext.sql("select title from answer")
    results.map(_ (0)).collect().foreach(println)

  }

  def save() = {

  }
}

object CSVReader {
  def main(args: Array[String]) {
    val path = args(0)
    val reader: CSVReader = new CSVReader(path)
    reader.load()
  }
}
