//Ian Durra, Taylor Posey
//Homework #6 Part 2
//03/06/15
//CSE 374 Brandon Myers

//This file returns allocated memory to the free memory list

#include <stdlib.h>
#include <inttypes.h>
#include "mem_impl.h"
#include "freemem.h"
freeblock* freefront;

//PRE: takes a reference to a block of memory allocated by calling getmem 
//POST: Return the block of storage at location p to the pool of available free 
//storage. Behavior is undefined if block was not allocated with getmem
void freemem(void* p){
	if (p) {	
		freeblock* b = (freeblock*)((uintptr_t)p - header);
		free_mem = free_mem + *(b->size);
		insertblock(b);
	}
}

//PRE: takes a reference to a block of memory allocated by calling getmem 
//POST: inserts the block into the free memory list
void insertblock(freeblock* block){
	if (freefront) {		
		freeblock* curr = freefront;
		freeblock* prev = NULL;
		if (&curr > &block) {
			freefront = block;
			block->nextblock = curr;
		} else {
			while (&curr < &block) {
				prev = curr;
				curr = curr->nextblock;
			}
			block->nextblock = curr;
			prev->nextblock = block;
		}
	} else {
		freefront = block;
	}
	free_blocks = free_blocks + 1;
}

//PRE: takes a reference to a block of memory, and a reference to its neighboring
//block
//POST: merges the respective blocks into a single block
void mergeblocks(freeblock* target, freeblock* prev){
	
}
