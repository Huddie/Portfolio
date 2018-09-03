CC = gcc
ARGS = -Wall -g

all: TestStack minstack

minstack: minstack.c
	$(CC) -c $(ARGS) minstack.c

TestStack: TestStack.c minstack 
	$(CC) -o TestStack $(ARGS) TestStack.c minstack.o

clean:
	rm -rf *.o TestStack
	
	