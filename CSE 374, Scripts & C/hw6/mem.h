//Ian Durra, Taylor Posey
//Homework #6 Part 2
//03/06/15
//CSE 374 Brandon Myers
#pragma once

// Given a required size in bytes, this method will attempt to allocate a block
// of memory of sufficent size for the user. Will return a pointer value as an
// integer if successful, otherwise will return NULL.
void* getmem(uintptr_t size);

//PRE: takes a reference to a block of memory allocated by calling getmem
//POST: Return the block of storage at location p to the pool of available free
//	storage. Behavior is undefined if block was not allocated with getmem
void freemem(void* p);

//PRE: receives 3 pointers to uintptr_ts
//POST: information about the total free memory, memory being used, and the total
//      number of free blocks are stored in one of each uintptr_t pointed to
void get_mem_stats(uintptr_t* total_size, uintptr_t* total_free, uintptr_t* n_free_blocks);

//PRE: takes a FILE f
//POST: Print a formatted listing on file f 
//      describing the blocks on the free list
void print_heap(FILE* f);
