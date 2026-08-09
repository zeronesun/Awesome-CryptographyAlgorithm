# -*- coding: utf-8 -*-
"""stdlib/sha1.py —— 基于标准库 hashlib 的封装"""


def sha1_hex(data: bytes) -> str:
    import hashlib
    return hashlib.sha1(data).hexdigest()


def sha1(data: bytes) -> bytes:
    import hashlib
    return hashlib.sha1(data).digest()
