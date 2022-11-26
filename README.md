# Word Split: Text Segmentation Tool

Word Split is a Java application for Java software developers. The application
provides a way to split conjoined words that lack punctuation into separate
words. The software is intended to split database column names into their
human-readable equivalent text.

Word Split takes the following input files:

  - a probability lexicon, one word and probability per line (CSV format)
  - a list of conjoined phrases, one per line

Word Split will use the lexicon to separate the list of conjoined phrases.
The resulting segmented phrases are written to standard out.

# Contents

This release includes:
 
  - version.properties         Build version
  - build.xml                  Build instructions for Ant
  - demos                      Example lexicons and conjoined files
  - scripts                    Corpus and lexicon helper scripts
  - scripts/tally-corpus.sh    Creates a tally lexicon from a corpus.
  - scripts/probability.awk    Creates a probability lexicon from tallies

# Requirements

The following software packages are required to compile and run Word Split:

  - Java 11 (or newer)
  - Ant 1.8.2 (or newer)

# Building

Build the software as follows:

    ant

# Running

After building, run the software on Linux (or MacOS) as follows:

    ./run.sh

