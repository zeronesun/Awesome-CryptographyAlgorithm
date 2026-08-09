/**
 * @file base64.c
 * @brief Base64 encoding and decoding (RFC 4648)
 */

#include "base64.h"

#include <stdlib.h>
#include <string.h>

static const char BASE64_TABLE[] =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

static const int BASE64_DECODE_TABLE[256] = {
    [0 ... 255] = -1,
    ['A'] = 0,  ['B'] = 1,  ['C'] = 2,  ['D'] = 3,  ['E'] = 4,  ['F'] = 5,
    ['G'] = 6,  ['H'] = 7,  ['I'] = 8,  ['J'] = 9,  ['K'] = 10, ['L'] = 11,
    ['M'] = 12, ['N'] = 13, ['O'] = 14, ['P'] = 15, ['Q'] = 16, ['R'] = 17,
    ['S'] = 18, ['T'] = 19, ['U'] = 20, ['V'] = 21, ['W'] = 22, ['X'] = 23,
    ['Y'] = 24, ['Z'] = 25, ['a'] = 26, ['b'] = 27, ['c'] = 28, ['d'] = 29,
    ['e'] = 30, ['f'] = 31, ['g'] = 32, ['h'] = 33, ['i'] = 34, ['j'] = 35,
    ['k'] = 36, ['l'] = 37, ['m'] = 38, ['n'] = 39, ['o'] = 40, ['p'] = 41,
    ['q'] = 42, ['r'] = 43, ['s'] = 44, ['t'] = 45, ['u'] = 46, ['v'] = 47,
    ['w'] = 48, ['x'] = 49, ['y'] = 50, ['z'] = 51, ['0'] = 52, ['1'] = 53,
    ['2'] = 54, ['3'] = 55, ['4'] = 56, ['5'] = 57, ['6'] = 58, ['7'] = 59,
    ['8'] = 60, ['9'] = 61, ['+'] = 62, ['/'] = 63, ['='] = 0};

char *base64_encode(const unsigned char *src, size_t len) {
    if (src == NULL) {
        return NULL;
    }

    size_t encoded_len = ((len + 2) / 3) * 4;
    char *res = (char *)malloc(encoded_len + 1);
    if (res == NULL) {
        return NULL;
    }

    size_t i = 0;
    size_t j = 0;
    while (i < len) {
        unsigned int b0 = src[i];
        unsigned int b1 = (i + 1 < len) ? src[i + 1] : 0;
        unsigned int b2 = (i + 2 < len) ? src[i + 2] : 0;

        unsigned int triple = (b0 << 16) | (b1 << 8) | b2;

        res[j++] = BASE64_TABLE[(triple >> 18) & 0x3F];
        res[j++] = BASE64_TABLE[(triple >> 12) & 0x3F];
        res[j++] = (i + 1 < len) ? BASE64_TABLE[(triple >> 6) & 0x3F] : '=';
        res[j++] = (i + 2 < len) ? BASE64_TABLE[triple & 0x3F] : '=';

        i += 3;
    }

    res[encoded_len] = '\0';
    return res;
}

unsigned char *base64_decode(const char *src, size_t *out_len) {
    if (src == NULL || out_len == NULL) {
        return NULL;
    }

    size_t in_len = strlen(src);
    size_t groups = in_len / 4;

    /* Count padding '=' to compute output length. */
    int pad = 0;
    if (in_len >= 1 && src[in_len - 1] == '=') pad++;
    if (in_len >= 2 && src[in_len - 2] == '=') pad++;

    size_t decoded_len = groups * 3;
    if (pad <= 2 && decoded_len >= (size_t)pad) {
        decoded_len -= (size_t)pad;
    }

    unsigned char *res = (unsigned char *)malloc(decoded_len + 1);
    if (res == NULL) {
        return NULL;
    }

    size_t j = 0;
    for (size_t g = 0; g < groups; g++) {
        const char *p = src + g * 4;
        unsigned int v0 = (unsigned int)BASE64_DECODE_TABLE[(unsigned char)p[0]];
        unsigned int v1 = (unsigned int)BASE64_DECODE_TABLE[(unsigned char)p[1]];
        unsigned int v2 = (unsigned int)BASE64_DECODE_TABLE[(unsigned char)p[2]];
        unsigned int v3 = (unsigned int)BASE64_DECODE_TABLE[(unsigned char)p[3]];

        unsigned int triple = (v0 << 18) | (v1 << 12) | (v2 << 6) | v3;

        if (j < decoded_len) res[j++] = (unsigned char)((triple >> 16) & 0xFF);
        if (j < decoded_len) res[j++] = (unsigned char)((triple >> 8) & 0xFF);
        if (j < decoded_len) res[j++] = (unsigned char)(triple & 0xFF);
    }

    res[decoded_len] = '\0';
    *out_len = decoded_len;
    return res;
}

void base64_free(void *ptr) { free(ptr); }
