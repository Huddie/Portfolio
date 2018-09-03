'''
    Takes a bunch if input numbers and determines whether or not each is a carmichael number or not
    @author: ehudadler
    Date: 3/18/18
    Language: Python 3.0
    Carmichael Numbers
'''

import math

''' 
    Purpose: Checks if the input number is prime
    n: number to test primality 
    return: if n is prime
'''
def is_prime(n):
    if n % 2 == 0 and n > 2: 
        return False
    return all(n % i for i in range(3, int(math.sqrt(n)) + 1, 2))

'''
    Purpose: Run the fermant test with a given a (base) and n (power)
    a: a value in fermants test (base)
    n: n value in fermants test (power) as divisor
    return: if a,n passes fermants test
'''
def fermants(a, n):
    return pow(a,n,n) == a


inValue = int(input())

while inValue != 0 :
    # Check if number is prime
    if(is_prime(inValue)):
        print(inValue, "is normal.")
    else:
        carmichael = True
        # Loop through all numbers 0 - inputValue checking if it passed fermant test
        for index in range(0, inValue) :
            if not fermants(index, inValue) :
                carmichael = False
                break
        if  carmichael:
            print("The number", inValue, "is a Carmichael number.")
        else:
            print(inValue, "is normal.")
    inValue = int(input()) # Get new input 
    
    
    


