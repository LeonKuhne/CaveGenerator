#!/bin/bash
# usage: <output> | clean.sh 

# read in regex expresions from .clean file

# read input
text=$(cat)
while read regex; do
  if [[ $regex == "" ]]; then continue; fi
  if [[ $regex == \#* ]]; then continue; fi
  # update text using perl
  text=$(echo "$text" | perl -pe "'$regex'")
done < .clean
echo "$text"