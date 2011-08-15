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

public class LCStrMatch extends MatchAlgorithm { // Longest common substring match, an approximate match technique

  private static final double MIN_LCS_MATCH_FRAC = 0.7;	// at least 70% match

  public boolean match(String text, String pattern) {
    String match = LCStr(text, pattern);
    System.out.println(match);
    return match.length() / (1.0 * pattern.length()) >= MIN_LCS_MATCH_FRAC;
  }

  public String LCStr(String a, String b) {
    int m = a.length(), n = b.length();
    if (m <= 0 || n <= 0) {
      return null;
    }
    int[][] tbl = new int[m][n];
    for (int i = 0; i < m; ++i) {
      tbl[i][0] = (a.charAt(i) == b.charAt(0)) ? 1 : 0;
    }
    for (int j = 0; j < n; ++j) {
      tbl[0][j] = (a.charAt(0) == b.charAt(j)) ? 1 : 0;
    }
    int max = 0, imax = 0;
    for (int i = 1; i < m; ++i) {
      for (int j = 1; j < n; ++j) {
        tbl[i][j] = (a.charAt(i) == b.charAt(j)) ? tbl[i - 1][j - 1] + 1 : 0;
        if (tbl[i][j] > max) {
          max = tbl[i][j];
          imax = i;
        }
      }
    }
    return max > 0 ? a.substring(imax - max + 1, imax + 1) : null;
  }
  
  public int countMatch(String text, String pattern) {
	return match(text, pattern) ? 1 : 0;
  }
}
