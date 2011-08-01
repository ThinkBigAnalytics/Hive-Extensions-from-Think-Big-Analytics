**
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

public class KeywordBasedMatch extends MatchAlgorithm {

  public boolean match(String text, String pattern) {
	System.out.println(text + ":" + pattern);  
    return text.indexOf(pattern) != -1;
  }
  
  public int countMatch(String text, String pattern) {
	  int index = 0, count = 0;
	  while (true) {
		  if ((index =text.indexOf(pattern, index)) != -1) {
			  ++count;
			  index += pattern.length();
		  }
		  else {
			  return count;
		  }
	  }
  }
}
