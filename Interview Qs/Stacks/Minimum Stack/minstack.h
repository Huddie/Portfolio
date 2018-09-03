///
///  MinStack.h
///
///  Created by Ehud Adler on 6/24/18.
///  Copyright © 2018 Ehud Adler. All rights reserved.
///

#ifndef MINSTACK_H
#define MINSTACK_H

#include <stdio.h>
#include <stdlib.h>

typedef struct node 
{
   void *data;
   struct node *next;
   struct node *previous_minimum;
}node;

typedef struct list 
{
  struct node *head;
  struct node *tail;
  struct node *min;
  int size;

  // Should return -1 for < , 0 for equal , 1 for greater
  int (* comparator)(void *, void *); 
  
}list;

list *list_init(int (* in_comparator)(void *, void *)); 
void list_deinit(list *in_list); 

void *last(list *in_list);
void *first(list *in_list);

void append(void *data, list *in_list);
void remove_start(list *in_list); 

#endif