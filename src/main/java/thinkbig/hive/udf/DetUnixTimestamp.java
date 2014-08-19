/**
 * Copyright (C) 2010-2014 Think Big Analytics, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. See accompanying LICENSE file.
 */
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
