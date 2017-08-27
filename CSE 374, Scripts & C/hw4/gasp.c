// Taylor Posey
// CSE 374, Brandon Myers
// 2/5/2015
// Homework 4, gasp.c
// This program searches the given text files for occurances of the given string.
// Lines containing the string are printed to stdout, preceded by the file name
// the line was found in.
// Options: '-i' will do a case-insensitive search.
// 	    '-n' will print the line number of each occurance.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
/*Constants for maximum lengths*/
#define MAX_STRING 100
#define MAX_LINE 500

/*Global variables for given options*/
int ignoreCase = 0;
int numberLines = 0;

void processFile(char*, char*);
int searchLine(char*, char*);
int options(int, char**);
void lowerCase(char*);

int main(int argc, char** argv) {
	/*optNum is the number of options specified*/
	int optNum = options(argc, argv);
	char pattern[MAX_STRING];
	/*pattern is a copy of the given argument string*/
	strncpy(pattern, argv[optNum + 1], MAX_STRING);
	/*For the '-i' option*/
	if (ignoreCase) {
		lowerCase(pattern);
	}
	/*Searches for the given string in each given file*/
	for (int i = optNum + 2; i < argc; i++) {
		processFile(argv[i], pattern);
	}
	return 0;
}

// Searches every line of the given file for the given string, pattern.
// If the given file does not exist or can not be opened, an error message is
// printed to stderr and the function call ends.
void processFile(char* file, char* pattern) {
        /*inputFile is a  file pointer for the given file name*/
	FILE* inputFile = fopen(file, "r");
	if (inputFile == NULL) {
		fprintf(stderr, "%s does not exist\n", file);
		return;
	}
	int lineCount = 0;
	/*Allocating memory for fgets, line will store the line retrieved*/
        char line[MAX_LINE];
	/*Seaches lines and prints matches until the EOF is reached*/
        while (fgets(line, MAX_LINE, inputFile) != NULL) {
                lineCount++;
		if (searchLine(line, pattern)) {
			printf("%s ", file);
			/*For the '-n' option*/
			if (numberLines) {
				printf("%d ", lineCount);
			}
			printf("%s", line);
		}
        }
}

// Searches the given line for the given string, pattern.
// If found, returns 1. Otherwise, returns 0.
int searchLine(char* line, char* pattern) {
        /*temp is a copy of line, allowing for case changing if necessary*/
	char temp[MAX_LINE];
        strncpy(temp, line, MAX_LINE);
        /*For the '-i' option*/
	if (ignoreCase) {
                lowerCase(temp);
        }
	/*Checks to see if pattern is in the given line (temp)*/
        if (strstr(temp, pattern) != NULL) {
	        return 1;
	} else {
		return 0;
	}
}

// Looks to see if the user specified any options.
// Returns the number of options specified as an int.
int options(int argc, char** argv) {
        int count = 0;
        for (int i = 1; i < argc && i <= 3; i++) {
                if (argv[i][0] == '-') {
                        /*Set globals to reflect options specified*/
			if (argv[i][1] == 'i') {
                                ignoreCase = 1;
                        }
                        if (argv[i][1] == 'n') {
                                numberLines = 1;
                        }
                        count++;
                }
        }
        return count;
}

// Takes a reference to a string and changes it to all lowercase.
void lowerCase(char* str) {
        for (int i = 0; str[i]; i++){
                str[i] = tolower(str[i]);
        }
}

