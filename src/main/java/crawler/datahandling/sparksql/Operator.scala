package crawler.datahandling.sparksql

import crawler.common.StructTypeAndHCatSchemaMapping
import org.apache.hadoop.io.Writable
import org.apache.hive.hcatalog.data.HCatRecord
import org.apache.hive.hcatalog.data.schema.{HCatFieldSchema, HCatSchema}
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.sql.types._
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.{SerializableWritable, SparkConf, SparkContext}

import scala.collection.JavaConversions.asScalaBuffer

/**
  * load from HIVE using hcatalog and deal with spark SQL then save to HIVE using hcatalog
  * Created by liukai on 2015/12/2.
  */
object Operator {

  def main(args: Array[String]): Unit = {
    val dbName = args(0)
    val tbName = args(1)

    val sc = new SparkContext(new SparkConf())
    val hiveContext = new HiveContext(sc)

    val hCatSchema: HCatSchema = new HCatSchema(new java.util.ArrayList[HCatFieldSchema]())
    val pairRDD: RDD[(SerializableWritable[Writable], HCatRecord)] = new Load(dbName, tbName).load(sc, hCatSchema)

    val values: RDD[HCatRecord] = pairRDD.values
    val rowRDD: RDD[Row] = values.map(record => {
      hCatSchema.getFieldNames.foldLeft[Row](Row()) { (row, str) => Row.merge(row, Row(record.get(str, hCatSchema))) }
    })

    val structType: StructType = StructTypeAndHCatSchemaMapping.convertHCatSchemaToStructType(hCatSchema)
    val dataFrame = hiveContext.createDataFrame(rowRDD, structType)

    dataFrame.registerTempTable("wahaha")
    dataFrame.printSchema()
    val result: DataFrame = hiveContext.sql("select url from wahaha")
    result.map(_ (0)).collect().foreach(println)

    val javaRDD: JavaRDD[Row] = result.toJavaRDD

    val rSchema: StructType = result.schema


    new Save("liukai", "test").save(javaRDD, StructTypeAndHCatSchemaMapping.convertStructTypeToHCatSchema(rSchema))

    sc.stop();
  }

}