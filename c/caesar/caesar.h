/**
 * @file caesar.h
 * @brief Caesar cipher
 */

#ifndef CAESAR_H
#define CAESAR_H

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Encrypt @p plain with Caesar shift @p key into @p cipher.
 * @p cipher must have room for strlen(plain)+1 bytes.
 */
void caesar_encode(const char *plain, char *cipher, int key);

/**
 * Decrypt @p cipher with Caesar shift @p key into @p plain.
 * @p plain must have room for strlen(cipher)+1 bytes.
 */
void caesar_decode(const char *cipher, char *plain, int key);

#ifdef __cplusplus
}
#endif

#endif /* CAESAR_H */
