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

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.Text;

import thinkbig.hive.udf.MatchUDFUtil.MatchType;

// break pattern phrase into words and then match
// matchsomewords: if one of the words found => return true
public final class UDFMatchAnyWordWithMetric extends UDF {  
  public BooleanWritable evaluate(final Text text, final Text pattern, Text algoType) {
    if (text == null || pattern == null || algoType == null) { return null; }
    return new BooleanWritable(MatchUDFUtil.match(text.toString(), pattern.toString(), algoType.toString(), MatchType.SomeWords));
  }
}
