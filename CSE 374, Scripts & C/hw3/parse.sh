#/bin/bash
# Taylor Posey
# CSE 374, Brandon Myers
# Homework 3, Problem 2
# 1/27/2015

# Print an error and exit if there are not exactly two arguments
if [ $# -ne 2 ]
then
	echo "${0}: two arguments needed" >&2
exit 1
fi

# Print an error and exit if the HTML file cannot be found
if [ ! -e "$1" ]
then
	echo "${0}: that HTML file does not exist" >&2
exit 2
fi

# The grep line extracts the URLs, although only the beginnings (up to the
# first "/"). Then, all URLs with "babel", "100bestwebsites" and
# "en.wikipedia" are removed using sed, since they are not in the top 100.
# This is then saved to the given output file.
grep -o 'http://[^/]*/' "$1" | sed -e '/babel/d' -e '/100bestwebsites/d' -e '/en\.wikipedia/d' >"$2"
