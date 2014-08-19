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
import org.apache.hadoop.io.Text;

/**
 * UDFArrayDeref
 *
 * UDF to dereference array elements... work around for Hive's
 * FAILED: Error in semantic analysis: line 33:27 Non Constant Expressions for Array Indexes not Supported device_uid
 */
@Description(name = "array_offset",
	     value = "_FUNC_(array<text>, int) - return the given offset in the array")
public class UDFArrayDeref extends UDF {

    private Text result = new Text();

    public Text evaluate(List<String> array, int pos) {
	result.set(array.get(pos));
	return result;
    }
}
