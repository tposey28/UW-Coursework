//Ian Durra, Taylor Posey
//Homework #6 Part 2
//03/06/15
//CSE 374 Brandon Myers

#pragma once

//PRE: receives 3 pointers to uintptr_ts
//POST: information about the total free memory, memory being used, and the total
//      number of free blocks are stored in one of each uintptr_t pointed to
void get_mem_stats(uintptr_t* total_size, uintptr_t* total_free, uintptr_t* n_free_blocks);
