# -*- coding: utf-8 -*-
"""SHA-1 (FIPS 180-4) 纯 Python 实现（教学版，32 位大端整数运算）"""

import struct

_MASK = 0xFFFFFFFF


def _pad(msg_len):
    bit_len = (msg_len * 8) & 0xFFFFFFFFFFFFFFFF
    pad = b"\x80" + b"\x00" * ((56 - (msg_len + 1) % 64) % 64)
    pad += struct.pack(">Q", bit_len)
    return pad


def _rotl(n, s):
    return ((n << s) | (n >> (32 - s))) & _MASK


def sha1(data: bytes) -> bytes:
    h = [0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0]

    msg = data + _pad(len(data))
    for chunk in range(0, len(msg), 64):
        w = list(struct.unpack(">16I", msg[chunk:chunk + 64]))
        for i in range(16, 80):
            w.append(_rotl(w[i - 3] ^ w[i - 8] ^ w[i - 14] ^ w[i - 16], 1))

        a, b, c, d, e = h
        for i in range(80):
            if i < 20:
                f = (b & c) | (~b & d)
                k = 0x5A827999
            elif i < 40:
                f = b ^ c ^ d
                k = 0x6ED9EBA1
            elif i < 60:
                f = (b & c) | (b & d) | (c & d)
                k = 0x8F1BBCDC
            else:
                f = b ^ c ^ d
                k = 0xCA62C1D6
            tmp = (_rotl(a, 5) + f + e + k + w[i]) & _MASK
            e, d, c, b, a = d, c, _rotl(b, 30), a, tmp

        h = [(x + y) & _MASK for x, y in zip(h, (a, b, c, d, e))]

    return b"".join(struct.pack(">I", x) for x in h)


def sha1_hex(data: bytes) -> str:
    return sha1(data).hex()


if __name__ == "__main__":
    import sys
    text = sys.argv[1].encode("utf-8") if len(sys.argv) > 1 else b"hello"
    print(sha1_hex(text))
