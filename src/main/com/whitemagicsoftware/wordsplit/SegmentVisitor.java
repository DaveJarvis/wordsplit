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
 * Called by the Combinations class when a new combination of words has been
 * defined (recursively). This class gathers statistics about the list of
 * words that are a possible contender for being the solution.
 */
public class SegmentVisitor implements Visitor<Map.Entry<String, Double>> {
  private final String mConcat;

  /**
   * @param concat - The concatenated string that was analysed.
   */
  public SegmentVisitor( final String concat ) {
    mConcat = concat;
  }

  /**
   * Determines the following statistics with respect to the list.
   * <ul>
   * <li>The number of words used in the list versus in the string.</li>
   * <li>The popularity of proposed solution words.</li>
   * <li>The number of remaining characters (and words) after removing the
   * word list from the concatenated string.</li>
   * </ul>
   *
   * @param list - The list of words to examine.
   */
  public SegmentAnalysis visit( List<Map.Entry<String, Double>> list ) {
    String result = getConcatenated();
    int wordsUsed = 0;

    for( final var o : list ) {
      final var word = o.getKey();

      if( result.contains( word ) ) {
        wordsUsed++;
        result = result.replaceFirst( word, " " );
      }
    }

    final var analysis = new SegmentAnalysis( list );

    analysis.setWordsUsed( wordsUsed );
    analysis.setRemaining( result );

    return analysis;
  }

  private String getConcatenated() {
    return this.mConcat;
  }
}

