/* hmac known-answer test (RFC 4231 vectors). Run: ./demo-test */

#include "hmac.h"

#include <stdio.h>
#include <string.h>

static const char *to_hex(const BYTE *d, char *buf)
{
    for (int i = 0; i < HMAC_SHA256_DIGEST_SIZE; i++)
        sprintf(buf + 2 * i, "%02x", d[i]);
    return buf;
}

int main(void)
{
    BYTE out[HMAC_SHA256_DIGEST_SIZE];
    char got[2 * HMAC_SHA256_DIGEST_SIZE + 1];
    int fails = 0;

    /* RFC 4231 Test Case 1 */
    const BYTE key1[20] = {
        0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b,
        0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b, 0x0b
    };
    hmac_sha256(key1, sizeof(key1), (const BYTE *)"Hi There", 8, out);
    if (strcmp(to_hex(out, got),
               "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7")) {
        printf("FAIL hmac TC1: got %s\n", got);
        fails++;
    }

    /* RFC 4231 Test Case 2 */
    hmac_sha256((const BYTE *)"Jefe", 4,
                (const BYTE *)"what do ya want for nothing?", 28, out);
    if (strcmp(to_hex(out, got),
               "5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843")) {
        printf("FAIL hmac TC2: got %s\n", got);
        fails++;
    }

    if (fails == 0) {
        printf("All hmac tests passed.\n");
        return 0;
    }
    printf("%d hmac test(s) failed.\n", fails);
    return 1;
}
