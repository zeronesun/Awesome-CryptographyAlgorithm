# -*- coding: utf-8 -*-
"""stdlib/sha256.py —— 基于标准库 hashlib 的封装"""


def sha256_hex(data: bytes) -> str:
    import hashlib
    return hashlib.sha256(data).hexdigest()


def sha256(data: bytes) -> bytes:
    import hashlib
    return hashlib.sha256(data).digest()
