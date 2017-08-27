//Ian Durra, Taylor Posey
//Homework #6 Part 2
//03/06/15
//CSE 374 Brandon Myers

#pragma once

// Our blocks of free memory, arranged within a list
typedef struct freeblock {
	uintptr_t* size; // Size in bytes of this block
	struct freeblock* nextblock; // Pointer to the next block in the list
} freeblock;

// This declares the root of our list of free blocks
extern freeblock* freefront;

// These declare list properities for get_mem_stats
extern uintptr_t total_mem;
extern uintptr_t free_mem;
extern uintptr_t free_blocks;

// Size of the header for all freeblocks
static const uintptr_t header = sizeof(uintptr_t*) + sizeof(freeblock*);
static const uintptr_t align = 16;
