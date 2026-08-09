# -*- coding: utf-8 -*-
"""stdlib/md5.py —— 基于标准库 hashlib 的封装"""


def md5_hex(data: bytes) -> str:
    import hashlib
    return hashlib.md5(data).hexdigest()


def md5(data: bytes) -> bytes:
    import hashlib
    return hashlib.md5(data).digest()
