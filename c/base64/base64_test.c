/* base64 known-answer test. Run: ./base64_test */

#include "base64.h"

#include <stdio.h>
#include <string.h>

struct kat {
    const char *plain;
    const char *b64;
};

static int test_base64(void) {
    struct kat cases[] = {
        {"Hello", "SGVsbG8="},
        {"", ""},
        {"f", "Zg=="},
        {"fo", "Zm8="},
        {"foo", "Zm9v"},
        {"foob", "Zm9vYg=="},
        {"fooba", "Zm9vYmE="},
        {"foobar", "Zm9vYmFy"},
    };

    int fails = 0;
    for (size_t i = 0; i < sizeof(cases) / sizeof(cases[0]); i++) {
        const char *plain = cases[i].plain;
        size_t len = strlen(plain);

        char *enc = base64_encode((const unsigned char *)plain, len);
        if (enc == NULL || strcmp(enc, cases[i].b64) != 0) {
            printf("FAIL encode [%zu]: got '%s' want '%s'\n", i,
                   enc ? enc : "(null)", cases[i].b64);
            fails++;
            base64_free(enc);
            continue;
        }

        size_t dec_len = 0;
        unsigned char *dec = base64_decode(enc, &dec_len);
        if (dec == NULL || dec_len != len ||
            memcmp(dec, plain, len) != 0) {
            printf("FAIL decode [%zu]\n", i);
            fails++;
        }

        base64_free(enc);
        base64_free(dec);
    }
    return fails;
}

int main(void) {
    int fails = test_base64();
    if (fails == 0) {
        printf("All base64 tests passed.\n");
        return 0;
    }
    printf("%d base64 test(s) failed.\n", fails);
    return 1;
}
