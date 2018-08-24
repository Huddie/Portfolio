/// Is Bigger Smarter? 
/// UVA Online Judge
/// https://bit.ly/2Kh8UoR
///
/// Problem Summery: Given an Nx2 matrix in the format
/// Elephant Weight, Elephant IQ
/// construct the greatest sequence of elephants that follow
/// the rules the elephant before you must be Heavier and Lower IQ
/// 
/// Author Ehud Adler
/// Lang   C/CPP
/// Status Accepted 
/// Date   06/24/18
///


// Includes
#include <cstdio>
#include <stdlib.h>
#include <string.h>
#include <vector>

// Typedefs
typedef unsigned int uint;
typedef std::vector<int> int_vec;
typedef std::vector<int_vec> int_matrix;

// Variables
const int MAX_NUMBER_OF_ELEPHANTS = 1000;
const int MAX_IQ_AND_WEIGHT = 10000;
const int COLUMNS = 2;

long glb_number_of_elephant;
int_matrix glb_memo_table; 


/// Gets input from the console and returns a Nx2 matrix
/// consisting of "elephants" in each index represented in the format
/// column 0: elephant weight, column 1: elephant IQ
int **get_input_set(void) {
    
    int row = 0, elephant;
    bool column = false; // Flips from FALSE ( 0 ) -> TRUE ( 1 ). This will represent the columns
    
    // Allocate mem.
    int **elephant_array = (int **)malloc(sizeof(int *) * MAX_NUMBER_OF_ELEPHANTS);
    elephant_array[0] = (int *)malloc(sizeof(int) * COLUMNS * MAX_NUMBER_OF_ELEPHANTS);

    for(uint index = 0; index < MAX_NUMBER_OF_ELEPHANTS; index++)
        elephant_array[index] = (*elephant_array + (COLUMNS * index) /* Offset */);
        
    while (scanf("%d", &elephant) != EOF)  {
        elephant_array[row][column] = elephant;
        row += column;
        column = !column;
    } 
    
    glb_number_of_elephant = row;
    
    return elephant_array;
    
} // get_input_set

/// Recursivly loops over each elephant finding the greates acceptable sequence.
/// Each G.A.S is stored in the memo_table for optomization 
int_vec is_big_smart(
    uint pre_weight,
    uint pre_IQ,
    uint pre_elephant,
    int ** elephant_array) {
    
    int_vec acceptables;  
    for(
        uint index = 0; 
        index < glb_number_of_elephant; 
        index += 1
    ) {
        if(elephant_array[index][0] > pre_weight && elephant_array[index][1] < pre_IQ) {
            int_vec possible_new_best;
            
            // Check Memoization 
            // If the memoized table contains data forgo the recursive call
            // and test the possible_new_best
            if(!glb_memo_table[index].empty()) {
                possible_new_best = glb_memo_table[index];
            }
            else {
                // Recursive call
                // Test all the other possible sequences to find the
                // new_possible_best leading up to this point.
                possible_new_best = is_big_smart(elephant_array[index][0],  elephant_array[index][1], index, elephant_array);
            }
           
            if(possible_new_best.size() > acceptables.size()) {
                acceptables = possible_new_best;
            }
        } 
    } 

    acceptables.push_back(pre_elephant + 1); // +1 since indicies start at 0 but we want to start at 1
    
    // Memoization
    // Fill the memo_table with the current acceptables 
    // since this is (and always will be) the best sequence up until this point
    glb_memo_table[pre_elephant] = acceptables;
    
    return acceptables;
    
} // is_big_smart


void initialize_memo_table(void) {
    glb_memo_table.resize(glb_number_of_elephant);
} // initialize_memo_table


int main(void) {
    
    int **elephant_array = get_input_set();
    
    initialize_memo_table();
    
    int_vec answer = is_big_smart(
        0,
        MAX_IQ_AND_WEIGHT + 1,
        0,
        elephant_array
    );

    
    answer.pop_back(); // Remove initial 1
    
    printf("%ld\n",answer.size());
    while(!answer.empty()) {
        printf("%d\n",answer.back());
        answer.pop_back();
    }

    // Memory Managment
    free(elephant_array[0]);
    free(elephant_array);
    
    return 0;
} // main




