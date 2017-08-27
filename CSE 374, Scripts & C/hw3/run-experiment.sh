#/bin/bash
# Taylor Posey
# CSE 374, Brandon Myers
# Homework 3, Problem 3
# 1/27/2015

# Print an error and exit if there are not exactly two arguments
if [ $# -ne 2 ]
then
	echo "${0}: needs two arguments" >&2
exit 1
fi

# Print an error and exit if the given list file doesn't exist
if [ ! -e "$1" ]
then
	echo "${0}: the given file does not exist" >&2
exit 2
fi

# A rank counter
rank=1
# Create output file (or clears contents if it already exists)
>"$2"
# For each line, perform byte count. If byte count fails, print
# "failed", otherwise print "success" and add rank, URL, and 
# count to the given output file
for line in $(cat "$1")
do
	echo "Performing measurement on"
	echo "$line"
	bytes=$(./perform-measurement.sh "$line")
        if [ $bytes -eq 0 ]
        then
                echo "...failed"
        else
                echo "...success"
        	echo "$rank $line $bytes" >>"$2"
        fi
	let rank=${rank}+1
done

