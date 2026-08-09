/**
 * @file base64.h
 * @brief Base64 encoding and decoding (RFC 4648)
 */

#ifndef BASE64_H
#define BASE64_H

#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

/**
 * Encode binary data into a NUL-terminated Base64 string.
 * @param src  input bytes (may contain arbitrary binary data)
 * @param len  number of bytes in @p src
 * @return heap-allocated, NUL-terminated string. Free with base64_free().
 */
char *base64_encode(const unsigned char *src, size_t len);

/**
 * Decode a NUL-terminated Base64 string.
 * @param src  NUL-terminated Base64 string
 * @param out_len  receives the number of decoded bytes
 * @return heap-allocated decoded bytes. Free with base64_free().
 */
unsigned char *base64_decode(const char *src, size_t *out_len);

/** Free memory returned by base64_encode / base64_decode. */
void base64_free(void *ptr);

#ifdef __cplusplus
}
#endif

#endif /* BASE64_H */
