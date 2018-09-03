///
///  MinStack.c
///
///  Created by Ehud Adler on 6/24/18.
///  Copyright © 2018 Ehud Adler. All rights reserved.
///

#include <stdlib.h>
#include "minstack.h"


list *list_init(int (* in_comparator)(void *, void *))
{
    list *llist = malloc(sizeof(list));
    llist->head = NULL;
    llist->tail = NULL;
    llist->size = 0;
    llist->comparator = in_comparator;
    return llist;
}

void list_deinit(list *in_list)
{
    free(in_list);
}

void *last(list *in_list)
{
    return in_list->tail->data;
} // _last

void *first(list *in_list)
{
    return in_list->head->data;
} //_first

void *min(list *in_list)
{
    return in_list->min->data;
} // _min

void append(void *in_data, list *in_list)
{
    
  node *newNode = malloc(sizeof(node)); // New node
  newNode->data = in_data;
  newNode->next = NULL;
  newNode->previous_minimum = NULL;
   
  if(in_list->head == NULL)  /* Empty */
  {
      in_list->head = in_list->tail = newNode;
      in_list->min  = newNode;
  }
  else 
  {
        newNode->next = in_list->head;
        in_list->head = newNode;
        
        switch(in_list->comparator (newNode->data, in_list->min->data))
        {
            case -1:
                newNode->previous_minimum = in_list->min;
                in_list->min = newNode;
                break;
            default:
                break; /* TODO : Throw error */
        }
  }
  in_list->size += 1;
} // _append

void remove_start(list *in_list)
{
    if(in_list->min == in_list->head && in_list->min->previous_minimum != NULL)
    {
        in_list->min  = in_list->min->previous_minimum;  
    }
    if(in_list->head->next != NULL)
    {
        node *temp    = in_list->head;
        in_list->head = in_list->head->next; 
        free(temp);
    }
} // _remove