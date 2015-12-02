package crawler.common;


import java.io.IOException;

import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hive.hcatalog.data.HCatRecord;
import org.apache.spark.SerializableWritable;

/**
 * Created by liukai on 2015/12/2.
 * copy from https://github.com/kawaa/SparkHCat
 */
class SparkHCatRecordReader extends RecordReader<SerializableWritable<Writable>, HCatRecord>  {
    @SuppressWarnings("rawtypes")
    private final RecordReader<WritableComparable, HCatRecord> reader;

    public SparkHCatRecordReader(RecordReader<WritableComparable, HCatRecord> reader) {
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public SerializableWritable<org.apache.hadoop.io.Writable> getCurrentKey() throws IOException,
            InterruptedException {
        return new SerializableWritable<org.apache.hadoop.io.Writable>(reader.getCurrentKey());
    }

    @Override
    public HCatRecord getCurrentValue() throws IOException,
            InterruptedException {
        return reader.getCurrentValue();
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return reader.getProgress();
    }

    @Override
    public void initialize(InputSplit arg0, TaskAttemptContext arg1)
            throws IOException, InterruptedException {
        reader.initialize(arg0, arg1);
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        return reader.nextKeyValue();
    }

}