t9: t9main.o t9trie.o t9io.o
	gcc -std=c11 -Wall -g -o t9 t9main.o t9trie.o t9io.o

t9main.o: t9main.c
	gcc -std=c11 -Wall -g -c t9main.c

t9trie.o: t9trie.c t9trie.h
	gcc -std=c11 -Wall -g -c t9trie.c

t9io.o: t9io.c t9io.h t9trie.h
	gcc -std=c11 -Wall -g -c t9io.c
