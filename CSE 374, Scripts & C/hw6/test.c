#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <inttypes.h>
#include "mem.h"

int main() {
	char* test1 = (char*)getmem(16);
	char* test2 = (char*)getmem(112);
	char* test3 = (char*)getmem(5);

	strncpy(test1, "World", 16);
	strncpy(test2, "Hello", 112);
	strncpy(test3, "Huh", 5);

	printf("Test: %s\n", test1);
        printf("Test: %s\n", test2);
        printf("Test: %s\n", test3);
	freemem(test1);
	freemem(test2);
	freemem(test3);

	FILE* file;
	print_heap(file);

	uintptr_t t;
	uintptr_t f;
	uintptr_t b;
	get_mem_stats(&t, &f, &b);
	printf("Total: %lu, Total Free: %lu, Total blocks: %lu\n", t, f, b);
	test1 = (char*)getmem(16);
	test2 = (char*)getmem(112);
	test3 = (char*)getmem(5);


	print_heap(file);

	char* test4 = (char*)getmem(200);
	printf("Test: %s\n", test1);
	printf("Test: %s\n", test2);
	printf("Test: %s\n", test3);
	printf("Test: %s\n", test4);
	get_mem_stats(&t, &f, &b);
	printf("Total: %lu, Total Free: %lu, Total blocks: %lu\n", t, f, b);
	return 0;
}
