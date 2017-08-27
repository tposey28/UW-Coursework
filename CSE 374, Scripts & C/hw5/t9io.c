// Taylor Posey
// CSE 374, Brandon Myers
// 2/20/2015
// Homework 5, t9io.c
// This controls input and output for t9. It defines the file reading and user
// interaction methods needed to run t9.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "t9trie.h"
#include "t9io.h"

static char* readword(FILE*);
static struct node* lookup(char*, struct node*, struct node*);

// Given the root of a t9 trie, this reads user input and searches the trie for
// the given key sequence. If a matching word is found, the word is printed,
// otherwise a message is printed describing the reason a word was not found.
// Interactions is ended either by the user typing "exit" or pressing ctrl-D.
void interactions(struct node* root) {
        printf("Enter \"exit\" to quit.\n");
	printf("Enter Key Sequence (or \"#\" for next word):\n");
	char* digits = readword(stdin);
	struct node* prev = NULL;
	// Runs until the user types "exit" or EOF is hit (digits == NULL)
        while (digits != NULL && strcmp(digits, "exit") != 0) {
		prev = lookup(digits, prev, root);
		digits = readword(stdin);
	}
	if (digits != NULL) {
		free(digits);
	}
}
// Given a file name, this method creates a t9 trie of the words in the file.
// Returns the root of the trie. Assumes all characters in the file are letters.
// If file doesn't exist or isn't given, prints an error and exits.
struct node* treefromfile(char* file) {
        if (file == NULL) {
		fprintf(stderr, "No file given...\n");
		exit(1);
	}
	FILE* input = fopen(file, "r");
        if (input == NULL) {
                fprintf(stderr, "%s does not exist\n", file);
                exit(2);
        }
        struct node* root = createroot();
        // Reads and adds words until EOF is hit (word == NULL)
        // Ignores blank lines
	char* word = readword(input);
        while (word != NULL) {
		if (strcmp(word, "\n") != 0) {
			root = addword(word, root);
		}
		free(word);
		word = readword(input);
        }
	fclose(input);
        return root;
}

// Lookup method for interactions method. Given a key sequence, the root of the
// t9 trie, and the node found in the previous lookup call, this method will 
// search the trie for the sequence. If found, prints it. Otherwise, prints
// why the word was not found. Returns the trie node the word was found in, if
// found, or a NULL node if not found.
static struct node* lookup(char* digits, struct node* node, struct node* root) {
	if (strcmp(digits, "\n") != 0) { // Ignore blank lines
                if (strcmp(digits, "#") == 0) { // If a # is given, look in node
                        if (node == NULL || node->digits[0] == NULL) {
                                printf("\tThere are no more T9onyms\n");
                        } else {
                                node = node->digits[0];
                                printf("\t\t'%s'\n", node->word);
                                printf("Enter Key Sequence (or \"#\" for next word):\n");
                        }
                } else { // If no # is given, look from the root
                        node = findword(root, digits);
                        if (node == NULL) {
                                if (digits[strlen(digits) - 1] == '#') {
                                        printf("\tThere are no more T9onyms\n");
                                } else {
                                        printf("\tNot found in current dictionary.\n");
                                }
                        } else {
                                printf("\t\t'%s'\n", node->word);
                                printf("Enter Key Sequence (or \"#\" for next word):\n");
                        }
                }
	}
        free(digits);
        return node;
}



static char* readword(FILE* input) {
        int size = 1;
	int ch;
	char* word = (char*) malloc(sizeof(char));
        while ((ch = fgetc(input)) != '\0' && ch != '\n' && ch != EOF) {
                size++;
                word = (char*) realloc(word, sizeof(char)*size);
		word[size-2] = (char) ch;
        }
	if (ch != EOF && size > 1) {
		word[size-1] = '\0';
		return word;
        } else if (ch == '\n' && size == 1) {
		word = (char*) realloc(word, sizeof(char)*2);
		word[0] = '\n';
		word[1] = '\0';
		return word;
	} else { 
		free(word);
		return NULL;
	}
}
