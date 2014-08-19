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

public class LCSMatch extends MatchAlgorithm { // Longest common subsequence match, an approximate match technique

  private static final double MIN_LCS_MATCH_FRAC = 0.7;	// at least 70% match

  public boolean match(String text, String pattern) {
    int len = LCS(text, pattern);
    //System.out.println("length: " + len);
    return len / (1.0 * pattern.length()) >= MIN_LCS_MATCH_FRAC;
  }

  public int LCS(String a, String b) {
    int m = a.length(), n = b.length();
    if (m <= 0 || n <= 0) {
      return 0;
    }
    int[][] tbl = new int[m][n];
    for (int i = 0; i < m; ++i) {
      tbl[i][0] = 0;
    }
    for (int j = 0; j < n; ++j) {
      tbl[0][j] =  0;
    }
    for (int i = 1; i < m; ++i) {
      for (int j = 1; j < n; ++j) {
        tbl[i][j] = (a.charAt(i) == b.charAt(j)) ? tbl[i - 1][j - 1] + 1 : Math.max(tbl[i - 1][j], tbl[i][j - 1]);
      }
    }
    return tbl[m - 1][n - 1];
  }
  
  public int countMatch(String text, String pattern) {
	return match(text, pattern) ? 1 : 0;
  }
}
