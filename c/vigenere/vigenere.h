/**
 * @file vigenere.h
 * @brief Vigenère cipher
 */

#ifndef VIGENERE_H
#define VIGENERE_H

#include <stdbool.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Encrypt @p src using Vigenère key @p key into @p dst.
 * @p dst must have room for strlen(src)+1 bytes.
 * @return true on success, false if the key is empty.
 */
bool vigenere_encode(const char *key, const char *src, char *dst);

/** Decrypt @p src using Vigenère key @p key into @p dst. */
bool vigenere_decode(const char *key, const char *src, char *dst);

#ifdef __cplusplus
}
#endif

#endif /* VIGENERE_H */
