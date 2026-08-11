#pragma once
#include <stdint.h>
#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

/*
 * ChaCha20 (RFC 7539) 教学实现 — 每次调用生成最多 64 字节密钥流。
 * 警告: 本仓库为教学仓库;生产使用 OpenSSL EVP_chacha20 如果可用,或 libsodium。
 */

/* 初始化。key 32B, nonce 12B, counter 起始 */
void chacha20_init(void *ctx, const uint8_t key[32], const uint8_t nonce[12], uint32_t counter);

/*
 * 生成最多 64 字节的 ChaCha20 密钥流块。
 */
void chacha20_block(const void *init, uint8_t out[64]);

/*
 * ChaCha20 加密/解密(同一种操作)。返回值: outlen (≤64)
 */
size_t chacha20_keystream(void *ctx, uint8_t *out, size_t outlen);

/*
 * 一次性加密/解密(内部管理密钥流块)
 */
void chacha20_crypt_oneshot(const uint8_t key32[32],
                            const uint8_t nonce[12],
                            uint32_t counter,
                            const uint8_t *msg,
                            size_t msglen,
                            uint8_t *out);

#ifdef __cplusplus
}
#endif
