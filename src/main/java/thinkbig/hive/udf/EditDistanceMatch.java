package thinkbig.hive.udf;

public class EditDistanceMatch extends MatchAlgorithm {

  protected static final double MAX_EDIT_MATCH_FRAC = 0.3;  // threshold, at most 30% mismatch

   public int EditDistance(String a, String b) {
    int m = a.length(), n = b.length();
    if (n == 0) {
      return m;
    }
    if (m == 0) {
      return n;
    }
    int[][] tbl = new int[m + 1][n + 1];
    for (int i = 0; i <= m; ++i) {
      tbl[i][0] = i;
    }
    for (int j = 0; j <= n; ++j) {
      tbl[0][j] = j;
    }
    for (int i = 1; i <= m; ++i) {
      for (int j = 1; j <= n; ++j) {
        tbl[i][j] = Math.min(
            tbl[i - 1][j - 1] + ((a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1),
            Math.min(tbl[i - 1][j] + 1, tbl[i][j - 1] + 1));
      }
    }
    return tbl[m][n];
  }

  public boolean match(String text, String pattern) {
    String[] texts = text.split("\\s+");
	int minDist = 9999;
	for (int i = 0; i < texts.length; ++i) {
		int dist = EditDistance(texts[i], pattern);
		if (dist < minDist) {
			minDist = dist;
		}
	}
    System.out.println(minDist);
    return minDist / (1.0 * pattern.length()) < MAX_EDIT_MATCH_FRAC;
  }
  
  public int countMatch(String text, String pattern)  {
	return match(text, pattern) ? 1 : 0;
  }
}
