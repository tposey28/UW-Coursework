//Ian Durra, Taylor Posey
//Homework #6 Part 2
//02/24/15
//CSE 374 Brandon Myers

//This file updates stats surrounding the amount of free memory available
#include <stdlib.h>
#include <inttypes.h>
#include "mem_impl.h"
#include "get_mem_stats.h"
uintptr_t total_mem = 0;
uintptr_t free_mem = 0;
uintptr_t free_blocks = 0;

//PRE: receives 3 pointers to uintptr_ts
//POST: information about the total free memory, memory being used, and the total
//      number of free blocks are stored in one of each uintptr_t pointed to
void get_mem_stats(uintptr_t* total_size, uintptr_t* total_free, uintptr_t* n_free_blocks){
	//assign parameters to values of global variables
	*total_size = total_mem;
	*total_free = free_mem;
	*n_free_blocks = free_blocks;
}

