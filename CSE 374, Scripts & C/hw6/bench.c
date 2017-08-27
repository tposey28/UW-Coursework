//Ian Durra, Taylor Posey
//Homework #6 Part 2
//02/24/15
//CSE 374 Brandon Myers
// Main for bench program. bench will perform memory allocation tests using the 
// given parameters. If a parameter is not given, then a default is used.
// Stats are printed 10 times during execution, detailing the total CPU time,
// the amount of storage acquired, the amount of free blocks, and the average
// amount of bytes per free block.
#include <inttypes.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include "mem.h"
#include "bench.h"

int main(int argc, char** argv) {
	time_t t;	
	//ntrials, pctget, pctlarge, small_limit, large_limit, random_seed
	int options[] = {100, 50, 10, 200, 20000, time(&t)};

	//assign arguments to options array
	for(int i = 1; i < argc; i++){
		options[i - 1] = atoi(argv[i]);
	}
	//run experiment
	run_trials(options);
	return 0;
}

//PRE: takes an a reference to a list of integers
//POST: runs the experiment, printing statistics at 10%, 20% 30% ... 100%
//	of the experiment's runtime. Statistics include total elapsed time,
//	total requested memory and average number of bytes available for requests.
void run_trials(int options[]) {
	//seed random number generator
	srand((unsigned) options[5]);
	clock_t comp = clock();
	//initialize arrays of pointers to memory blocks
	void** arr_large_blocks = (void**) malloc(sizeof(void*) * options[0]);
	void** arr_small_blocks = (void**) malloc(sizeof(void*) * options[0]);
        //initialize data structures which will  keep track of 
        //empty values in aforementioned arrays
	ListIndex* small_list = NULL;
        ListIndex* large_list = NULL;
	//number of blocks in each array	
	int small_blocks = 0;
	int large_blocks = 0;
	for(int i = 1; i <= options[0]; i++){
		//getmem freemem calls
		if(rand() % 100 < options[1]){
			//getmem: decide between small vs large blocks
			if(rand() % 100 < options[2]){
				//grab blocks with sizes between the small limit and large
				int random = ((rand() % options[4]) + options[3] + 1);
				large_list = store_block(&large_blocks, arr_large_blocks, large_list, random);
			} else {
				//grab blocks with sizes between 1 and the small limit
				int random = (rand() % options[3]) + 1;
				small_list = store_block(&small_blocks, arr_small_blocks, small_list, random);
			}
		} else {
			//freemem calls have a 50% chance of pulling a large or small block
			if(rand() % 100 >= 50){
				small_list = return_block(&small_blocks, arr_small_blocks, small_list);
			} else {
				large_list = return_block(&large_blocks, arr_large_blocks, large_list);
			}
		}
		//calls print method at increments of 10%
		if(i % (options[0] / 10) == 0){
			print_stats(comp);
		}
		print_heap(stdout);
	}
}

//PRE: takes a reference to an integer, num_blocks, a reference to a list of references to blocks,
//	arr_blocks, and an object to keep track of empty values in the lists of pointers, list.
//POST: returns a block to the freelist. Removes a block from a list containing previously allocated blocks,
//	adds a reference indicating the empty value to the parameter list and finally returns the newly modified
//	parameter, list.
ListIndex* return_block(int* num_blocks, void** arr_blocks, ListIndex* list){
	//test if the number of blocks is not 0
	if(*num_blocks != 0){
        	int random = rand() % *num_blocks;
                //call freemem returning a block to memory, update storage
                freemem(arr_blocks[random]);
                *num_blocks = *num_blocks - 1;
		arr_blocks[random] = NULL;
		//add node to list with random size field
		if(list){
			list = add_node(random, list);
		} else {
			list = create_node(random);
		}
	}
	return list;
}

//PRE: takes a references to an integer, num_blocks, a references to a list of references to blocks,
//	arr_blocks, an object to keep track of empty values in the lists of pointers, list and integer
//	keeping track of the lists of references to blocks.
//POST: removes a block from the freelist, stores a reference to that block, and keeps track of where
//	that value is stored, simultaneously updating any relevant experimental statistics..
ListIndex* store_block(int* num_blocks, void** arr_blocks, ListIndex* list, int size){
	*num_blocks = *num_blocks + 1;
	if(list){
		//insert reference to block, into array of blocks
		arr_blocks[list->value] = getmem(size);
		//update data structure containing locations of empty
		//values in block arrays.
		list = remove_node(list);
	} else {
		//case for handling empty array
		arr_blocks[*num_blocks - 1] = getmem(size);
	}
	return list;
} 

//PRE: takes an integer, size 
//POST: return a single new node, containing the parameter size
ListIndex* create_node(int size) {
	ListIndex* node = (ListIndex*)malloc(sizeof(ListIndex));;
	node->value = size;
	node->next = NULL;
	return node;
}

//PRE: takes an integer, size, and a reference to a struct  to store size, list.
//POST: adds a new node to list containing a reference to size
ListIndex* add_node(int size, ListIndex* list){
	ListIndex* temp = create_node(size);
	temp->next= list;
	list = temp;
	return list;
}

//PRE: takes a reference to a struct, list
//POST: remove a single node and return list
ListIndex* remove_node(ListIndex* list) {
	list = list->next;
	return list;
}

//PRE: takes a reference to the start time of the experiment, comp.
//POST: prints the total elapsed time of the experiment, the number of free blocks
//	available in freelist, and the average number of bytes in the freelist
void print_stats(clock_t comp) {
	clock_t curr = clock();
	uintptr_t total_mem;
	uintptr_t free_mem;
	uintptr_t free_blocks;
        uintptr_t mem_per_block;
	//test to avoid dividing by 0 when free_blocks = 0.
	get_mem_stats(&total_mem, &free_mem, &free_blocks);	
	if(free_blocks > 0){
		mem_per_block = (free_mem / free_blocks);
	} else {
		mem_per_block = 0;
	}
	//print statistics
	printf("time: %.2f\n", (float)(curr - comp)/CLOCKS_PER_SEC);
	printf("total amount of storage: %lu\n", total_mem);
	printf("total number of blocks: %lu\n", free_blocks);
	printf("average number of bytes: %lu\n", mem_per_block);
}
