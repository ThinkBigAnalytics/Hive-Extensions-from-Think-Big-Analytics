package thinkbig.hive.udf;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.io.Text;

/**
 * Purpose: Return the rank based on the key value. A change in the key value will reset the rank to zero. Usage: Top 3 name value pairs:
 * select name, value from (select name, value, rank(name) rank from mytable order by name,value) A where rank < 3;
 */
@UDFType(deterministic = false)
@Description(name = "Rank", value = "This function will increment and return a counter since the last key change")
public final class Rank extends UDF {

    private long counter = 0;
    private String last_key;
    private long last_longkey;

    public long evaluate( final Text key ) {
        return null == key ? 0 : evaluate( key.toString() );
    }

    public long evaluate( final String key ) {
        if (null == key) {
            return 0;
        }
        if (!key.equalsIgnoreCase( this.last_key )) {
            this.counter = 0;
            this.last_key = key;
        }
        return this.counter++;
    }

    public long evaluate( final long key ) {
        if (key != last_longkey) {
            this.counter = 0;
            this.last_longkey = key;
        }
        return this.counter++;
    }

    public String getDisplayString() {
        return "Simple rank function";
    }

}
