/* vigenere known-answer test. Run: ./vigenere_test */

#include "vigenere.h"

#include <stdio.h>
#include <string.h>

struct kat {
    const char *plain;
    const char *key;
    const char *cipher;
};

static int test_vigenere(void) {
    struct kat cases[] = {
        {"ATTACKATDAWN", "LEMON", "LXFOPVEFRNHR"},
        {"hello", "key", "rijvs"},
        {"attack at dawn", "lemon", "lxfopv ef rnhr"},
        {"", "abc", ""},
    };

    int fails = 0;
    char buf[256];
    for (size_t i = 0; i < sizeof(cases) / sizeof(cases[0]); i++) {
        bool ok = vigenere_encode(cases[i].key, cases[i].plain, buf);
        if (!ok || strcmp(buf, cases[i].cipher) != 0) {
            printf("FAIL encode [%zu]: got '%s' want '%s'\n", i, buf,
                   cases[i].cipher);
            fails++;
            continue;
        }
        char back[256];
        vigenere_decode(cases[i].key, buf, back);
        if (strcmp(back, cases[i].plain) != 0) {
            printf("FAIL roundtrip [%zu]: got '%s' want '%s'\n", i, back,
                   cases[i].plain);
            fails++;
        }
    }

    /* empty key must fail */
    if (vigenere_encode("", "abc", buf)) {
        printf("FAIL: empty key should be rejected\n");
        fails++;
    }
    return fails;
}

int main(void) {
    int fails = test_vigenere();
    if (fails == 0) {
        printf("All vigenere tests passed.\n");
        return 0;
    }
    printf("%d vigenere test(s) failed.\n", fails);
    return 1;
}
