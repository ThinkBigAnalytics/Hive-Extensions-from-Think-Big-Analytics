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

import java.util.*;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.IntWritable;

/**
 * UDFArrayOffset
 *
 * Note that Hive arrays are ZERO-based, unlike other SQL dialects...
 */
@Description(name = "array_offset",
	     value = "_FUNC_(array<text>, text) - return first offset of text in array (what position matches) or -1 if not in array")
public class UDFArrayOffset extends UDF {

    private IntWritable result = new IntWritable();

    public IntWritable evaluate(List<String> array, String sought) {

	for (int i=0; i<array.size(); i++) {
	    if (sought.equals(array.get(i))) {
		result.set(i);
		return result;
	    }
	}

	result.set(-1);
	return result;
    }
}
