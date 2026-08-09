/**
 * @file vigenere.c
 * @brief Vigenère cipher implementation
 *
 * Multi-table substitution based on the Caesar cipher.
 * Only alphabetic characters are shifted; case is preserved.
 * Non-alphabetic characters are copied unchanged and do not consume a
 * key character.
 */

#include "vigenere.h"

#include <ctype.h>
#include <string.h>

static char vigenere_single(char c, int shift, bool encode) {
    if ('a' <= c && c <= 'z') {
        int v = c - 'a';
        v = encode ? (v + shift) % 26 : (v - shift % 26 + 26) % 26;
        return (char)(v + 'a');
    }
    if ('A' <= c && c <= 'Z') {
        int v = c - 'A';
        v = encode ? (v + shift) % 26 : (v - shift % 26 + 26) % 26;
        return (char)(v + 'A');
    }
    return c;
}

static int key_shift(const char *key, size_t key_len, size_t idx) {
    char k = key[idx % key_len];
    if ('a' <= k && k <= 'z') return k - 'a';
    if ('A' <= k && k <= 'Z') return k - 'A';
    return 0;
}

static bool vigenere_run(const char *key, const char *src, char *dst,
                         bool encode) {
    if (key == NULL || src == NULL || dst == NULL || key[0] == '\0') {
        return false;
    }
    size_t key_len = strlen(key);
    size_t ki = 0;
    for (size_t i = 0; src[i] != '\0'; i++) {
        char c = src[i];
        if (isalpha((unsigned char)c)) {
            int shift = key_shift(key, key_len, ki);
            dst[i] = vigenere_single(c, shift, encode);
            ki++;
        } else {
            dst[i] = c;
        }
    }
    dst[strlen(src)] = '\0';
    return true;
}

bool vigenere_encode(const char *key, const char *src, char *dst) {
    return vigenere_run(key, src, dst, true);
}

bool vigenere_decode(const char *key, const char *src, char *dst) {
    return vigenere_run(key, src, dst, false);
}
