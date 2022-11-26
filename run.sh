#!/usr/bin/env bash

readonly MEM_MIN=1024m
readonly MEM_MAX=1024m
readonly ENCODING=UTF-8

if [ -e build/wordsplit.jar ]; then
  java -Xmx$MEM_MAX -Xms$MEM_MIN -Dfile.encoding=$ENCODING \
    -jar build/wordsplit.jar $1 $2
else
  readonly ANT=$(command -v ant)

  if [ -z "${ANT}" ]; then
    echo "Compile Word Split using ant before running."
    echo ""
    echo "https://ant.apache.org/"
  else
    ant
  fi
fi

