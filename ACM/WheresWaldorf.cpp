//
//  main.cpp
//  Skiena
//
//  Created by Ehud Adler on 12/27/17.
//  Copyright © 2017 Ehud Adler. All rights reserved.
//

/// Main Data Structure: vecVector (typedeffed 2D Vector) 
/// Reason: Built in, Quick insert, Quick iteration
///
/// Main Algorithm: Iterative Search
/// Reason: Unsorted vectors, easy implementation with vectors
///
/// Steps:
/// 1: Select char
/// 2: Loop through vecVector for matching char
/// 3: Treverse in all directions from that point for matching string


#include <iostream>
#include <string>
#include <vector>
#include <iterator>

typedef std::vector < std::vector<char> > vecVector;

#define repeat(n) for(int _x = 0; _x < n; ++_x)
#define forever() while(true)

bool search(
  const std::string,
  const unsigned int&, const unsigned int&,
  const vecVector&,
  const unsigned int &,
  const unsigned int &
  );
  
void find_first_letter(
  const vecVector&, 
  const std::string, 
  const unsigned int &, 
  const unsigned int &
  );

void findWaldorf();

/// Main hands off the workload to find waldorf function

int main() { findWaldorf(); }

/// The find waldorf function take an initial user input for number of cases and
/// proceeds to loop that many times, each loop getting a set of row and column values,
/// and 2d string array of input (line by line)
///
/// It then takes another user input for number of strings and proceeds to loop that
/// many times, each loop taking in a string and passing it into the find_first_letter
/// function 

void findWaldorf () {
  
  int number_of_inputs,
  number_of_rows,
  number_of_columns,
  number_of_strings;
  
  number_of_inputs = number_of_rows = number_of_columns = number_of_strings = 0;
  
  std::cin >> number_of_inputs; 
  
  vecVector matrix;

  repeat(number_of_inputs) {

    std::cin >> number_of_rows;               
    std::cin >> number_of_columns; 

    matrix.clear(); 
    matrix.resize(
      number_of_columns, 
      std::vector<char>(number_of_rows, 0)
    );
    
    repeat(number_of_rows) {
      std::string line;
      std::cin >> line;
      for(unsigned int c = 0; c < number_of_columns; c += 1) matrix[c][_x] = line[c];
    }

    std::cin  >> number_of_strings;           
    std::cout << std::endl; 
  
    repeat(number_of_strings) {
      std::string line;
      std::cin >> line;

      find_first_letter(
        matrix, line,
        number_of_rows,
        number_of_columns
      );

    }
  }
}

/// The find first letter function loops over the input string array 
/// looks at the first letter of each string and calls the search func.
/// on that char. 
///
/// If the word exists, it prints the starting cord.
/// Once it loops through all possible strings, it exits the program.

void find_first_letter(
  const vecVector& matrix,
  const std::string s,
  const unsigned int& rows,
  const unsigned int& cols) {

    for (unsigned int row = 0; row < rows; row += 1)
          for (unsigned int item = 0; item < cols; item += 1)
              if(tolower(matrix[item][row]) == tolower(s[0]))         
                  if( search(s, row, item, matrix, rows, cols) ) {
                    std::cout << row+1 << ' ' << item+1 << std::endl;
                    return;
                  }
}

/// The search function will loop through a 2d array where each element 
/// is a 2 element array containing the (x, y) offset requried to direct
/// the loop in the proper direction. [0, 1] for example will shift the 
/// matrix right ->. 
///
/// The loop will iterate until it either finds an invalid sequence (break)
/// and try the next direction or it finds a complete sequence and returns true
/// 
/// -returns: bool 

bool search(
  const std::string s, 
  const unsigned int& row, 
  const unsigned int& col,
  const vecVector& matrix, 
  const unsigned int& rows, 
  const unsigned int& cols) {

                  //  R  C
  int dirs[8][2] = {{ 0, 1},  // UP
                    { 1, 1},  // UP_RIGHT
                    { 1, 0},  // RIGHT
                    { 1,-1},  // DOWN_RIGHT
                    { 0,-1},  // DOWN
                    {-1,-1},  // DOWN_LEFT
                    {-1, 0},  // LEFT
                    {-1, 1}}; // UP_LEFT
                    
  for (auto dir : dirs) { 
    int index = 0;
    int test_row = row;
    int test_col = col;
    
    forever() {
      
      if ( test_row > rows 
        || test_col > cols 
        || test_col < 0 
        || test_row < 0) break; 
      
      char test_char = matrix[test_col][test_row];            
      if(tolower(s[index]) != tolower(test_char)) break;      
      else if (index == s.size() - 1) return true;            
      
      test_row += dir[0];                                 
      test_col += dir[1];                                  
      index += 1;

    }
  }
  return false;
}