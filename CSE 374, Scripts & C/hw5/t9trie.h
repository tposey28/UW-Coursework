#pragma once

struct node {
	char* word;
	struct node* digits[10];
};

struct node* createroot();

struct node* addword(char*, struct node*);

struct node* findword(struct node*, char*);

void printtree(struct node*);

void freeall(struct node*);
