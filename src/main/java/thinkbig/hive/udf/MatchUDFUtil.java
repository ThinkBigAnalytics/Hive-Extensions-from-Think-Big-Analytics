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

import java.text.BreakIterator;
import java.util.*;

public class MatchUDFUtil {
    private static final String PUNCTUATION = "[;\\.\\-_,\\|\\&\\%\\*\\?\\!\\^\\~\\s+\\[\\]\\+/'\"@#=\\>\\<\\(\\)\\{\\}\\:]+";

    /*
     * private enum AlgoType { KEYWORD, // exact match EDITDISTANCE, // approx match LCS, // approx match LCSTR, // approx match
     * REGEX, // regex match KMP, BOYERMOORE, RABINKARP, SUFFIXTREE, AHOCORASICK //... }
     */

    public enum MatchType {
        SomeWords, // check if at least one word in the pattern matches (OR)
        AllWords, // check if at least one word in the pattern matches (AND)
        WholePhrase
        // check if the entire phrase matches
    }

    /**
     * gets the actual algorithm class
     * 
     * @param algoType
     * @return the concrete algorithm class
     */
    public static MatchAlgorithm getAlgo(String algoType) {

        MatchAlgorithm algo = null;

        algoType = algoType == null ? "keyword" : algoType;

        if (algoType.equalsIgnoreCase("regex")) {
            algo = new RegexMatch();
        }
        else if (algoType.equalsIgnoreCase("edit")) {
            algo = new EditDistanceMatch();
        }
        else if (algoType.equalsIgnoreCase("lcstr")) {
            algo = new LCStrMatch();
        }
        else if (algoType.equalsIgnoreCase("lcs")) {
            algo = new LCSMatch();
        }
        else { // default is "keyword"
            algo = new KeywordBasedMatch();
        }
        return algo;
    }

    public static boolean match(String text, String pattern) {
        return match(text, pattern, "keyword", MatchType.SomeWords);
    }

    public static boolean match(String text, String pattern, String algoType, MatchType matchType) {
        return match(text, pattern, algoType, matchType, true); // use sentence type split using BackIterator
    }


    // An LRU cache using a linked hash map
    static class HashCache<K, V> extends LinkedHashMap<K, V> {

      private static final int CACHE_SIZE = 32;
      private static final int INIT_SIZE = 64;
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

    static HashCache<String, String> patternPunctuationNormalized = new HashCache<String, String>();
    static HashCache<String, String> patternSentenceNormalized = new HashCache<String, String>();
    static HashCache<String, String[]> patternSplits = new HashCache<String, String[]>();
    
    // TODO: should use enum for algorithm type
    // then use Enum.valueOf for String to enum conversion
    /**
     * matches text with pattern
     * 
     * @param text
     * @param pattern
     * @param algoType
     * @param matchType
     * @return true is found a match, false otherwise
     */
    public static boolean match(String text, String pattern, String algoType, MatchType matchType, boolean sentenceStyle) {

        algoType = algoType == null ? "keyword" : algoType;
        MatchAlgorithm algo = getAlgo(algoType);

        // get rid of the punctuation from text
        if (!sentenceStyle) {
            text = text.toLowerCase().replaceAll(PUNCTUATION, " ");            
        }
        else {
            String[] splits = splitSentence(text.toLowerCase(), Locale.ENGLISH);
            StringBuilder holder = new StringBuilder();
            for (String textSplit : splits) {
                holder.append(textSplit);
                holder.append(' ');
            }
            text = holder.toString();
        }

        if (!algoType.equalsIgnoreCase("regex")) {
            
            // get rid of the punctuation from pattern
            if (!sentenceStyle) {
                String result = patternPunctuationNormalized.get(pattern);
                if (result == null) {
                    result = pattern.toLowerCase().replaceAll(PUNCTUATION, " ");
                    patternPunctuationNormalized.put(pattern, result);
                }
                pattern = result;
            }
            else {
                String result = patternSentenceNormalized.get(pattern);
                if (result == null) {
                    String[] splits = splitSentence(pattern.toLowerCase(), Locale.ENGLISH);
                    StringBuilder pat = new StringBuilder();
                    for (String patSplit : splits) {
                        pat.append(patSplit);
                        pat.append(' ');
                    }
                    result = pat.toString();
                    patternSentenceNormalized.put(pattern, result);
                }
                pattern = result;
            }
        }
        //System.out.println("Text & Pattern: " + text + "& " + pattern);

        boolean found = matchType == MatchType.AllWords ? true : false;
        if (matchType == MatchType.WholePhrase) {
            found = algo.match(text, pattern);
        }
        else {
            String[] patterns = patternSplits.get(pattern);
            if (patterns == null) {
                patterns = pattern.split(" ");
                patternSplits.put(pattern, patterns);
            }
            for (int i = 0; i < patterns.length; ++i) {
                if (matchType == MatchType.AllWords) {
                    found &= algo.match(text, patterns[i]);
                    if (!found)
                        return false;
                }
                else { // default: check if at least one word in the pattern matches
                    found |= algo.match(text, patterns[i]);
                    if (found)
                        return true;
                }
                // ToDo: add weights to words from the pattern
            }
        }
        //System.out.println(found);
        return found;
    }

    public static int countMatch(String text, String pattern) {
        return countMatch(text, pattern, "keyword");
    }

    // should use enum for algorithm type
    public static int countMatch(String text, String pattern, String algoType) {

        MatchAlgorithm algo = null;

        algoType = algoType == null ? "keyword" : algoType;

        if (algoType.equalsIgnoreCase("regex")) {
            algo = new RegexMatch();
        }
        else if (algoType.equalsIgnoreCase("edit")) {
            algo = new EditDistanceMatch();
        }
        else if (algoType.equalsIgnoreCase("lcstr")) {
            algo = new LCStrMatch();
        }
        else if (algoType.equalsIgnoreCase("lcs")) {
            algo = new LCSMatch();
        }
        else { // default is "keyword"
            algo = new KeywordBasedMatch();
        }

        text = text.replaceAll(PUNCTUATION, " ");
        System.out.print("Text & Pattern: " + text + "& " + pattern);

        return algo == null ? 0 : algo.countMatch(text.toLowerCase(), pattern.toLowerCase());
    }

    /**
     * Split using BreakIterator
     * 
     * @param text
     * @return
     */
    public static String[] splitSentence(String text, Locale locale) {

        if (text == null) {
            return null;
        }

        BreakIterator boundary = BreakIterator.getWordInstance(locale);
        boundary.setText(text);
        // note the words may contain punctuation characters, get rid of them
        List<String> words = new ArrayList<String>();

        // take care of the first word specially
        for (int p = boundary.first(); p < boundary.next(); p++) {
            if (Character.isLetter(text.codePointAt(p))) {
                // a valid word
                String word = text.substring(boundary.first(), boundary.next());
                // System.out.println(word);
                words.add(word);
                break;
            }
        }

        int pos = 0;
        while (pos != BreakIterator.DONE) {
            Boundary wb = getNextWord(pos, text);
            if (wb.end != BreakIterator.DONE) {
                String word = text.substring(wb.start, wb.end);
                // System.out.println(word);
                words.add(word);
            }
            pos = wb.end;
        }

        return (String[]) words.toArray(new String[0]);
    }

    /**
     * represents word boundary
     * 
     * @author sandipandey
     * 
     */
    private static class Boundary {
        int start, end;

        Boundary(int s, int e) {
            start = s;
            end = e;
        }
    }

    /**
     * uses heuristic to find whether the content between two boundaries is a word
     * 
     * @param pos
     * @param text
     * @return next word start and end pos
     */
    public static Boundary getNextWord(int pos, String text) {
        BreakIterator wordBoundary = BreakIterator.getWordInstance();
        wordBoundary.setText(text);
        int last = wordBoundary.following(pos);
        int current = wordBoundary.next();
        while (current != BreakIterator.DONE) {
            for (int p = last; p < current; p++) {
                if (Character.isLetter(text.codePointAt(p)))
                    return new Boundary(last, current);
            }
            last = current;
            current = wordBoundary.next();
        }
        return new Boundary(last, BreakIterator.DONE);
    }
}
