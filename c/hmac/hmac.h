/*********************************************************************
* Filename:   hmac.h
* Details:    API for HMAC-SHA256 (RFC 2104) built on the SHA-256
*             implementation in ../sha256/sha256.h.
*********************************************************************/

#ifndef HMAC_H
#define HMAC_H

/*************************** HEADER FILES ***************************/
#include <stddef.h>

/****************************** MACROS ******************************/
#define HMAC_SHA256_DIGEST_SIZE 32  // HMAC-SHA256 produces a 32-byte tag

/**************************** DATA TYPES ****************************/
typedef unsigned char BYTE;         // 8-bit byte

/*********************** FUNCTION DECLARATIONS **********************/
/*
 * Compute HMAC-SHA256 over `data` (length data_len) keyed by `key`
 * (length key_len). Result is written into out (32 bytes).
 */
void hmac_sha256(const BYTE *key, size_t key_len,
                 const BYTE *data, size_t data_len,
                 BYTE out[HMAC_SHA256_DIGEST_SIZE]);

#endif // HMAC_H
