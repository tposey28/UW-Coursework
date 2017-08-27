// Taylor Posey
// CSE 374, Brandon Myers
// 2/20/2015
// Homework 5, t9main.c
// This is the main file for t9. Given a file as an argument, t9 creates a
// digit to word dictionary using the words in the file. An interactions is
// then opened with the user, allowing them to enter a key sequence. If the
// given sequence represents a word in the dictionary, the word is displayed.
// "#" can be used to search for words that have overlapping key sequences.
// The user can exit this interaction by typing "exit" or pressing ctrl-D.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "t9trie.h"
#include "t9io.h"

int main(int argc, char** argv) {
	struct node* root = treefromfile(argv[1]);
	interactions(root);
	freeall(root);
	return 0;	
}
