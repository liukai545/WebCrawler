package crawler.datahandling.sparksql

import crawler.common.SparkHCatInputFormat
import org.apache.hadoop.io.{Writable}
import org.apache.hive.hcatalog.data.HCatRecord
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema
import org.apache.hive.hcatalog.data.schema.HCatSchema
import org.apache.hive.hcatalog.mapreduce
import org.apache.hive.hcatalog.mapreduce.HCatInputFormat

import org.apache.spark.SerializableWritable
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import scala.collection.JavaConversions.asScalaBuffer

/**
  * Created by liukai on 2015/12/2.
  */
class Load(dbName: String, tbName: String) {


  def load(sparkContext: SparkContext, tableSchema: HCatSchema): RDD[(SerializableWritable[Writable], HCatRecord)] = {
    val f = classOf[SparkHCatInputFormat]
    val k = classOf[org.apache.spark.SerializableWritable[org.apache.hadoop.io.Writable]]
    val v = classOf[org.apache.hive.hcatalog.data.HCatRecord]

    val conf = new org.apache.hadoop.conf.Configuration()
    //conf.set("hive.metastore.uris", "thrift://hdp3:9083");

    HCatInputFormat.setInput(conf, dbName, tbName)
    org.apache.hive.hcatalog.mapreduce.HCatBaseInputFormat.getTableSchema(conf).getFields.foreach(tableSchema.append(_))

    val d: RDD[(SerializableWritable[Writable], HCatRecord)] = sparkContext.newAPIHadoopRDD(conf, f, k, v)
    d


    /*
        val job = Job.getInstance();
        HCatInputFormat.setInput(job.getConfiguration(), dbName, tbName);
    */

    //设置HCatSchema
    /*      for (HCatFieldSchema hCatFieldSchema: HCatInputFormat.getTableSchema(job.getConfiguration()).getFields()) {
          tableSchema.append(hCatFieldSchema);
        }*/

    /*
        val rdd: RDD[(WritableComparable, SerializableWritable[HCatRecord])] = sparkContext.newAPIHadoopRDD(
          job.getConfiguration,
          Class[HCatInputFormat[WritableComparable, SerializableWritable[HCatRecord]]],
          Class[WritableComparable],
          Class[SerializableWritable[HCatRecord]])
        rdd
    */


    /*    var readAllJob = Job.getInstance()
        SerHCatInputFormat.setInput(readAllJob.getConfiguration(), this.dbName, this.tbName)
        sparkContext.newAPIHadoopRDD[NullWritable, SerializableWritable[HCatRecord], SerHCatInputFormat]
        (job.getConfiguration(),
          Class[SerHCatInputFormat[NullWritable, SerializableWritable[HCatRecord]]], Class[NullWritable], Class[SerializableWritable[HCatRecord]])*/


    /*      hCatRecordRDD: JavaPairRDD[WritableComparable, SerializableWritable[HCatRecord]] =
          sparkContext.newAPIHadoopRDD(job.getConfiguration(),
            HCatInputFormat.class, WritableComparable.class,
          SerializableWritable.class).mapToPair(new MapToSerWriHCatRecord());
        return hCatRecordRDD;*/

  }
}

/*
class MapToSerWriHCatRecord

implements PairFunction < Tuple2 < WritableComparable, SerializableWritable >,
WritableComparable, SerializableWritable < HCatRecord >> {
@Override
public Tuple2 < WritableComparable, SerializableWritable < HCatRecord >>
call (Tuple2 < WritableComparable, SerializableWritable > tuple2) throws Exception {
return new Tuple2 < WritableComparable, SerializableWritable < HCatRecord >> (tuple2._1 (), tuple2._2 () );
}
}
*/
