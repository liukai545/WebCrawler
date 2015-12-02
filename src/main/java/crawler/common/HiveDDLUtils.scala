package crawler.common

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hive.conf.HiveConf
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient
import org.apache.hadoop.hive.metastore.api.{FieldSchema, SerDeInfo, StorageDescriptor, Table}
import org.apache.hadoop.hive.ql.io.{RCFileOutputFormat, RCFileInputFormat}
import org.apache.hadoop.hive.serde.serdeConstants
import org.apache.hadoop.hive.serde2.columnar.{BytesRefArrayWritable, ColumnarSerDe}
import org.apache.hadoop.io.LongWritable
import org.apache.hive.hcatalog.common.HCatUtil
import org.apache.hive.hcatalog.data.schema.HCatSchema
import org.apache.thrift.TException
import java.util

/**
  * Created by liukai on 2015/12/2.
  */
object HiveDDLUtils {
  def createTable2(dbName: String, tblName: String, schema: HCatSchema) {
    var client: HiveMetaStoreClient = null
    try {
      val hiveConf: HiveConf = HCatUtil.getHiveConf(new Configuration)
      client = HCatUtil.getHiveClient(hiveConf)
    }
    catch {
      case e: Exception => {
        e.printStackTrace
      }
    }
    try {
      if (client.tableExists(dbName, tblName)) {
        client.dropTable(dbName, tblName)
      }
    }
    catch {
      case e: TException => {
        e.printStackTrace
      }
    }
    val fields: util.List[FieldSchema] = HCatUtil.getFieldSchemaList(schema.getFields)
    System.out.println(fields)
    val table: Table = new Table
    table.setDbName(dbName)
    table.setTableName(tblName)
    val sd: StorageDescriptor = new StorageDescriptor
    sd.setCols(fields)
    table.setSd(sd)
    sd.setInputFormat(classOf[RCFileInputFormat[_ <: LongWritable, _ <: BytesRefArrayWritable]].getName)
    sd.setOutputFormat(classOf[RCFileOutputFormat].getName)
    sd.setParameters(new util.HashMap[String, String])
    sd.setSerdeInfo(new SerDeInfo)
    sd.getSerdeInfo.setName(table.getTableName)
    sd.getSerdeInfo.setParameters(new util.HashMap[String, String])
    sd.getSerdeInfo.getParameters.put(serdeConstants.SERIALIZATION_FORMAT, "1")
    sd.getSerdeInfo.setSerializationLib(classOf[ColumnarSerDe].getName)
    val tableParams: util.Map[String, String] = new util.HashMap[String, String]
    table.setParameters(tableParams)
    try {
      client.createTable(table)
      System.out.println("Create table successfully!")
    }
    catch {
      case e: TException => {
        e.printStackTrace
        return
      }
    } finally {
      client.close
    }
  }
}
