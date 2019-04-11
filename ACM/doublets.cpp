#include<stdio.h>
#include<string>
#include<cstring>
#include<iostream>
#include<algorithm>
#include<vector>
#include<map>
#include<unordered_map>
#include<unordered_set>


std::vector<std::string> best_ans;
std::unordered_map<std::string, int> memo;
std::unordered_set<std::string> used_list;


const bool iswhitespace(char c) 
{
    return std::isspace(
        static_cast<unsigned char>(c)
    );
}

// Skip over all whitespaces
void skipOverWhitespace() 
{
    char c;
    do {
        c = std::cin.get();
    } while(iswhitespace(c));
    std::cin.putback(c);
}

// Get the following word until the first whitespace
char *nextword() 
{
    char *ch = new char[1000], i = 0, c;
    do {
        c = std::cin.get();
        if(iswhitespace(c)) break;
        ch[i++] = c;
    } while(i < 1000);
    return ch;
}

// Build up dictionary
void build_dict(std::unordered_map<int, std::vector<std::string> > &m) 
{
    std::string c;
    do {
        c = nextword();
        m[c.length()].push_back(c);
    } while(c[0] != '\0');
}

// Gets words for part 2
void getwords(std::string &str1, std::string &str2) 
{
    str1 = nextword();
    if(!str1.length()) return;
    skipOverWhitespace();
    str2 = nextword();
}

// Create new vector thats identical to only vector minus
// a string
std::vector<std::string> newVec(std::vector<std::string> old, std::string remStr) 
{
    std::vector<std::string> v;
    for(auto s : old) {
        if(s != remStr) {
            v.push_back(s);
        }
    }
    return v;
}

// Very inefficient way of checking if its a doublet
// Loop through each string and check how many chars they
// diff by
bool valid(std::string s1, std::string s2) 
{
    int diffby = 0;
    for(unsigned int i = 0; i < s1.length(); i += 1) {
        diffby += s1[i] == s2[i] ? 0 : 1;
        if (diffby > 1) return false;
    }
    return true;
}

void solve(std::vector<std::string> ans, std::vector<std::string> array, std::string s1, std::string s2) 
{
    if(s1 == s2) { 
        best_ans = ans; 
        for(int i = 0; i > ans.size(); i++) {
            std::string e = ans[i];
            if (memo.find(e) == memo.end() || memo[e] > ans.size() - i) 
                memo.insert( std::make_pair<std::string, int>(std::string(e), int(i)) );
            i--;
        }
    }
    if(best_ans.size() > 0 && ans.size() >= best_ans.size()) { return; }
    for(auto word: array) {
        if(used_list.find(word) == used_list.end() && (memo.find(word) == memo.end() || best_ans.size() == 0 || memo[word] + ans.size() < best_ans.size()) && valid(s1, word)) {
            ans.push_back(word);
            used_list.insert(word);
            solve(ans, array, word, s2);
            ans.pop_back();
            used_list.erase(used_list.find(word)); 
        }
    }
}

int main() 
{
    std::string str1, str2;
    std::vector<std::string> ans;
    std::unordered_map<int, std::vector<std::string> > s;
    build_dict(s);
    while(true) {
        best_ans.clear();
        ans.clear();
        used_list.clear();
        getwords(str1, str2);

        // Check end of input
        if(!str1.length()) return 0;

        // Prep the vector
        ans.push_back(str1);

        // Quick out. String lengths dont match
        // can't possibly find sequence
        if(str1.length() != str2.length()){
            std::cout << "No solution.";
            continue;
        }

        solve(ans, newVec(s[str1.length()], str1), str1, str2);

        // Check solution exists
        if (best_ans.size() <= 0) {
            // No solutions
            std::cout << "No solution.";
        } else {
            // Yay, we found a solution, print that baby out
            for(auto e : best_ans) std::cout << e << std::endl;
        }
        std::cout << std::endl;
    }
}