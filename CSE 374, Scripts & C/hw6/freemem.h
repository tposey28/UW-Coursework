//Ian Durra, Taylor Posey
//Homework #6 Part 2
//03/06/15
//CSE 374 Brandon Myers

#pragma once

//PRE: takes a reference to a block of memory allocated by calling getmem 
//POST: inserts the block into the free memory list
void insertblock(freeblock*);

//PRE: takes a reference to a block of memory, and a reference to its neighboring
//	block
//POST: merges the respective blocks into a single block
void mergeblocks(freeblock* target, freeblock* adjacent);
