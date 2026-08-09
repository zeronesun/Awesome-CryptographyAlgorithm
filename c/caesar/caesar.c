/**
 * @file caesar.c
 * @brief Caesar cipher implementation
 *
 * f(c) = (c + key) % 26  (letters only, case preserved)
 * Non-letter characters are copied unchanged.
 */

#include "caesar.h"

#include <ctype.h>
#include <stdlib.h>
#include <string.h>

static char caesar_single(char c, int shift) {
    if ('a' <= c && c <= 'z') {
        return (char)(((c - 'a' + shift) % 26 + 26) % 26 + 'a');
    }
    if ('A' <= c && c <= 'Z') {
        return (char)(((c - 'A' + shift) % 26 + 26) % 26 + 'A');
    }
    return c;
}

void caesar_encode(const char *plain, char *cipher, int key) {
    if (plain == NULL || cipher == NULL) {
        return;
    }
    for (size_t i = 0; plain[i] != '\0'; i++) {
        cipher[i] = caesar_single(plain[i], key);
    }
    cipher[strlen(plain)] = '\0';
}

void caesar_decode(const char *cipher, char *plain, int key) {
    if (cipher == NULL || plain == NULL) {
        return;
    }
    for (size_t i = 0; cipher[i] != '\0'; i++) {
        plain[i] = caesar_single(cipher[i], -key);
    }
    plain[strlen(cipher)] = '\0';
}

