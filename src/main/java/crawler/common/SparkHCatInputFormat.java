package crawler.common;

import java.io.IOException;

import java.util.List;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.spark.SerializableWritable;
import org.apache.hive.hcatalog.mapreduce.HCatInputFormat;
import org.apache.hive.hcatalog.data.HCatRecord;


/**
 * Created by liukai on 2015/12/2.
 * copy form https://github.com/kawaa/SparkHCat
 */
public class SparkHCatInputFormat extends InputFormat<SerializableWritable<Writable>, HCatRecord> {
    private final HCatInputFormat input;
    public SparkHCatInputFormat() {
        input = new HCatInputFormat();
    }

    @Override
    public RecordReader<SerializableWritable<Writable>, HCatRecord> createRecordReader(
            InputSplit arg0, TaskAttemptContext arg1) throws IOException,
            InterruptedException {
        return new SparkHCatRecordReader(input.createRecordReader(arg0, arg1));
    }

    @Override
    public List<InputSplit> getSplits(JobContext arg0) throws IOException,
            InterruptedException {
        return input.getSplits(arg0);
    }
}