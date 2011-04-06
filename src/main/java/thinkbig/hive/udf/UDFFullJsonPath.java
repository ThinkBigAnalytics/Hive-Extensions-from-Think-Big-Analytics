/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2011 Think Big Analytics.
 */

package thinkbig.hive.udf;

import java.util.*;

import com.jayway.jsonpath.*;

import org.apache.hadoop.hive.ql.exec.Description;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.io.Text;

/**
 * UDFFullJsonPath
 *
 */
@Description(name = "get_json_full",
	     value = "_FUNC_(json_txt, path) - like get_json_object but it allows full json path and in general returns multiple results" )
    public class UDFFullJsonPath extends UDF { //GenericUDTF {

  // An LRU cache using a linked hash map - reused from UDFJson
  static class HashCache<K, V> extends LinkedHashMap<K, V> {

    private static final int CACHE_SIZE = 16;
    private static final int INIT_SIZE = 32;
    private static final float LOAD_FACTOR = 0.6f;

    HashCache() {
      super(INIT_SIZE, LOAD_FACTOR);
    }

    private static final long serialVersionUID = 1;

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
      return size() > CACHE_SIZE;
    }

  }

  static Map<String, JsonPath> jsonCache = new HashCache<String, JsonPath>();

  private ArrayList<Text> result = new ArrayList<Text>();

  /**
   * Extract json object from a json string based on json path specified, and
   * return json string of the extracted json object. It will return null if the
   * input json string is invalid.
   * 
   * @param jsonString
   *          the json string.
   * @param pathString
   *          the json path expression.
   * @return json string or null when an error happens.
   */
  public List<Text> evaluate(String jsonString, String pathString) {

      JsonPath path = jsonCache.get(pathString);
      if (path == null) {
	  path = JsonPath.compile(pathString);
	  jsonCache.put(pathString, path);
      }

      Object res;
      try {
	  res = JsonPath.read(jsonString, pathString);
      } catch (Exception e) {
	  return null;
      }
    if (res == null) return null;
    result.clear();
    if (res instanceof List) {
	for (Object o : (List)res) {
	    // would be more efficient by keeping N cached text's...
	    Text t = new Text();
	    t.set(o.toString());
	    result.add(t);
	}
    } else {
	Text t = new Text();
	t.set(res.toString());
	result.add(t);
    }
    return result;
  }

}
