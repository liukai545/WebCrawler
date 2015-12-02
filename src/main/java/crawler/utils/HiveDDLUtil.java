package crawler.utils;

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
import org.apache.hive.hcatalog.common.HCatUtil;
import org.apache.hive.hcatalog.data.schema.HCatSchema;
import org.apache.thrift.TException;

import java.util.*;

/**
 * Created by liukai on 2015/12/2.
 */
public class HiveDDLUtil {
    public static boolean createTabe(String dbName, String tblName, HCatSchema schema) {
        HiveMetaStoreClient client = null;
        try {
            HiveConf hiveConf = HCatUtil.getHiveConf(new Configuration());
            client = HCatUtil.getHiveClient(hiveConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (client.tableExists(dbName, tblName)) {
                client.dropTable(dbName, tblName);
            }
        } catch (TException e) {
            e.printStackTrace();
        }
        List<FieldSchema> fields = HCatUtil.getFieldSchemaList(schema.getFields());
        System.out.println(fields);
        Table table = new Table();
        table.setDbName(dbName);
        table.setTableName(tblName);

        StorageDescriptor sd = new StorageDescriptor();
        sd.setCols(fields);
        table.setSd(sd);
        sd.setInputFormat(RCFileInputFormat.class.getName());
        sd.setOutputFormat(RCFileOutputFormat.class.getName());
        sd.setParameters(new HashMap<String, String>());
        sd.setSerdeInfo(new SerDeInfo());
        sd.getSerdeInfo().setName(table.getTableName());
        sd.getSerdeInfo().setParameters(new HashMap<String, String>());
        sd.getSerdeInfo().getParameters().put(serdeConstants.SERIALIZATION_FORMAT, "1");
        sd.getSerdeInfo().setSerializationLib(org.apache.hadoop.hive.serde2.columnar.ColumnarSerDe.class.getName());
        Map<String, String> tableParams = new HashMap<String, String>();
        table.setParameters(tableParams);
        try {
            client.createTable(table);
            System.out.println("Create table successfully!");
            return true;
        } catch (TException e) {
            e.printStackTrace();
            return false;
        } finally {
            client.close();
        }
    }
}


