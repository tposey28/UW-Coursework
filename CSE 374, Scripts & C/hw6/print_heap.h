//Ian Durra, Taylor Posey
//Homework #6 Part 2
//03/06/15
//CSE 374 Brandon Myers

#pragma once

//PRE: Takes a reference to a single block
//      on the free list
//POST: Print information about a single block beginning with two 
//      hexadecimal numbers giving the address and size
void print_single_block(FILE* f, freeblock* block);
