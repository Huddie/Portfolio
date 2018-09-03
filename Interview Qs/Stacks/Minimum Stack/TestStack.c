#include <stdio.h>
#include <stdlib.h>
#include "minstack.h"

typedef int (* in_comparator)(void *, void *);

int compareInt(void *firstvp, void *secondvp)
{
    int first_int  = *(int *)firstvp;
    int second_int = *(int *)secondvp;

    if(first_int > second_int)
    {
        return 1;
    }
    else if(second_int > first_int)
    {
        return -1;
    }
    else
    {
        return 0;
    }
}


int main (void) 
{
    

    // list *stack = list_init(&compareInt);
    
    // int one   = 3;
    // int two   = 2;
    // int three = 1;
    // int four  = 5;

    // void *vone   = &one;
    // void *vtwo   = &two;
    // void *vthree = &three;
    // void *vfour  = &four;
    
    // append(vone,stack);
    // append(vtwo,stack);
    // append(vthree,stack);
    // append(vfour,stack);
    
    // remove_start(stack);
    // remove_start(stack);
    // remove_start(stack);
    // remove_start(stack);

    // list_deinit(stack);


    return 0;
    
}