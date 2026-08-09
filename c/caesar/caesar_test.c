/* caesar known-answer test. Run: ./caesar_test */

#include "caesar.h"

#include <stdio.h>
#include <string.h>

struct kat {
    const char *plain;
    int key;
    const char *cipher;
};

static int test_caesar(void) {
    struct kat cases[] = {
        {"HELLO", 3, "KHOOR"},
        {"hello", 3, "khoor"},
        {"xyz", 1, "yza"},
        {"Attack at dawn!", 5, "Fyyfhp fy ifbs!"},
        {"", 7, ""},
    };

    int fails = 0;
    char buf[256];
    for (size_t i = 0; i < sizeof(cases) / sizeof(cases[0]); i++) {
        caesar_encode(cases[i].plain, buf, cases[i].key);
        if (strcmp(buf, cases[i].cipher) != 0) {
            printf("FAIL encode [%zu]: got '%s' want '%s'\n", i, buf,
                   cases[i].cipher);
            fails++;
            continue;
        }
        char back[256];
        caesar_decode(buf, back, cases[i].key);
        if (strcmp(back, cases[i].plain) != 0) {
            printf("FAIL roundtrip [%zu]: got '%s' want '%s'\n", i, back,
                   cases[i].plain);
            fails++;
        }
    }
    return fails;
}

int main(void) {
    int fails = test_caesar();
    if (fails == 0) {
        printf("All caesar tests passed.\n");
        return 0;
    }
    printf("%d caesar test(s) failed.\n", fails);
    return 1;
}
