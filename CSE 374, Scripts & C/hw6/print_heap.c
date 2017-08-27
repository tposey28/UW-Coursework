//Ian Durra, Taylor Posey
//Homework #6 Part 2
//02/24/15
//CSE 374 Brandon Myers

//This file prints to a file information about each block of free memory

#include <stdio.h>
#include <inttypes.h>
#include "mem_impl.h"
#include "print_heap.h"

//PRE: takes a FILE f
//POST: Print a formatted listing on file f 
//	describing the blocks on the free list 
void print_heap(FILE* f){
	if (freefront) {
		//assign temp variable to freelist
		freeblock* curr = freefront;
		//loop through list, printing info about blocks
		while(curr){
			print_single_block(f, curr);
			fprintf(f, "\n\n\n");
			curr = curr->nextblock;
		}
	} else {
		fprintf(f, "the list is empty!\n");
		fprintf(f, "\n\n\n");
	}
}

//PRE: Takes a reference to a single block
//	on the free list
//POST: Print information about a single block beginning with two 
//	hexadecimal numbers giving the address and size
void print_single_block(FILE* f, freeblock* block){	
	fprintf(f, "size of block: %lu\n", *(block->size));
	fprintf(f, "address of block: %p\n", block);

}
