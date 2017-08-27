//Ian Durra, Taylor Posey
//Homework #6 Part 1
//02/14/15
//CSE 374 Brandon Myers

#pragma once

void* findmem(uintptr_t size);

// Allocates memory of the given size from system. Generally does a large block
// of 8000 bytes, but if that is not large enough a large block is obtained for
// user. 8000 byte block is split into a block of the right size for user.
void* allocate_memory(uintptr_t size);

// Splits given block into a block of the given size and a block of the leftover
// // size.
freeblock* splitblock(uintptr_t size);

static const uintptr_t buffer = 128;
