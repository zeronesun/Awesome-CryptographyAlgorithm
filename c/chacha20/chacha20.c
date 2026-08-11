#include "chacha20.h"
#include <stdint.h>
#include <string.h>

/* ChaCha20 常量 "expand 32-byte k" */
static const uint32_t CHACHA20_SIGMA[4] = {
    0x61707865,  /* "expand" */
    0x3320646e,  /* "32-by" */
    0x79622d32,  /* "te k" */
    0x6b206574   /* "k" */
};

/* 内部状态: 16 × uint32 */
typedef struct {
    uint32_t state[16];
} chacha20_state;

/* QR (Quarter Round) — RFC 7539 第 2.1 节。每轮对四个 32 位变量混合。 */
static void chacha20_quarterround(uint32_t *a, uint32_t *b, uint32_t *c, uint32_t *d)
{
    *a += *b; *d ^= *a; *d = (*d << 16) | (*d >> 16);
    *c += *d; *b ^= *c; *b = (*b << 12) | (*b >> 20);
    *a += *b; *d ^= *a; *d = (*d << 8)  | (*d >> 24);
    *c += *d; *b ^= *c; *b = (*b << 7)  | (*b >> 25);
}

/* ChaCha20 双轮函数（2 列轮 + 2 对角轮 = 8 次 QR） — RFC 7539 Section 2.1 */
static void chacha20_double_round(chacha20_state *s)
{
    /* Column rounds */
    chacha20_quarterround(&s->state[0], &s->state[4], &s->state[8], &s->state[12]);
    chacha20_quarterround(&s->state[1], &s->state[5], &s->state[9], &s->state[13]);
    chacha20_quarterround(&s->state[2], &s->state[6], &s->state[10], &s->state[14]);
    chacha20_quarterround(&s->state[3], &s->state[7], &s->state[11], &s->state[15]);
    /* Diagonal rounds */
    chacha20_quarterround(&s->state[0], &s->state[5], &s->state[10], &s->state[15]);
    chacha20_quarterround(&s->state[1], &s->state[6], &s->state[11], &s->state[12]);
    chacha20_quarterround(&s->state[2], &s->state[7],  &s->state[8],  &s->state[13]);
    chacha20_quarterround(&s->state[3], &s->state[4],  &s->state[9],  &s->state[14]);
}

/* 接收一个初始状态，执行 20 轮并生成 64 字节密钥流 */
void chacha20_block(const void *init_void, uint8_t out[64])
{
    chacha20_state s;
    memcpy(&s, init_void, sizeof(s));
    for (int i = 0; i < 10; i++) {
        chacha20_double_round(&s);
    }
    /* 加上初始状态 */
    const chacha20_state *init = (const chacha20_state *)init_void;
    for (int i = 0; i < 16; i++) {
        s.state[i] += init->state[i];
    }
    /* 序列化为 64 字节小端 */
    for (int i = 0; i < 16; i++) {
        uint32_t x = s.state[i];
        out[4*i+0] = (uint8_t)(x);
        out[4*i+1] = (uint8_t)(x >> 8);
        out[4*i+2] = (uint8_t)(x >> 16);
        out[4*i+3] = (uint8_t)(x >> 24);
    }
}

void chacha20_init(void *ctx_void, const uint8_t key[32], const uint8_t nonce[12], uint32_t counter)
{
    chacha20_state *ctx = (chacha20_state *)ctx_void;
    memcpy(&ctx->state[0], CHACHA20_SIGMA, 4 * sizeof(uint32_t));
    memcpy(&ctx->state[4], key, 32);
    ctx->state[12] = counter;
    memcpy(&ctx->state[13], nonce, 12);
}

size_t chacha20_keystream(void *ctx_void, uint8_t *out, size_t outlen)
{
    chacha20_state init;
    memcpy(&init, (chacha20_state *)ctx_void, sizeof(init));
    chacha20_block(&init, out);
    /* 更新内部 counter (state[12]++) */
    ((chacha20_state *)ctx_void)->state[12]++;
    return (outlen <= 64) ? outlen : 64;
}

void chacha20_crypt_oneshot(const uint8_t key32[32],
                            const uint8_t nonce[12],
                            uint32_t counter,
                            const uint8_t *msg,
                            size_t msglen,
                            uint8_t *out)
{
    chacha20_state init;
    memcpy(&init.state[0], CHACHA20_SIGMA, 4 * sizeof(uint32_t));
    memcpy(&init.state[4], key32, 32);
    init.state[12] = counter;
    memcpy(&init.state[13], nonce, 12);

    size_t offset = 0;
    uint8_t block[64];
    while (offset < msglen) {
        /* 生成一个 64 字节的 keystream 块 */
        chacha20_state s;
        memcpy(&s, &init, sizeof(s));
        for (int i = 0; i < 10; i++) {
            chacha20_double_round(&s);
        }
        for (int i = 0; i < 16; i++) {
            s.state[i] += init.state[i];
        }
        /* 序列化为 64 字字节小端 */
        for (int i = 0; i < 16; i++) {
            uint32_t x = s.state[i];
            block[4*i+0] = (uint8_t)(x);
            block[4*i+1] = (uint8_t)(x >> 8);
            block[4*i+2] = (uint8_t)(x >> 16);
            block[4*i+3] = (uint8_t)(x >> 24);
        }

        /* XOR keystream 块到输出 */
        size_t step = (msglen - offset) > 64 ? 64 : (msglen - offset);
        for (size_t i = 0; i < step; i++) {
            out[offset + i] = msg[offset + i] ^ block[i];
        }
        offset += step;
        /* 更新 counter，准备下一块 */
        init.state[12]++;
    }
}
