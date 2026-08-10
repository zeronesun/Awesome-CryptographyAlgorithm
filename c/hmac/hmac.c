/*********************************************************************
* Filename:   hmac.c
* Details:    HMAC-SHA256 (RFC 2104) implemented on top of SHA-256.
*              - B = 64  (SHA-256 compression-block size)
*              - L = 32  (SHA-256 digest size)
*              - ipad = 0x36, opad = 0x5c
*              If the key is longer than B it is first hashed (SHA-256).
*********************************************************************/

#include "hmac.h"
#include "../sha256/sha256.h"   // sha256_init / sha256_update / sha256_final

#include <string.h>

#define HMAC_BLOCK_SIZE 64       // SHA-256 block size (NOT sha256.h's macro)

void hmac_sha256(const BYTE *key, size_t key_len,
                 const BYTE *data, size_t data_len,
                 BYTE out[HMAC_SHA256_DIGEST_SIZE])
{
    BYTE k[HMAC_BLOCK_SIZE];
    SHA256_CTX ctx;
    BYTE inner[HMAC_SHA256_DIGEST_SIZE];

    /* ---- normalize key to exactly B bytes ---- */
    if (key_len > HMAC_BLOCK_SIZE) {
        sha256_init(&ctx);
        sha256_update(&ctx, key, key_len);
        sha256_final(&ctx, k);                 // k now holds L bytes
        memset(k + HMAC_SHA256_DIGEST_SIZE, 0,
               HMAC_BLOCK_SIZE - HMAC_SHA256_DIGEST_SIZE);
    } else {
        memcpy(k, key, key_len);
        memset(k + key_len, 0, HMAC_BLOCK_SIZE - key_len);
    }

    /* ---- derive ipad / opad key blocks ---- */
    BYTE k_ipad[HMAC_BLOCK_SIZE];
    BYTE k_opad[HMAC_BLOCK_SIZE];
    for (int i = 0; i < HMAC_BLOCK_SIZE; i++) {
        k_ipad[i] = (BYTE)(k[i] ^ 0x36);
        k_opad[i] = (BYTE)(k[i] ^ 0x5c);
    }

    /* ---- inner = SHA256(k_ipad || data) ---- */
    sha256_init(&ctx);
    sha256_update(&ctx, k_ipad, HMAC_BLOCK_SIZE);
    sha256_update(&ctx, data, data_len);
    sha256_final(&ctx, inner);

    /* ---- outer = SHA256(k_opad || inner) ---- */
    sha256_init(&ctx);
    sha256_update(&ctx, k_opad, HMAC_BLOCK_SIZE);
    sha256_update(&ctx, inner, HMAC_SHA256_DIGEST_SIZE);
    sha256_final(&ctx, out);
}
