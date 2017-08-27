//Ian Durra, Taylor Posey
//Homework #6 Part 1
//02/24/15
//CSE 374 Brandon Myers

//This file takes and executes a request to allocate free memory
#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>
#include "mem_impl.h"
#include "getmem.h"

// Given a required size in bytes, this method will attempt to allocate a block
// of memory of sufficent size for the user. Will return a pointer value as an
// integer if successful, otherwise will return NULL.
void* getmem(uintptr_t size) {
	if (size > 0) {
		void* block = findmem(size);
		return block;
	}
	return(0);
}

// Searches the list of free blocks for a block with a size that is between the
// given size and size + buffer (default is 128 bytes). If one is found, that 
// block is returned to the user. If no block is found, then attempts are made
// to split a block of the necessary size off of a block larger than size +
// buffer. In the case that there are no blocks large enough, or no blocks at
// all, memory is allocated from the system.
void* findmem(uintptr_t size) {
	if (freefront) { // Search if there are blocks			
		freeblock* curr = freefront;
		uintptr_t max = *(curr->size); // For keeping track of largest block
		// If the front is the right size, remove it from list and return it
		if (*(freefront->size) >= size && *(freefront->size) <= size + buffer) {
			freefront = freefront->nextblock;
			return (void*)((uintptr_t)curr + header);
		}
		// Otherwise, keep looking
		freeblock* prev = curr;
		curr = curr->nextblock;
		while (curr) {
			if (*(curr->size) > max) {
				max = *(curr->size);
			}
			// If the current block works, remove it and return it
			if (*(curr->size) >= size && *(curr->size) <= size + buffer) {
				prev->nextblock = curr->nextblock;
				return (void*)((uintptr_t)curr + header);
			}
			prev = curr;
			curr = curr->nextblock;
		}
		// If we know that there is a block that is larger, split it
		if (max > size + buffer) {
			curr = splitblock(size);
			return (void*)((uintptr_t)curr + header);
		}
	}
	// If all else fails, allocate memory from system
	return allocate_memory(size);
}

// Allocates memory of the given size from system. Generally does a large block
// of 8000 bytes, but if that is not large enough a large block is obtained for
// user. 8000 byte block is split into a block of the right size for user.
void* allocate_memory(uintptr_t size) {
	freeblock* new;
	if (size <= 8000 - buffer) {
		new = (freeblock*)malloc(8000 + header);
		new->size = (uintptr_t*)malloc(sizeof(uintptr_t));
		*(new->size) = 8000;
		new->nextblock = freefront;
		freefront = new;
		total_mem = total_mem + 8000 + header;
		new = splitblock(size);
	} else {
		size = size + (align - size % align);
		new = (freeblock*)malloc(size + header);
		new->size = (uintptr_t*)malloc(sizeof(uintptr_t));
		*(new->size) = size;
		new->nextblock = NULL;
		total_mem = total_mem + size + header;
	}
	return (void*)((uintptr_t)new + header);
}

// Splits given block into a block of the given size and a block of the leftover
// size.
freeblock* splitblock(uintptr_t size) {
	freeblock* curr = freefront;
	freeblock* new;
	if (*(freefront->size) >= size + buffer) {
		new = curr;
        	curr = (freeblock*)((uintptr_t)curr + size + header);
        	memmove(curr, new, header);
		curr->size = (uintptr_t*)malloc(sizeof(uintptr_t));
        	*(curr->size) = *(curr->size) - size;
		*(new->size) = size;
        	new->nextblock = NULL;
		freefront = curr;
		return new;
	}
	freeblock* prev = curr;
	curr = curr->nextblock;
	while (*(curr->size) < size + buffer) {
		prev = curr;
		curr = curr->nextblock;
	}
	new = curr;
	curr = (freeblock*)((uintptr_t)curr + size + header);
	memmove(curr, new, header);
        curr->size = (uintptr_t*)malloc(sizeof(uintptr_t));
        *(curr->size) = *(curr->size) - size;
        *(new->size) = size;
	prev->nextblock = curr; 
	return new;
}
