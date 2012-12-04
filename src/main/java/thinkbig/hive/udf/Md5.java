package thinkbig.hive.udf;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;
import org.apache.hadoop.io.Text;

/**
 * Purpose: Calculate md5 of the string Usage: Select md5(description) from mytable;
 */
@UDFType(deterministic = true)
@Description(name = "MD5", value = "This function will return the MD5 hash of it's argument.")
public final class Md5 extends UDF {

    public Text evaluate( final Text s ) {
        if (s == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance( "MD5" );
            md.update( s.toString().getBytes() );
            byte[] md5hash = md.digest();
            StringBuilder builder = new StringBuilder();
            for (byte b : md5hash) {
                builder.append( Integer.toString( (b & 0xff) + 0x100, 16 ).substring( 1 ) );
            }
            return new Text( builder.toString() );
        } catch (NoSuchAlgorithmException nsae) {
            System.out.println( "Cannot find digest algorithm" );
            System.exit( 1 );
        }
        return null;
    }

    public String getDisplayString() {
        return "MD5 hash function";
    }
}