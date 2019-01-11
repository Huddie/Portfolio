#include <iostream>
#include <string>
#include <vector>
#include <random>

class RSA {
        private:
                long e, d, mod, blocksize;
        public:
                RSA(long exp, long mod);
                RSA();
                void generate_public_key();
                std::string encrpyt(std::string);
                std::string decrpyt(std::string);
}

RSA::RSA(long exp, long mod) {

}

RSA::RSA() {
        this->generate_public_key();
}

void RSA::extract_twos(long &p) {
        double temp = p / 2;
        while(temp % 2 != 0) {
                p = temp;
                temp = p / 2;
        }
}

bool RSA::is_exotic_sqrt_of_one(long n) {
        return n == 1;
}

long RSA::power_mod(long base, long pow, long modulus) {
        base %= modulus;
        long result = 1;
        while (exp > 0) {
                if (exp & 1) {
                        result = (result * base) % modulus;
                }
                base = (base * base) % modulus;
                exp >>= 1;
        }
        return result;
}

bool RSA::is_prob_prime(long p) {
        //Miller-Rabin test
        long n = p-1, y, base = 2;
        this->extract_twos(n);
        y =

}

long generate_rand_num(long lower_bound, long upper_bound) {
        std::mt19937 rng;
        rng.seed(std::random_device()());
        std::uniform_int_distribution<std::mt19937::long> dist(lower_bound, upper_bound);
        return dist(rng);
}

long RSA::find_nearest_prime(long n) {
        while(!this->is_prob_print(n)) {
                n += 2;
        }
        return n;
}

void RSA::generate_public_key() {
        long starting_number1 = std::pow(2, generate_rand_num(100,1000)) - 1;
        long starting_number2 = std::pow(2, generate_rand_num(100,1000)) - 1;¬
        long prime1 = find_nearest_prime(starting_number1);
        long prime2 = find_nearest_prime(starting_number2);¬
        this->e = generate_rand_num(100,1000);
        this->mod = prime1 * prime2;
}
std::string RSA::decrypt_block(std::string str) {

}
std::string RSA::encrypt_block(std::string str) {

}
std::string RSA::encrypt(std::string str) {
        std::string encrypted_str = "";
        std::vector<std::string> blocks = splitstr_by_blocksize(str);
        for(std::string block : blocks) {
                encrypted_str += this->encrypt_block(block);
        }
        return encrypted_str;
}

std::string RSA::decrpyt(std::string str) {
        std::string decrypted_str = "";
        std::vector<std::string> blocks = splitstr_by_blocksize(str);¬
        for(std::string block : blocks) {¬
             decrypted_str += this->decrypt_block(block);¬
        }
        return decrypted_str;
}
