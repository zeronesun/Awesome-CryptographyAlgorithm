# -*- coding: utf-8 -*-

"""
凯撒密码（纯 Python 教学版）
字母偏移：f(a) = (a + key) % 26（处理大小写），非字母保留为空格
"""

import sys

def caesar_encode_single(src_char: str, key: int) -> str:
    """加密单个字符"""
    if 'a' <= src_char <= 'z':
        return chr((ord(src_char) - ord('a') + key) % 26 + ord('a'))
    elif 'A' <= src_char <= 'Z':
        return chr((ord(src_char) - ord('A') + key) % 26 + ord('A'))
    else:
        return ' '

def caesar_decode_single(src_char: str, key: int) -> str:
    """解密单个字符"""
    if 'a' <= src_char <= 'z':
        offset = ord(src_char) - key
        return chr(offset if offset >= ord('a') else offset + 26)
    elif 'A' <= src_char <= 'Z':
        offset = ord(src_char) - key
        return chr(offset if offset >= ord('A') else offset + 26)
    else:
        return ' '

def caesar_encode(plain: str, key: int) -> str:
    """加密整个字符串"""
    result = []
    for ch in plain:
        result.append(caesar_encode_single(ch, key))
    return ''.join(result)

def caesar_decode(cipher: str, key: int) -> str:
    """解密整个字符串"""
    result = []
    for ch in cipher:
        result.append(caesar_decode_single(ch, key))
    return ''.join(result)

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python caesar.py <text> <key>")
        sys.exit(1)
    text = sys.argv[1]
    key = int(sys.argv[2])
    enc = caesar_encode(text, key)
    dec = caesar_decode(enc, key)
    print(f"Ciphertext: {enc}")
    print(f"Decoded: {dec}")
