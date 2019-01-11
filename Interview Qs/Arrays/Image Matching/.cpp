#include <bits/stdc++.h>

using namespace std;

string ltrim(const string &);
string rtrim(const string &);



/*
 * Complete the 'countMatches' function below.
 *
 * The function is expected to return an INTEGER.
 * The function accepts following parameters:
 *  1. STRING_ARRAY grid1
 *  2. STRING_ARRAY grid2
 */
bool floodfill(vector<string> &grid1, vector<string> &grid2, pair<int,int> start, bool state) {

    bool leftBool, downBool, rightBool, upBool;
    leftBool = rightBool = upBool = downBool = true;

    if(start.first >= grid1.size() 
    || start.second < 0 
    || start.first < 0 
    || start.second >= grid1[0].length()
    ) return state;

    if(grid1[start.first][start.second] != grid2[start.first][start.second]) state = false;
    else if (grid1[start.first][start.second] != '1') return state;
    
    grid1[start.first][start.second] = grid2[start.first][start.second] = '0';
    
    // Right
    if (start.first < grid1.size() - 1)  
        rightBool = floodfill(grid1, grid2, make_pair(start.first + 1,start.second), state);
    // Left
    if(start.first > 0)
        leftBool = floodfill(grid1, grid2, make_pair(start.first - 1,start.second), state);
    // Up
    if(start.second > 0)
        upBool = floodfill(grid1, grid2, make_pair(start.first,start.second - 1), state);
    // Down
    if(start.second < grid1[0].length() - 1)
        downBool = floodfill(grid1, grid2, make_pair(start.first,start.second + 1), state);

    return state && rightBool && downBool && upBool && leftBool;
}

int countMatches(vector<string> grid1, vector<string> grid2) {
    // Driver
    int row_length = grid1.size(), row, col;
    int ans = 0;
    for(int i = 0; i < row_length * row_length; i += 1) {
        row = i / row_length;
        col = i % row_length;
        if(grid1[row][col] == '1' && grid2[row][col] == '1') {
            ans += floodfill(grid1, grid2, make_pair(row, col), true) ? 1 : 0;
        }
    }
    return ans;
}

int main()