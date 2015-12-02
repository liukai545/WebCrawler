package crawler.datahandling.sparksql;

import crawler.common.HiveDDLUtils;
import iie.udps.common.hcatalog.SerHCatOutputFormat;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.metastore.HiveMetaStoreClient;
import org.apache.hadoop.hive.metastore.api.FieldSchema;
import org.apache.hadoop.hive.metastore.api.SerDeInfo;
import org.apache.hadoop.hive.metastore.api.StorageDescriptor;
import org.apache.hadoop.hive.metastore.api.Table;
import org.apache.hadoop.hive.ql.io.RCFileInputFormat;
import org.apache.hadoop.hive.ql.io.RCFileOutputFormat;
import org.apache.hadoop.hive.serde.serdeConstants;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hive.hcatalog.api.HCatClient;
import org.apache.hive.hcatalog.api.HCatCreateTableDesc;
import org.apache.hive.hcatalog.api.HCatTable;
import org.apache.hive.hcatalog.common.HCatException;
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.hcatalog.data.DefaultHCatRecord;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.hive.hcatalog.data.schema.HCatFieldSchema;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.hive.hcatalog.mapreduce.HCatInputFormat;
import org.apache.hive.hcatalog.mapreduce.OutputJobInfo;
import org.apache.spark.SerializableWritable;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.Row;
import org.apache.thrift.TException;
import scala.Tuple2;


import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by liukai on 2015/12/1.
 */
public class Save {
    private String dbName;
    private String tbName;

    public Save(String dbName, String tbName) {
        this.dbName = dbName;
        this.tbName = tbName;
    }

    public void save(JavaRDD<Row> rdd, HCatSchema hCatSchema) {
        JavaPairRDD<WritableComparable, SerializableWritable<HCatRecord>> writableComparableSerializableWritableJavaPairRDD
                = rdd.mapToPair(new convertToRecord(hCatSchema));

        try {
            Job outputJob = Job.getInstance();
            outputJob.setOutputFormatClass(SerHCatOutputFormat.class);
            outputJob.setOutputKeyClass(WritableComparable.class);
            outputJob.setOutputValueClass(SerializableWritable.class);

            HiveDDLUtils.createTable2(dbName, tbName, hCatSchema);

            SerHCatOutputFormat.setOutput(outputJob, OutputJobInfo.create(dbName, tbName, new HashMap<String, String>()));
            SerHCatOutputFormat.setSchema(outputJob, hCatSchema);


            writableComparableSerializableWritableJavaPairRDD.saveAsNewAPIHadoopDataset(outputJob.getConfiguration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class convertToRecord implements PairFunction<Row, WritableComparable, SerializableWritable<HCatRecord>> {
    HCatSchema hCatSchema;

    public convertToRecord(HCatSchema hCatSchema) {
        this.hCatSchema = hCatSchema;
    }

    public Tuple2<WritableComparable, SerializableWritable<HCatRecord>> call(Row row) throws Exception {
        HCatRecord record = new DefaultHCatRecord(row.size());
        int i = 0;
        for (String fieldName : hCatSchema.getFieldNames()) {
            record.set(fieldName, hCatSchema, row.get(i++));
        }
        return new Tuple2<WritableComparable, SerializableWritable<HCatRecord>>(NullWritable.get(), new SerializableWritable<HCatRecord>(record));
    }
}