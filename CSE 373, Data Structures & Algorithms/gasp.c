#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_LINE 500

void process(char* file) {
	FILE* input_file = fopen(file, "r");
	char line[MAX_LINE];
	while (getline(line, MAX_LINE, input_file) != -1) {
		puts(line);
	}
}

int main(int argc, char** argv) {
	process(argv[1]);
	return 0;
}
