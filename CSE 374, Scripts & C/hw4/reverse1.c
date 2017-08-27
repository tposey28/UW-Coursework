/*
 * Ask user for a word and print it forwards and backwards.
 * CSE 303/374 demo (for debugging), 10/08-1/13.  HP
 * 1/14 LC - split demo into 4 files with annotated changes.
 *
 * Compile with -g option to use the gdb debugger.
 * This iteration of the program crashes with a seg fault.
 */

#define MAX_STR 100   /* length of longest input string */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* Return a new string with the contents of s backwards */
char * reverse(char * s) {

  char * result = NULL;  /* the reversed string */
  int L, R;
  char ch;

  /* copy original string then reverse and return the copy */
  strcpy(result,s);
  
  L = 0;
  R = strlen(result);

  while (L < R) {
    ch = result[L];
    result[L] = result[R];
    result[R] = ch;
    L++; R--;
  }
  
  return result;
}


/* Ask the user for a string, then print it forwards and backwards.     */
int main() {

  char line[MAX_STR];    /* original input line */
  char * rev_line;       /* backwards copy from reverse function */
  
  printf("Please enter a string: ");
  fgets(line, MAX_STR, stdin);

  rev_line = reverse(line);

  printf("The original string was:   >%s<\n", line);
  printf("Backwards, that string is: >%s<\n", rev_line);
  printf("Thank you for trying our program.\n");

  return 0;
}
