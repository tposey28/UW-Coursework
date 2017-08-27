//Ian Durra, Taylor Posey
//Homework #6 Part 2
//03/06/15
//CSE 374 Brandon Myers

#pragma once

// A linked-list containing indexes of empty elements
// within lists used to store references to allocated
// blocks
typedef struct ListIndex {
  int value; //index location of empty value
  struct ListIndex* next; // reference to next block in the list
} ListIndex;

//PRE: takes an a reference to a list of integers
////POST: runs the experiment, printing statistics at 10%, 20% 30% ... 100%
////      of the experiment's runtime. Statistics include total elapsed time,
////      total requested memory and average number of bytes available for requests.
void run_trials(int options[]);

//PRE: takes a reference to an integer, num_blocks, a reference to a list of references to blocks,
////      arr_blocks, and an object to keep track of empty values in the lists of pointers, list.
////POST: returns a block to the freelist. Removes a block from a list containing previously allocated blocks,
////      adds a reference indicating the empty value to the parameter list and finally returns the newly modified
////      parameter, list.
ListIndex* return_block(int* num_blocks, void** arr_blocks, ListIndex* list);

//PRE: takes a references to an integer, num_blocks, a references to a list of references to blocks,
////      arr_blocks, an object to keep track of empty values in the lists of pointers, list and integer
////      keeping track of the lists of references to blocks.
////POST: removes a block from the freelist, stores a reference to that block, and keeps track of where
////      that value is stored, simultaneously updating any relevant experimental statistics..
ListIndex* store_block(int* num_blocks, void** arr_blocks, ListIndex* list, int size);

//PRE: takes an integer, size 
////POST: return a single new node, containing the parameter size
ListIndex* create_node(int size); 

//PRE: takes a reference to a struct, list
////POST: remove a single node and return list
ListIndex* remove_node(ListIndex* list);

//PRE: takes an integer, size, and a reference to a struct  to store size, list.
////POST: adds a new node to list containing a reference to size
ListIndex* add_node(int value, ListIndex* list);

//PRE: takes a reference to the start time of the experiment, comp.
////POST: prints the total elapsed time of the experiment, the number of free blocks
////      available in freelist, and the average number of bytes in the freelist
void print_stats(time_t comp);
