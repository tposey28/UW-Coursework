// Taylor Posey
// CSE 374, Brandon Myers
// 2/20/2015
// Homework 5, t9io.c
// This defines the trie needed for t9. Stores words in the trie based on their
// t9 key sequence. Defines the addition of words, searching of words, and
// creation of nodes.
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "t9trie.h"
// An array mapping the 26 letters of the alphabet to a key
int T9_DICTIONARY[] = {2, 2, 2, 3, 3, 3, 4, 4, 4, 5, 5, 5, 6, 6, 6,
                         7, 7, 7, 7, 8, 8, 8, 9, 9, 9, 9};

static int charnum(char);
static struct node* checknode(struct node*);
static void printnode(struct node*, char*, int);
static void freethis(struct node*);

// Creates a new root of a trie. Returns this root.
struct node* createroot() {
	struct node* root = (struct node*) malloc(sizeof(struct node));
        root->word = NULL;
	return root;
}

// Given a word and the root of a trie, adds the word to the trie. Overlapping
// words are added as a linked list from the conflicting word. Returns the
// newly formed root.
struct node* addword(char* word, struct node* root) {	
	struct node* curr = root;
	for (int i = 0; i < strlen(word); i++) {
               	int digit = charnum(word[i]);
		curr->digits[digit] = checknode(curr->digits[digit]);
               	curr = curr->digits[digit];
       	}
	// This goes deeper if there is a conflict
	while (curr->word != NULL) {
		curr->digits[0] = checknode(curr->digits[0]);
		curr = curr->digits[0];
	}	
	char* temp = (char*) malloc(sizeof(word));
	temp = strcpy(temp, word);
      	curr->word = temp;
	return root;
}

// Given a node in the trie and a key sequence, searches for the word in the
// trie. Returns the node the word is in if found. Returns NULL otherwise.
struct node* findword(struct node* node, char* digits) {	
	// Traverses a character at a time
	for (int i = 0; i < strlen(digits); i++) {
		if (node != NULL) {
			int digit = digits[i] - '0';
			if (digits[i] == '#') { // For overlaps
				node = node->digits[0];
			} else if (digit >= 0 && digit <= 9) {
				node = node->digits[digit];
			}
		} else {
			return NULL;
		}
	}
	if (node == NULL) {
		return NULL;
	} else if (node->word == NULL) {
		return NULL;
	} else {
		return node;
	}	
}

// Prints the given tree.
void printtree(struct node* root) {
	printf("Root...\n");
	char digits[50];
	for (int i = 2; i < 10; i++) {
		char digit[2];
		sprintf(digit, "%d", i);
		strcpy(digits, digit);
		printnode(root->digits[i], digits, 1);
		digits[0] = '\0';
	}
}

// Frees the given tree recursively
void freeall(struct node* root) {
	freethis(root);
}

static void printnode(struct node* node, char* digits, int level) {
	if (node != NULL) {
		if (node->word != NULL) {
			printf("%s: %s\n", digits, node->word);
                        strcat(digits, "#");
                        printnode(node->digits[0], digits, level+1);
                        digits[level] = '\0';
		}
		for (int i = 2; i < 10; i++) {
			char digit[2];
			sprintf(digit, "%d", i);
			strcat(digits, digit);
			printnode(node->digits[i], digits, level+1);
			digits[level] = '\0';
		}
	}
}

static void freethis(struct node* curr) {
        if (curr != NULL) {
                for (int i = 0; i < 10; i++) {
                        freethis(curr->digits[i]);
                }
		if (curr->word != NULL) {
			free(curr->word);
        	}
	        free(curr);
        }
}

// Converts a letter to a key
static int charnum(char ch) {
        return T9_DICTIONARY[tolower(ch) - 'a'];
}

// Checks a node to make sure it's not null
static struct node* checknode(struct node* node) {
        if (node == NULL) {
        	node = (struct node*) malloc(sizeof(struct node));
        	node->word = NULL;
        }
        return node;
}

