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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexMatch extends MatchAlgorithm {
  
  Pattern pattern;	
	
  public boolean match(String text, String pattern) {
	  this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	  Matcher matcher = this.pattern.matcher(text);
      return matcher.find();
  }
  
  public int countMatch(String text, String pattern) {
	  this.pattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
	  Matcher matcher = this.pattern.matcher(text);
	  int count = 0;
	  while (matcher.find()) {
		 //System.out.println("group: " + matcher.group());
		 ++count;
	  }
	  return count;
  }
}
