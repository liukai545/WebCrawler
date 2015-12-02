package crawler.datahandling.sparksql

import crawler.common.StructTypeAndHCatSchemaMapping
import org.apache.hadoop.io.Writable
import org.apache.hive.hcatalog.data.HCatRecord
import org.apache.hive.hcatalog.data.schema.{HCatFieldSchema, HCatSchema}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.{SerializableWritable, SparkConf, SparkContext}
import scala.collection.JavaConversions.asScalaBuffer

/**
  * load from HIVE using hcatalog and deal with spark SQL
  * Created by liukai on 2015/12/2.
  */
class HiveLoader(dbName: String, tbName: String) {
  val sc = new SparkContext(new SparkConf())
  val hiveContext = new HiveContext(sc)

  def load() = {
    var hCatSchema: HCatSchema = new HCatSchema(new java.util.ArrayList[HCatFieldSchema]())
    val pairRDD: RDD[(SerializableWritable[Writable], HCatRecord)] = new Load(dbName, tbName).load(sc, hCatSchema)

    val values: RDD[HCatRecord] = pairRDD.values

    val structType: StructType = StructTypeAndHCatSchemaMapping.convertHCatSchemaToStructType(hCatSchema)

    val rowRDD: RDD[Row] = values.map(record => {
      hCatSchema.getFieldNames.foldLeft[Row](Row()) { (row, str) => Row.merge(row, Row(record.get(str, hCatSchema))) }
    })
    hiveContext.createDataFrame(rowRDD, structType)
  }

  def doSomethingIn(dataFrame: DataFrame) = {
    dataFrame.registerTempTable("wahaha")
    dataFrame.printSchema()
    val result: DataFrame = hiveContext.sql("select title from wahaha")
    result.map(_ (0)).collect().foreach(println)
  }
}

object HiveLoader {
  def main(args: Array[String]): Unit = {
    val dbName = args(0)
    val tbName = args(1)

    val loader: HiveLoader = new HiveLoader(dbName, tbName)
    val load: DataFrame = loader.load()
    loader.doSomethingIn(load)

    loader.sc.stop();
  }
}