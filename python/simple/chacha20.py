# -*- coding: utf-8 -*-

"""ChaCha20 (RFC 7539) 纯 Python 实现.

仅供教学参考, 展示 ChaCha20 密钥流生成原理.
"""

import struct

# "expand 32-byte k"
SIGMA = [0x61707865, 0x3320646e, 0x79622d32, 0x6b206574]


def _rotl32(v: int, n: int) -> int:
    return ((v << n) | (v >> (32 - n))) & 0xFFFFFFFF


def _quarter(state, a, b, c, d):
    """RFC 7539 §2.1 Quarter Round — 原地混合四个 32 位字."""
    state[a] = (state[a] + state[b]) & 0xFFFFFFFF
    state[d] = _rotl32(state[d] ^ state[a], 16)
    state[c] = (state[c] + state[d]) & 0xFFFFFFFF
    state[b] = _rotl32(state[b] ^ state[c], 12)
    state[a] = (state[a] + state[b]) & 0xFFFFFFFF
    state[d] = _rotl32(state[d] ^ state[a], 8)
    state[c] = (state[c] + state[d]) & 0xFFFFFFFF
    state[b] = _rotl32(state[b] ^ state[c], 7)


def chacha_init(key: bytes, nonce: bytes, counter: int) -> list[int]:
    """构造 16 个 32-bit 字的初始状态 (RFC 7539 §2.3):
       sigma(4) + key(8) + counter(1) + nonce(3)."""
    c = list(SIGMA)
    k = list(struct.unpack('<8I', key))
    n = list(struct.unpack('<3I', nonce))
    return c + k + [counter & 0xFFFFFFFF, n[0], n[1], n[2]]


def chacha20_block_inner(key: bytes, nonce: bytes, counter: int) -> bytes:
    """生成 64 字节密钥流块: 20 轮(10 个 double round) 后加上初始状态."""
    state = chacha_init(key, nonce, counter)
    x = list(state)

    for _ in range(10):
        # Column rounds
        _quarter(x, 0, 4, 8, 12)
        _quarter(x, 1, 5, 9, 13)
        _quarter(x, 2, 6, 10, 14)
        _quarter(x, 3, 7, 11, 15)
        # Diagonal rounds
        _quarter(x, 0, 5, 10, 15)
        _quarter(x, 1, 6, 11, 12)
        _quarter(x, 2, 7, 8, 13)
        _quarter(x, 3, 4, 9, 14)

    out = [(x[i] + state[i]) & 0xFFFFFFFF for i in range(16)]
    return struct.pack('<16I', *out)


def chacha20_crypt(key: bytes, nonce: bytes, counter: int, plaintext: bytes) -> bytes:
    """ChaCha20 加密/解密(同一种异或操作). 按 64 字节分块处理任意长度消息."""
    if len(key) != 32:
        raise ValueError("ChaCha20 key must be 32 bytes")
    if len(nonce) != 12:
        raise ValueError("ChaCha20 nonce must be 12 bytes")
    out = bytearray(len(plaintext))
    offset = 0
    ctr = counter & 0xFFFFFFFF
    while offset < len(plaintext):
        keystream = chacha20_block_inner(key, nonce, ctr)
        step = min(64, len(plaintext) - offset)
        for i in range(step):
            out[offset + i] = plaintext[offset + i] ^ keystream[i]
        offset += step
        ctr = (ctr + 1) & 0xFFFFFFFF
    return bytes(out)


if __name__ == "__main__":
    # RFC 7539 测试向量
    KEY = bytes.fromhex("000102030405060708090a0b0c0d0e0f"
                        "101112131415161718191a1b1c1d1e1f")
    NONCE_A1 = bytes.fromhex("0000000900004a0000000031")
    NONCE_A2 = bytes.fromhex("000000000000004a00000000")
    PLAIN = (b"Ladies and Gentlemen of the class of '99: If I could offer you "
             b"only one tip for the future, sunscreen would be it.")

    # RFC 7539 §2.4.2 "Sunscreen" 示例: key=00..1f, nonce=..09..31, ctr=1, 全长 114 字节密文
    EXPECT_SUNSCREEN = bytes.fromhex(
        "6e2e359a2568f98041ba0728dd0d6981e97e7aec1d4360c20a27afccfd9fae0b"
        "f91b65c5524733ab8f593dabcd62b3571639d624e65152ab8f530c359f0861d8"
        "07ca0dbf500d6a6156a38e088a22b65e52bc514d16ccf806818ce91ab7793736"
        "5af90bbf74a35be6b40b8eedf2785e42874d")
    ct = chacha20_crypt(KEY, NONCE_A1, 1, PLAIN)
    print("RFC7539 2.4.2 Sunscreen:", "PASS" if ct == EXPECT_SUNSCREEN else "FAIL")

    # RFC 7539 Appendix A.1 Test Vector #1 (全 0 密钥/nonce, ctr=0) —— 64 字节密钥流
    EXPECT_A1 = bytes.fromhex(
        "76b8e0ada0f13d90405d6ae55386bd28bdd219b8a08ded1aa836efcc8b770dc7"
        "da41597c5157488d7724e03fb8d84a376a43b8f41518a11cc387b669b2ee6586")
    ks = chacha20_block_inner(bytes(32), bytes(12), 0)
    print("RFC7539 A.1 keystream:", "PASS" if ks == EXPECT_A1 else "FAIL")

    # 往返自反验证
    dec = chacha20_crypt(KEY, NONCE_A1, 1, ct)
    print("Roundtrip:", "PASS" if dec == PLAIN else "FAIL")
