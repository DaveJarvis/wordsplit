/* Copyright 2022 White Magic Software, Ltd.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.whitemagicsoftware.wordsplit;

import java.util.List;
import java.util.Map;

/**
 * Stores the details about a possible solution to a concatenated phrase.
 * These details allow the TextSegmenter class to determine whether or not
 * the solution is the most likely.
 */
public class SegmentAnalysis {
  private final List<Map.Entry<String, Double>> mWords;

  private int mWordsUsed;
  private String mRemaining;

  public SegmentAnalysis( final List<Map.Entry<String, Double>> words ) {
    mWords = words;
  }

  /**
   * Splits the given word (concatenated text) into multiple words, with
   * spaces to separate each word.
   *
   * @param concat - The words to split.
   * @return The given parameter with spaces in between each word.
   */
  public StringBuilder apply( String concat ) {
    for( final var entry : getWords() ) {
      final var word = entry.getKey();
      concat = concat.replaceFirst( word, " " + word + " " );
    }

    return new StringBuilder( normalise( concat ) );
  }

  public boolean matchedAllWords() {
    return getWordCount() == getWordsUsed();
  }

  public int length() {
    return getRemaining().length();
  }

  /**
   * Removes multiple spaces from inside a string, as well as trimming white
   * space from both ends of the string.
   *
   * @return The value of s with its whitespace normalised.
   */
  private String normalise( final String s ) {
    return s.replaceAll( "\\b\\s{2,}\\b", " " ).trim();
  }

  public void setRemaining( final String remaining ) {
    mRemaining = normalise( remaining );
  }

  private String getRemaining() {
    return mRemaining;
  }

  private double getWordCount() {
    return getWords().size();
  }

  public void setWordsUsed( final int wordsUsed ) {
    mWordsUsed = wordsUsed;
  }

  private double getWordsUsed() {
    return mWordsUsed;
  }

  /**
   * Returns the product of the probability of each word in this potential
   * solution.
   *
   * @return A number between 0 and 1.
   */
  public double getProbability() {
    double probability = 1;

    for( final var entry : getWords() ) {
      probability *= entry.getValue();
    }

    return probability * (getWordsUsed() / getWordCount());
  }

  private List<Map.Entry<String, Double>> getWords() {
    return this.mWords;
  }
}
