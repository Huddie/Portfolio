// Example program
#include <iostream>
#include <string>
#include <algorithm>  
#include <vector> 
#include <set>
#include <time.h>

long long missing_3(long long a[], long long b[], long long bsize) {
    long long sum = 0 ^ a[bsize];
    for (long long i = 0; i < bsize; i++) {
        sum ^= a[i];
        sum ^= b[i];
    }
    return sum;
}

long long missing_2(long long a[], long long b[], long long bsize) {
    long long asum = a[bsize], bsum = 0;
    for (long long i = 0; i < bsize; i++) {
        asum += a[i];
        bsum += b[i];
    }
    return asum-bsum;
}


long long missing_1(long long a[], long long b[], long long bsize) {
    std::set<long long> s1, s2;
    std::set<long long> result;
    std::set_difference(a, a+bsize+1, b, b+bsize, std::inserter(result, result.end()));
    return *result.begin();
}
    
     
bool isEven_2(long long a) {
    return !(a & 1);
}

bool isEven_1(long long a) {
    return !(a % 2);
}
    
    

int main()
{
    long long smaller_size = 1000000;
    long long check_even = 1_000_000_000_000;
    
    long long* a = new long long[smaller_size+1];
    long long* b = new long long[smaller_size];

    for(long long i = 0; i < smaller_size; i++){
        a[i] = b[i] = i;
    }
    a[smaller_size] = 1434;
    // std::cout << missing_1(a, b, smaller_size) << std::endl;
    clock_t tStart = clock();
    std::cout << "Start List Test\n";

    std::cout << missing_2(a, b, smaller_size) << std::endl;
    printf("Time taken: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
    tStart = clock();
    std::cout << missing_3(a, b, smaller_size) << std::endl;
    printf("Time taken: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);

    std::cout << "Start Even Test\n";

    tStart = clock();
    for(long long i = 0; i < check_even; i++) isEven_1(check_even);
    printf("Time taken: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);
    tStart = clock();
    for(long long i = 0; i < check_even; i++) isEven_2(check_even);
    printf("Time taken: %.2fs\n", (double)(clock() - tStart)/CLOCKS_PER_SEC);

}






