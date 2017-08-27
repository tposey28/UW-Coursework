#!/bin/bash
# Taylor Posey
# CSE 374, Brandon Myers
# Homework 3, Problem 1
# 1/27/2014

# Print an error message if there is not exactly one argument
if [ $# -ne 1 ]
then 
	echo "${0}: needs one URL argument" >&2
exit 1
fi

# Create a temp file and save the given HTML to that temp.
# Will retry download if no progress is made after 30 seconds,
# and will retry 3 times.
temp="$(mktemp tempXXXX)"
wget -q -t 3 -T 30 -O "$temp" "$1"

# If download failed, remove temp, print 0, and exit
if [ $? -ne 0 ]
then
	rm $temp
	echo 0
exit 2
fi

# Otherwise, print byte count of HTML file
wc -c <"$temp"

# Clear up temp file
rm $temp
