package thinkbig.hive.udf;

import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import thinkbig.hive.udf.MatchUDFUtil.MatchType;

/**
         * UnitTestcases for Hive UDFs
         *
         * author: Sandipan Dey
         */
public class TestMatchUDFUtil extends TestCase {
	
	String text;
	
	@Before
	public void setup(){
		text = "lehman,, aksdjfhalskd-jfhasdkljf brothers has been bankrupt. Lehman	now ;.'	brothers mnsdfb,mnsd lehman jasdfvjhasdfadjhfgaf defaullt bank";
	}
	
	@Test
	public void testSplitUsingBreakIterator() {
		   String[] words = MatchUDFUtil.splitSentence("Lehman Brothers Holdings Inc...; (former NYSE ticker symbol LEH) ", Locale.ENGLISH);
		   /*for (String word: words) {
			   System.out.println(word);
		   }*/
		   assertTrue(words.length == 9);
    	}

	@Test
	public void testKeyword() {
		text = "lehman,, aksdjfhalskd-jfhasdkljf brothers has been bankrupt. Lehman	now ;.'	brothers mnsdfb,mnsd lehman jasdfvjhasdfadjhfgaf defaullt bank";
		assertTrue(!MatchUDFUtil.match(text, "lehman brothers", "keyword", MatchType.WholePhrase));  // whole phrase match
		assertTrue(MatchUDFUtil.match(text, "lehman brothers", "keyword", MatchType.AllWords));  	// all keywords match
		assertTrue(!MatchUDFUtil.match(text, "value_277 abcd", "value_3", MatchType.WholePhrase));   // whole phrase match		
	}
		
	@Test
	public void testEdit() {
		text = "lehman,, aksdjfhalskd-jfhasdkljf brothers has been bankrupt. Lehman	now ;.'	brothers mnsdfb,mnsd lehman jasdfvjhasdfadjhfgaf defaullt bank";
		assertTrue(MatchUDFUtil.match(text, "bankrupcy", "edit", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match(text, "defaults Greece", "edit", MatchType.SomeWords));
	}
	
	@Test
	public void testLCStr() {
		text = "lehman,, aksdjfhalskd-jfhasdkljf brothers has been bankrupt. Lehman	now ;.'	brothers mnsdfb,mnsd lehman jasdfvjhasdfadjhfgaf defaullt bank";
		assertTrue(MatchUDFUtil.match(text, "bankrupcy", "lcstr", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(text, "brothers flock together", "lcstr", MatchType.SomeWords));
	}
	
	@Test
	public void testLCS() {
		text = "lehman,, aksdjfhalskd-jfhasdkljf brothers has been bankrupt. Lehman	now ;.'	brothers mnsdfb,mnsd lehman jasdfvjhasdfadjhfgaf defaullt bank";
		assertTrue(MatchUDFUtil.match(text, "bankruptcy brothers", "lcs", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(text, "lehman bro together", "lcs", MatchType.SomeWords));
	}
	
	@Test
	public void testRegex() {
		text = "lehman,, aksdjfhalskd-jfhasdkljf brothers has been bankrupt. Lehman	now ;.'	brothers mnsdfb,mnsd lehman jasdfvjhasdfadjhfgaf defaullt bank";
		assertTrue(!MatchUDFUtil.match(text, "lehman\\s+brothers\\s+.*bankrupt", "regex", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(text, "lehman\\s+brothers|lehman|bankruptcy", "regex", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.countMatch(text, "lehman\\s+brothers|lehman|bankrupt", "regex") == 4);
	}

	@Test
	public void testAll() {
		
		// whole phrase match
		assertTrue(!MatchUDFUtil.match("lehman not brothers", "lehman BROTHERS", "keyword", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match("lehman	//	brothers		collapses ", "lehman-BROTHERS", "keyword", MatchType.WholePhrase, false));	// split punctuation
		assertTrue(!MatchUDFUtil.match("lehman	//	brothers		collapses ", "lehman-BROTHERS", "keyword", MatchType.WholePhrase, true));	// sentence type
		assertTrue(MatchUDFUtil.match("lehman	//	brothers		collapses ", "lehman - BROTHERS", "keyword", MatchType.WholePhrase, true));	// sentence type
		
		String textLong = "Lehman Brothers Holdings Inc. (former NYSE ticker symbol LEH) " +
			   "(pronounced /'li:man/) was a global financial services firm. " +
			   "Before declaring bankruptcy in 2008, Lehman was the fourth largest " +
			   "investment bank in the USA (behind Goldman Sachs, Morgan Stanley, and Merrill Lynch), " +
			   "doing business in investment banking, equity and fixed-income sales and trading " +
			   "(especially U.S. Treasury securities), market research, investment management, " +
			   "private equity, and private banking.On September 15, 2008, the firm filed for Chapter 11 " +
			   "bankruptcy protection following the massive exodus of most of its clients, drastic losses in " +
			   "its stock, and devaluation of its assets by credit rating agencies. The filing marked the largest " +
			   "bankruptcy in U.S. history,[4] and is thought to have played a major role in the unfolding of the " +
			   "late-2000s global financial crisis. The following day, Barclays announced its agreement to purchase, " +
			   "subject to regulatory approval, Lehman's North American investment-banking and trading divisions " +
			   "along with its New York headquarters building.[5][6] On September 20, 2008, a revised version of " +
			   "that agreement was approved by US Bankruptcy Court Judge James M. Peck.[7] The next week, " +
			   "Nomura Holdings announced that it would acquire Lehman Brothers' franchise in the Asia-Pacific " +
			   "region, including Japan, Hong Kong and Australia,[8] as well as Lehman Brothers' investment banking" +
			   " and equities businesses in Europe and the Middle East. The deal became effective on " +
			   "October 13, 2008.[9]";
		assertTrue(MatchUDFUtil.match(textLong, "lEhmAn * BROTHERS", "keyword", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(textLong, "global Financial Crisis", "keyword", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(textLong, "Lehman brotherS bankrupt declarings", "edit", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match(textLong, "Lehman brothers, Morgan Stanley, Goldman Sachs, Merrill Lynch", "keyword", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match(textLong, "market research investment management", "keyword", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.countMatch(textLong, "lehman") == 5);
		assertTrue(MatchUDFUtil.match(textLong, "the largest bankruptcy in history", "keyword", MatchType.AllWords));
		
		textLong = "Non-negative matrix factorization (NMF) is a group of algorithms in multivariate analysis and " +
				"linear algebra where a matrix, , is factorized into (usually) two matrices,  and  : " +
				"Factorization of matrices is generally non-unique, and a number of different methods of doing " +
				"so have been developed (e.g. principal component analysis and singular value decomposition) by " +
				"incorporating different constraints; non-negative matrix factorization differs from these methods " +
				"in that it enforces the constraint that the factors W and H must be non-negative, i.e., all " +
				"elements must be equal to or greater than zero";
		assertTrue(MatchUDFUtil.match(textLong, "non Negative matrix/factorization", "keyword", MatchType.WholePhrase, false));	 // split punctuation
		assertTrue(!MatchUDFUtil.match(textLong, "non Negative matrix/factorization", "keyword", MatchType.WholePhrase, true));	 // senetencetype
		assertTrue(MatchUDFUtil.match(textLong, "non-Negative matrix / factorization", "keyword", MatchType.WholePhrase, true)); // senetencetype
		assertTrue(MatchUDFUtil.match(textLong, "factorizing matrices", "lcstr", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match(textLong, "principal component singular values", "edit", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match(textLong, "principle component-analysis", "lcs", MatchType.WholePhrase));
		
		textLong = "PRIMES is in P. The AKS primality test (also known as Agrawal-Kayal-Saxena primality test and " +
				"cyclotomic AKS test) is a deterministic primality-proving algorithm created and published by three " +
				"Indian Institute of Technology Kanpur computer scientists, Manindra Agrawal, Neeraj Kayal, and Nitin" +
				"Saxena, on August 6, 2002, in a paper titled PRIMES is in P.[1] The authors received many " +
				"accolades, including the 2006 Godel Prize and the 2006 Fulkerson Prize, for this work. " +
				"The algorithm determines whether a number is prime or composite within polynomial time. " +
				"The key significance of AKS is that it was the first published primality-proving algorithm to be " +
				"simultaneously general, polynomial, deterministic, and unconditional. Previous algorithms had " +
				"achieved three of these properties at most, but not all four." +
				"The AKS algorithm can be used to verify the primality of any general number given. Many fast " +
				"primality tests are known that work only for numbers with certain properties. For example, " +
				"the Lucas-Lehmer test for Mersenne numbers works only for Mersenne numbers, while Pepin's test " +
				"can be applied to Fermat numbers only." +
				"The maximum running time of the algorithm can be expressed as a polynomial over the number of " +
				"digits in the target number. ECPP and APR conclusively prove or disprove that a given number is " +
				"prime, but are not known to have polynomial time bounds for all inputs." +
				"The algorithm is guaranteed to distinguish deterministically whether the target number is prime or " +
				"composite. Randomized tests, such as Miller-Rabin and Baillie-PSW, can test any given number for " +
				"primality in polynomial time, but are known to produce only a probabilistic result." +
				"The correctness of AKS is not conditional on any subsidiary unproven hypothesis. In contrast, " +
				"the Miller test is fully deterministic and runs in polynomial time over all inputs, but its " +
				"correctness depends on the truth of the yet-unproven generalized Riemann hypothesis.";

		assertTrue(MatchUDFUtil.match(textLong, "primes is in p", "keyword", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(textLong, "primes in p, AKS algorithm Godel", "keyword", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match(textLong, "primality-test + Neeraj Kayal", "keyword", MatchType.AllWords, false));  // split punctuation
		assertTrue(!MatchUDFUtil.match(textLong, "primality-test + Neeraj Kayal", "keyword", MatchType.AllWords, true));  // sentence type
		assertTrue(MatchUDFUtil.match(textLong, "primality test + Neeraj Kayal", "keyword", MatchType.AllWords, true));   // sentence type
		assertTrue(MatchUDFUtil.match(textLong, "P!=NP complexity problems", "keyword", MatchType.SomeWords));
		
		assertTrue(MatchUDFUtil.match("lehman not brothers", "Lehman Brothers", "keyword", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match("lehmanBrothers", "Lehman Brothers default collapse", "keyword", MatchType.SomeWords));
		assertTrue(MatchUDFUtil.match("'LeHman'.-.(-:brothers:-)", "%Lehman/***/Brothe^rs%.", "lcstr", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match("Le'H'man.-.(-:bro#thers:-)", "%Lehman/***/Bro^thers%.", "lcs", MatchType.WholePhrase));
		assertTrue(!MatchUDFUtil.match("lehman BANKruptcy", "bankrupted", "keyword", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match("lehman bankruptcy", "lehman BANKrupted", "edit", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match("Breaking News: 'lehman and brothers' - collosal financial firm"
				    			+ ", collapses and defaults this year to bankruptcy", 
				    			"lehmanN - brrothers collapsed:   defaulter bankrupted", "lcstr", MatchType.AllWords));

		assertTrue(MatchUDFUtil.match("This is cool. Ram went there", "cool Ram"));
		assertTrue(MatchUDFUtil.match("This is cool. Ram went there", "cool Ram", "keyword", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match("This is cool. Ram went there", "cool Ram", "keyword", MatchType.WholePhrase));
	}
	
}
