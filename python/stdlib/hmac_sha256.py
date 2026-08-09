# -*- coding: utf-8 -*-
"""stdlib/hmac_sha256.py —— 基于标准库 hmac 的封装"""


def hmac_sha256(key: bytes, msg: bytes) -> bytes:
    import hmac
    import hashlib
    return hmac.new(key, msg, hashlib.sha256).digest()


def hmac_sha256_hex(key: bytes, msg: bytes) -> str:
    return hmac_sha256(key, msg).hex()
