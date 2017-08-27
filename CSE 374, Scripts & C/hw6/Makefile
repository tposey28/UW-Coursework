# Makefile for bench program
# CSE 374, Brandon Myers
# Taylor Posey & Ian Durra

MEMHEADER = getmem.h freemem.h get_mem_stats.h print_heap.h mem_impl.h mem.h
MEMSOURCE = getmem.c freemem.c get_mem_stats.c print_heap.c
MEMPACK = getmem.o freemem.o get_mem_stats.o print_heap.o

# default target
bench: bench.o $(MEMPACK)
	gcc -std=c11 -Wall -g -o bench bench.o $(MEMPACK)

# source files
bench.o: bench.c bench.h mem.h
	gcc -std=c11 -Wall -g -c bench.c

getmem.o: getmem.c getmem.h mem_impl.h
	gcc -std=c11 -Wall -g -c getmem.c

freemem.o: freemem.c freemem.h mem_impl.h
	gcc -std=c11 -Wall -g -c freemem.c

get_mem_stats.o: get_mem_stats.c get_mem_stats.h mem_impl.h
	gcc -std=c11 -Wall -g -c get_mem_stats.c

print_heap.o: print_heap.c print_heap.h mem_impl.h
	gcc -std=c11 -Wall -g -c print_heap.c

# run default bench test
test.: bench.o $(MEMPACK) 
	./bench

# distribute program source files and documentation (.tar file)
dist.: clean
	git log >git.log
	tar -cf hw6.tar $(MEMSOURCE) $(MEMHEADER) Makefile bench.c bench.h README git.log

# cleans all but the original source files from current directory
clean:
	rm -f *.o .*.swp *.bak bench *~ git.log hw6.tar
