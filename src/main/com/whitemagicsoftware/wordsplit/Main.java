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

import java.io.File;

/**
 * Splits concatenated text into a sentence.
 */
public class Main {
  /**
   * Default constructor.
   */
  public Main() {
  }

  private static void out( final String s ) {
    System.out.println( s );
  }

  /**
   * Main application. Takes a lexicon (with probabilities) and list of
   * concatenated strings. Writes the split strings to standard output.
   */
  public static void main( final String[] args ) {
    final var ts = new TextSegmenter();

    if( args.length == 2 ) {
      try {
        ts.split( new File( args[ 0 ] ), new File( args[ 1 ] ) );
      } catch( final Exception e ) {
        System.err.println( "Error: " + e.getMessage() );
        e.printStackTrace();
      }
    }
    else {
      out( "com.com.whitemagicsoftware.wordsplit.Main <lexicon> <conjoined>" );
      out( "<lexicon>   - CSV file: word,probability" );
      out( "<conjoined> - Text file" );
    }
  }
}

