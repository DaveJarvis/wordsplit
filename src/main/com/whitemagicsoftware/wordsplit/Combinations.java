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

import java.util.ArrayList;
import java.util.List;

/**
 * An almost generic class for generating all possible combinations of
 * values in a list as a list.
 */
@SuppressWarnings("unchecked")
public class Combinations {
  private final static int MAX_DEPTH = 22;

  private Visitor visitor;
  private final List<SegmentAnalysis> analysis = new ArrayList<>();

  /**
   * @param visitor - The class used to examine each possible text segment.
   */
  public Combinations( Visitor visitor ) {
    setVisitor( visitor );
  }

  /**
   * Entry point.
   *
   * @param initial - The list of possible words that could constitute the
   * solution.
   */
  public List<SegmentAnalysis> root( List initial ) {
    clearAnalysis();
    root( new ArrayList(), initial, 0 );
    return getAnalysis();
  }

  /**
   * Print all subsets of the remaining elements, with given prefix.
   */
  private void root( List prefix, List remain, int depth ) {
    if( remain.size() > 0 && depth < MAX_DEPTH ) {
      List combination = new ArrayList<>( prefix.size() + 1 );
      combination.addAll( prefix );
      combination.add( remain.get( 0 ) );

      addAnalysis( getVisitor().visit( combination ) );

      List r = new ArrayList( remain.size() );
      r.addAll( remain.subList( 1, remain.size() ) );

      root( combination, r, depth + 1 );
      root( prefix, r, depth + 1 );
    }
  }

  private void setVisitor( Visitor visitor ) {
    this.visitor = visitor;
  }

  private Visitor getVisitor() {
    return this.visitor;
  }

  private void clearAnalysis() {
    getAnalysis().clear();
  }

  private List<SegmentAnalysis> getAnalysis() {
    return this.analysis;
  }

  private void addAnalysis( SegmentAnalysis sa ) {
    getAnalysis().add( sa );
  }

}
