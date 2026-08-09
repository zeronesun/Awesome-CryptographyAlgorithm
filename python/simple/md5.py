# -*- coding: utf-8 -*-
"""MD5 (RFC 1321) 纯 Python 实现（教学版，32 位整数运算）"""

import struct

_MASK = 0xFFFFFFFF


def _left_rotate(n, s):
    return ((n << s) | (n >> (32 - s))) & _MASK


def _pad(msg_len):
    # 以 64 位小端长度收尾的 PKCS#5 风格填充
    bit_len = (msg_len * 8) & 0xFFFFFFFFFFFFFFFF
    pad = b"\x80"
    pad += b"\x00" * ((56 - (msg_len + 1) % 64) % 64)
    pad += struct.pack("<Q", bit_len)
    return pad


def md5(data: bytes) -> bytes:
    a0, b0, c0, d0 = 0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476

    K = [0] * 64
    for i in range(64):
        K[i] = int(abs(__import__("math").sin(i + 1)) * (2 ** 32)) & _MASK

    s = [7, 12, 17, 22] * 4 + [5, 9, 14, 20] * 4 + [4, 11, 16, 23] * 4 + [6, 10, 15, 21] * 4

    msg = data + _pad(len(data))
    for chunk_start in range(0, len(msg), 64):
        chunk = msg[chunk_start:chunk_start + 64]
        M = list(struct.unpack("<16I", chunk))

        A, B, C, D = a0, b0, c0, d0
        for i in range(64):
            if i < 16:
                F = (B & C) | (~B & D)
                g = i
            elif i < 32:
                F = (D & B) | (~D & C)
                g = (5 * i + 1) % 16
            elif i < 48:
                F = B ^ C ^ D
                g = (3 * i + 5) % 16
            else:
                F = C ^ (B | ~D)
                g = (7 * i) % 16
            F = (F + A + K[i] + M[g]) & _MASK
            A, D, C, B = D, C, B, (B + _left_rotate(F, s[i])) & _MASK

        a0 = (a0 + A) & _MASK
        b0 = (b0 + B) & _MASK
        c0 = (c0 + C) & _MASK
        d0 = (d0 + D) & _MASK

    return struct.pack("<4I", a0, b0, c0, d0)


def md5_hex(data: bytes) -> str:
    return md5(data).hex()


if __name__ == "__main__":
    import sys
    text = sys.argv[1].encode("utf-8") if len(sys.argv) > 1 else b"hello"
    print(md5_hex(text))
