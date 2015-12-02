package crawler.common

import org.apache.hadoop.hive.serde2.typeinfo.TypeInfoFactory
import org.apache.hive.hcatalog.data.schema.{HCatFieldSchema, HCatSchema}
import org.apache.spark.sql.types._
import scala.collection.JavaConversions.asScalaBuffer

import scala.collection.mutable

/**
  * Created by liukai on 2015/12/2.
  */
object StructTypeAndHCatSchemaMapping {
  def convertStructTypeToHCatSchema(st: StructType): HCatSchema = {
    val fieldSchemaList = st.fields.map(st => st match {
      case data if data.dataType.isInstanceOf[ByteType] => new HCatFieldSchema(data.name, TypeInfoFactory.byteTypeInfo, null)
      case data if data.dataType.isInstanceOf[ShortType] => new HCatFieldSchema(data.name, TypeInfoFactory.shortTypeInfo, null)
      case data if data.dataType.isInstanceOf[IntegerType] => new HCatFieldSchema(data.name, TypeInfoFactory.intTypeInfo, null)
      case data if data.dataType.isInstanceOf[LongType] => new HCatFieldSchema(data.name, TypeInfoFactory.longTypeInfo, null)
      case data if data.dataType.isInstanceOf[FloatType] => new HCatFieldSchema(data.name, TypeInfoFactory.floatTypeInfo, null)
      case data if data.dataType.isInstanceOf[DoubleType] => new HCatFieldSchema(data.name, TypeInfoFactory.doubleTypeInfo, null)
      case data if data.dataType.isInstanceOf[DecimalType] => new HCatFieldSchema(data.name, TypeInfoFactory.decimalTypeInfo, null)
      case data if data.dataType.isInstanceOf[StringType] => new HCatFieldSchema(data.name, TypeInfoFactory.stringTypeInfo, null)
      case data if data.dataType.isInstanceOf[BinaryType] => new HCatFieldSchema(data.name, TypeInfoFactory.binaryTypeInfo, null)
      case data if data.dataType.isInstanceOf[BooleanType] => new HCatFieldSchema(data.name, TypeInfoFactory.booleanTypeInfo, null)
      case data if data.dataType.isInstanceOf[DateType] => new HCatFieldSchema(data.name, TypeInfoFactory.dateTypeInfo, null)
      case data if data.dataType.isInstanceOf[TimestampType] => new HCatFieldSchema(data.name, TypeInfoFactory.timestampTypeInfo, null)
      // 未匹配类型如何定义??
      case _ => null
    })
    val javaFieldShcemaList = new java.util.ArrayList[HCatFieldSchema](fieldSchemaList.size)
    for (i <- 0 until fieldSchemaList.size) {
      javaFieldShcemaList.add(fieldSchemaList(i))
    }
    new HCatSchema(javaFieldShcemaList)
  }


  def convertHCatSchemaToStructType(hCatSchema: HCatSchema) = {
    val fields: scala.collection.mutable.Buffer[HCatFieldSchema] = hCatSchema.getFields
    val structFields: mutable.Buffer[StructField] = for (hCatFieldSchema <- fields) yield {
      hCatFieldSchema.getTypeInfo match {
        case TypeInfoFactory.binaryTypeInfo => StructField(hCatFieldSchema.getName, BinaryType)
        case TypeInfoFactory.booleanTypeInfo => StructField(hCatFieldSchema.getName, BooleanType)
        case TypeInfoFactory.byteTypeInfo => StructField(hCatFieldSchema.getName, ByteType)
        //case TypeInfoFactory.charTypeInfo => StructField(hCatFieldSchema.getName, ByteType)
        case TypeInfoFactory.dateTypeInfo => StructField(hCatFieldSchema.getName, DateType)
        //case TypeInfoFactory.decimalTypeInfo => StructField(hCatFieldSchema.getName, DecimalType)
        case TypeInfoFactory.doubleTypeInfo => StructField(hCatFieldSchema.getName, DoubleType)
        case TypeInfoFactory.floatTypeInfo => StructField(hCatFieldSchema.getName, FloatType)
        case TypeInfoFactory.intTypeInfo => StructField(hCatFieldSchema.getName, IntegerType)
        case TypeInfoFactory.longTypeInfo => StructField(hCatFieldSchema.getName, LongType)
        case TypeInfoFactory.shortTypeInfo => StructField(hCatFieldSchema.getName, ShortType)
        case TypeInfoFactory.stringTypeInfo => StructField(hCatFieldSchema.getName, StringType)
        case TypeInfoFactory.timestampTypeInfo => StructField(hCatFieldSchema.getName, TimestampType)
        case _ => println("kong"); null
        //case TypeInfoFactory.varcharTypeInfo => StructField(hCatFieldSchema.getName, StringType)
      }
    }
    StructType(structFields)
  }
}
