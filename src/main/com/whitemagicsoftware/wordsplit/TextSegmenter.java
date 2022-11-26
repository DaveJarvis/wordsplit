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

import java.io.*;
import java.util.*;

/**
 * Splits concatenated text into a sentence.
 */
public class TextSegmenter {
  /**
   * Lexical and concatenated entries must be at least 2 characters.
   */
  private static final int MIN_LEX_LENGTH = 2;

  /**
   * Words and frequencies.
   */
  private final Map<String, Double> dictionary = new TreeMap<>();

  /**
   * List of concatenated words to split.
   */
  private final List<String> concat = new ArrayList<>();

  /**
   * Default constructor.
   */
  public TextSegmenter() { }

  /**
   * Helper method.
   */
  public void split( File lexicon, File concat )
    throws IOException {
    BufferedReader lex = new BufferedReader(
      new InputStreamReader( new FileInputStream( lexicon ) ) );
    BufferedReader col = new BufferedReader(
      new InputStreamReader( new FileInputStream( concat ) ) );

    split( lex, col );

    lex.close();
    col.close();
  }

  /**
   * Splits the text. Callers must close the streams.
   */
  public void split( BufferedReader lexicon, BufferedReader concat )
    throws IOException {
    loadLexicon( lexicon );
    loadConcat( concat );
    split();
  }

  /**
   * Iterates over the concatenated text, splitting each concatenated
   * String into English words.
   */
  private void split() {
    for( String concat : getConcat() ) {
      System.out.printf( "%s,%s\n", concat, segments( concat ) );
    }
  }

  /**
   * Returns a number between 0 and 1 that represents how often the word is
   * used relative to all the other words in the lexicon.
   */
  private double getProbability( String s ) {
    try {
      return getDictionary().get( s );
    } catch( Exception e ) {
      return 0.0;
    }
  }

  /**
   * Splits a concatenated phrase into its constituent words. This will look
   * up the words in a dictionary and find the most likely combination that
   * satisfies the word segmentation.
   *
   * @param concat - The phrase without spaces to split into words.
   * @return The concat text with spaces.
   */
  private String segments( String concat ) {
    final var length = concat.length();
    final var words = new ArrayList<Map.Entry<String, Double>>();

    // Put all the words that exist in the string into a map.
    //
    for( int i = 0; i < length; i++ ) {
      for( int j = 0; j < length - i; j++ ) {
        // Word and probability from the lexicon.
        //
        String w = concat.substring( j, length - i );
        double p = getProbability( w );

        // Retain words that comprise the concatenated string in order.
        //
        if( p > 0 ) {
          words.add( 0, new AbstractMap.SimpleEntry<>( w, p ) );
        }
      }
    }

    var result = new StringBuilder( length * 2 );
    var joined = new StringBuilder( concat );
    int wordCount = words.size();
    int wordsUsed = 0;

    // If all the words can be accounted for, then the problem is solved.
    // If not, then a more complex analysis is required.
    for( final var entry : words ) {
      final var word = entry.getKey();
      final var wlen = word.length();
      final var index = joined.indexOf( word );

      wordsUsed++;

      if( index == 0 ) {
        // The word from the lexicon matched the beginning of
        // the concatenated string. Track the word within "result".
        result.append( word ).append( ' ' );
        joined.delete( 0, wlen );
      }
      else if( index > 0 ) {
        // The word from the lexicon matched the concatenated string,
        // but not at the beginning.
        result.append( joined.substring( 0, index ) ).append( ' ' );
        joined.delete( 0, index );
      }
      else {
        // The word could not be found within the string, so lower the
        // count of the number of words (from the list) that were used
        // in this potential solution. The number of words used will be
        // checked against the number of words found. If they are not
        // equal then a deeper analysis must be performed.
        wordsUsed--;
      }
    }

    // Tack on the last word that was not accounted for in the loop.
    result.append( joined );

    // The 80% case is when there was a 1:1 match between the concatenated
    // text and having found all the suggested words in said text. If there
    // was only one possible match, then there is no point performing any
    // further analysis.
    boolean solved = wordCount == wordsUsed;

    if( !solved ) {
      result.setLength( 0 );

      List<SegmentAnalysis> saList = combinations( concat, words );
      List<SegmentAnalysis> candidates = new ArrayList<>();

      int minLength = Integer.MAX_VALUE;

      // Record the candidates with the shortest remaining character
      // count (after splitting and removing the most probable words).
      // This loop primarily reduces the candidates based on whether all
      // the words in one particular combination of words were used and
      // each of those words exists in the lexicon.
      for( SegmentAnalysis sa : saList ) {
        if( sa.matchedAllWords() ) {
          int saLength = sa.length();

          if( saLength < minLength ) {
            minLength = saLength;
          }

          candidates.add( sa );
        }
      }

      // Swap the segment analysis list for the candidate list. This
      // step isn't necessary, but it makes the previous loop and any
      // subsequent loops operate on the same variables with the same
      // meaning: the "candidates" list will shrink until there is only
      // one element -- the solution.
      swap( saList, candidates );

      // The solutions that have the fewest remaining letters are the
      // ones to keep. The winning solution will be decided by probability.
      for( SegmentAnalysis sa : saList ) {
        if( sa.length() == minLength ) {
          candidates.add( sa );
        }
      }

      swap( saList, candidates );

      SegmentAnalysis solution = saList.get( 0 );
      double maxProbability = Double.MIN_VALUE;

      // Find the solution with the highest probability. The probability
      // is calculated using the probabilities from the lexicon (which
      // are, in turn, used by the SegmentAnalysis instance).
      for( final var sa : saList ) {
        double probability = sa.getProbability();

        if( probability > maxProbability ) {
          solution = sa;
          maxProbability = probability;
        }
      }

      result = solution.apply( concat );
    }

    return result.toString().trim();
  }

  /**
   * Copies the elements from the second list into the first list, then
   * clears the second list. This method is used so that the candidates
   * variable in the 'segments' method always whittles down to the most
   * likely solution.
   */
  private void swap(
    final List<SegmentAnalysis> l1,
    final List<SegmentAnalysis> l2 ) {
    l1.clear();
    l1.addAll( l2 );
    l2.clear();
  }

  /**
   * This method recursively generates a list of all possible word
   * combinations from a list of words. The result is an analysis of each
   * combination, containing details like probability, relative word
   * lengths, and so forth.
   */
  private List<SegmentAnalysis> combinations(
    final String concat, List<Map.Entry<String, Double>> words ) {
    final var v = new SegmentVisitor( concat );

    final var combinations = new Combinations( v );
    return combinations.root( words );
  }

  /**
   * Loads all the words and word probability from the dictionary. Words
   * are separated from the probability by a comma.
   */
  private void loadLexicon( BufferedReader lexiconData )
    throws IOException {
    String line;
    final var dictionary = getDictionary();

    dictionary.clear();

    while( (line = lexiconData.readLine()) != null ) {
      String[] lex = line.toLowerCase().split( "," );

      if( lex[ 0 ].length() >= MIN_LEX_LENGTH ) {
        try {
          dictionary.put( lex[ 0 ], Double.parseDouble( lex[ 1 ] ) );
        } catch( Exception e ) {
          dictionary.put( lex[ 0 ], getDefaultProbability() );
        }
      }
    }
  }

  /**
   * Inserts the lines of concatenated text into the internal list.
   */
  private void loadConcat( BufferedReader concatData )
    throws IOException {
    String line;
    final var concat = getConcat();

    concat.clear();

    while( (line = concatData.readLine()) != null ) {
      if( line.length() >= MIN_LEX_LENGTH ) {
        concat.add( line.toLowerCase() );
      }
    }
  }

  /**
   * Returns the list of strings that have been concatenated together (such
   * as those in a database column name).
   */
  private List<String> getConcat() {
    return this.concat;
  }

  /**
   * Returns a unique set of words, each having a probability calculated
   * using the relative frequency of the word. (The word that appears most
   * often in the dictionary's source corpus has a probability of 1.)
   */
  private Map<String, Double> getDictionary() {
    return this.dictionary;
  }

  /**
   * Returns the default probability when no value is given. This is
   * likely an error in the lexicon that should be fixed.
   */
  private Double getDefaultProbability() {
    return 0.0;
  }
}
