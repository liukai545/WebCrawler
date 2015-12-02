package crawler.datahandling.sparksql

import crawler.common.StructTypeAndHCatSchemaMapping
import crawler.datahandling.sparksql.CSVReader._
import crawler.utils.Logs
import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory
import org.apache.hadoop.io.{NullWritable, WritableComparable}
import org.apache.hive.hcatalog.data.schema.{HCatFieldSchema, HCatSchema}
import org.apache.hive.hcatalog.data.{DefaultHCatRecord, HCatRecord}
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.{SerializableWritable, SparkContext, SparkConf}

/**
  * load from CSV using spark SQL And save to Hive using Hcatalog
  * Created by liukai on 2015/11/29.
  */
class CSVReader(path: String, dbName: String, tbName: String) extends Logs {
  val schemaString = "url author title labels question content imgLinks"
  val structType =
    StructType(
      schemaString.split(" ").map(fieldName => StructField(fieldName, StringType, true)))

  val sc = new SparkContext(new SparkConf())
  sc.hadoopConfiguration.set("textinputformat.record.delimiter", """@@@@""")
  val sqlContext = new HiveContext(sc)

  import sqlContext.implicits._

  def load(): DataFrame = {
    val map: RDD[Row] = sc.textFile(path).map(_.split("\\*\\#\\*\\#")).map(p => Row(p(0), p(1), p(2), p(3), p(4), p(5), p(6)))
    val dataFrame = sqlContext.createDataFrame(map, structType)
    dataFrame

    /*    dataFrame.registerTempTable("answer")

        val results: DataFrame = sqlContext.sql("select title from answer")
        println("select title from answer count" + results.count())
        results.map(_ (0)).collect().foreach(println)*/

    //sqlContext.sql("create table people as select * from answer")
  }

  def save(dataFrame: DataFrame) = {
    val javaRDD: JavaRDD[Row] = dataFrame.toJavaRDD
    new Save(dbName, tbName).save(javaRDD, StructTypeAndHCatSchemaMapping.convertStructTypeToHCatSchema(structType))
  }
}

object CSVReader {
  def main(args: Array[String]) {
    val path = args(0)
    val dbName = args(1)
    val tbName = args(2)

    val reader: CSVReader = new CSVReader(path, dbName, tbName)
    val dataFrame: DataFrame = reader.load()
    reader.save(dataFrame)
    reader.sc.stop()
  }
}
