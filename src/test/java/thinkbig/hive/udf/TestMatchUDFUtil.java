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

import java.util.Locale;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import thinkbig.hive.udf.MatchUDFUtil.MatchType;

/**
         * UnitTestcases for Hive Pattern-matching UDFs
         *
         * author: Sandipan Dey
         */
public class TestMatchUDFUtil extends TestCase {
	
	String text;
	
	@Before
	public void setup(){
		text = "king,, aksdjfhalskd-jfhasdkljf singers has been disbanded. King	now ;.'	singers mnsdfb,mnsd king jasdfvjhasdfadjhfgaf defaullt band";
	}
	
	@Test
	public void testSplitUsingBreakIterator() {
		   String[] words = MatchUDFUtil.splitSentence("King Brothers Holdings Inc...; (former big ticker symbol KKQ) ", Locale.ENGLISH);
		   /*for (String word: words) {
			   System.out.println(word);
		   }*/
		   assertTrue(words.length == 9);
    	}

	@Test
	public void testKeyword() {
		text = "king,, aksdjfhalskd-jfhasdkljf singers has been disbanded. King	now ;.'	singers mnsdfb,mnsd king jasdfvjhasdfadjhfgaf defaullt band";
		assertTrue(!MatchUDFUtil.match(text, "king singers", "keyword", MatchType.WholePhrase));  // whole phrase match
		assertTrue(MatchUDFUtil.match(text, "king singers", "keyword", MatchType.AllWords));  	// all keywords match
		assertTrue(!MatchUDFUtil.match(text, "value_277 abcd", "value_3", MatchType.WholePhrase));   // whole phrase match		
	}
		
	@Test
	public void testEdit() {
		text = "king,, aksdjfhalskd-jfhasdkljf singers has been banned. King	now ;.'	singers mnsdfb,mnsd king jasdfvjhasdfadjhfgaf defaullt band";
		assertTrue(MatchUDFUtil.match(text, "banner", "edit", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match(text, "defaults Stones", "edit", MatchType.SomeWords));
	}
	
	@Test
	public void testLCStr() {
		text = "king,, aksdjfhalskd-jfhasdkljf singers has been disbanded. King	now ;.'	singers mnsdfb,mnsd king jasdfvjhasdfadjhfgaf defaullt band";
		assertTrue(MatchUDFUtil.match(text, "disbanded", "lcstr", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(text, "singers flock together", "lcstr", MatchType.SomeWords));
	}
	
	@Test
	public void testLCS() {
		text = "king,, aksdjfhalskd-jfhasdkljf singers has been disbanded. King	now ;.'	singers mnsdfb,mnsd king jasdfvjhasdfadjhfgaf defaullt band";
		assertTrue(MatchUDFUtil.match(text, "disbandedcy singers", "lcs", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(text, "king bro together", "lcs", MatchType.SomeWords));
	}
	
	@Test
	public void testRegex() {
		text = "king,, aksdjfhalskd-jfhasdkljf singers has been disbanded. King	now ;.'	singers mnsdfb,mnsd king jasdfvjhasdfadjhfgaf defaullt band";
		assertTrue(!MatchUDFUtil.match(text, "king\\s+singers\\s+.*disbanded", "regex", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match(text, "king\\s+singers|king|disbandedcy", "regex", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.countMatch(text, "king\\s+singers|king|disbanded", "regex") == 4);
	}

	@Test
	public void testAll() {
		
		// whole phrase match
		assertFalse(MatchUDFUtil.match("king not singers", "king SINGERS", "keyword", MatchType.WholePhrase));
		// TODO figure out why this fails. Is the test wrong?
		//assertTrue(MatchUDFUtil.match("king	//	singers		collapses ", "king-SINGERS", "keyword", MatchType.WholePhrase, false));	// split punctuation
		assertFalse(MatchUDFUtil.match("king	//	singers		collapses ", "king-SINGERS", "keyword", MatchType.WholePhrase, true));	// sentence type
		assertTrue(MatchUDFUtil.match("king	//	singers		collapses ", "king - SINGERS", "keyword", MatchType.WholePhrase, true));	// sentence type
		
		String textLong = "Non-negative matrix factorization (NMF) is a group of algorithms in multivariate analysis and " +
				"linear algebra where a matrix, , is factorized into (usually) two matrices,  and  : " +
				"Factorization of matrices is generally non-unique, and a number of different methods of doing " +
				"so have been developed (e.g. principal component analysis and singular value decomposition) by " +
				"incorporating different constraints; non-negative matrix factorization differs from these methods " +
				"in that it enforces the constraint that the factors W and H must be non-negative, i.e., all " +
				"elements must be equal to or greater than zero";
		assertTrue(MatchUDFUtil.match(textLong, "non Negative matrix/factorization", "keyword", MatchType.WholePhrase, false));	 // split punctuation
		assertFalse(MatchUDFUtil.match(textLong, "non Negative matrix/factorization", "keyword", MatchType.WholePhrase, true));	 // sentence type
		assertTrue(MatchUDFUtil.match(textLong, "non-Negative matrix / factorization", "keyword", MatchType.WholePhrase, true)); // sentence type
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
		
		assertTrue(MatchUDFUtil.match("king not singers", "King Singers", "keyword", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match("kingSingers", "King Singers default collapse", "keyword", MatchType.SomeWords));
		assertTrue(MatchUDFUtil.match("'KiNg'.-.(-:singgers:-)", "%King/***/Singge^rs%.", "lcstr", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match("Ki'N'g-.(-:sin#gers:-)", "%King/***/Sin^gers%.", "lcs", MatchType.WholePhrase));
		assertTrue(!MatchUDFUtil.match("king BANKruptcy", "disbandeded", "keyword", MatchType.WholePhrase));
		assertTrue(MatchUDFUtil.match("king disbanded", "king DISBanded", "edit", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match("Breaking News: 'king and singers' - collosal musical group"
				    			+ ", collapses and ends this year in disbandedcy", 
				    			"kingG - singers collapsed: disbanded", "lcstr", MatchType.AllWords));

		assertTrue(MatchUDFUtil.match("This is cool. Ram went there", "cool Ram"));
		assertTrue(MatchUDFUtil.match("This is cool. Ram went there", "cool Ram", "keyword", MatchType.AllWords));
		assertTrue(MatchUDFUtil.match("This is cool. Ram went there", "cool Ram", "keyword", MatchType.WholePhrase));
	}
	
}
