package thinkbig.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.hive.ql.udf.UDFUnixTimeStamp;
import org.apache.hadoop.hive.serde2.io.TimestampWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;

/**
 * Wraps the Hive built-in unix timestamp UDF, providing only its deterministic evaluate methods. Useful for partition pruning.
 */
@UDFType(deterministic = true)
@Description(
        name = "DetUnixTimestamp",
        value = "Wraps the Hive built-in unix timestamp UDF, providing only it's deterministic evaluate methods. Useful for partition pruning.")
public class DetUnixTimestamp extends UDF {

    private final UDFUnixTimeStamp wrapped = new UDFUnixTimeStamp();

    public LongWritable evaluate( Text dateText ) {
        return wrapped.evaluate( dateText );
    }

    public LongWritable evaluate( Text dateText, Text patternText ) {
        return wrapped.evaluate( dateText, patternText );
    }

    public LongWritable evaluate( TimestampWritable i ) {
        return wrapped.evaluate( i );
    }
}
