# -*- coding: utf-8 -*-
"""simple/base64.py —— 基础 Base64 编码/解码（教学实现，无第三方依赖）"""

import sys


# Base64 编码表
_BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"


def base64_encode(data: bytes) -> str:
    """将字节串编码为 Base64 字符串(不含换行)"""
    result = []
    for i in range(0, len(data), 3):
        chunk = data[i:i + 3]
        n = int.from_bytes(chunk, "big")
        pad = 3 - len(chunk)
        n <<= 8 * pad
        result.append(_BASE64_CHARS[(n >> 18) & 63])
        result.append(_BASE64_CHARS[(n >> 12) & 63])
        result.append('=' if pad > 1 else _BASE64_CHARS[(n >> 6) & 63])
        result.append('=' if pad > 0 else _BASE64_CHARS[n & 63])
    return ''.join(result)


_BASE64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
