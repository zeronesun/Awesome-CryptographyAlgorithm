# -*- coding: utf-8 -*-

"""
HMAC-SHA256 (RFC 2104) —— 纯 Python 手写实现（教学版）。

基于本包内的 simple.sha256 实现:
    inner = SHA256( (key ^ ipad) || message )
    outer = SHA256( (key ^ opad) || inner )
其中 SHA-256 分组长度 B = 64,输出长度 L = 32。
"""

import os
import sys

# 让本模块既可被 simple 包导入,也可作为独立脚本运行。
sys.path.insert(0, os.path.dirname(os.path.abspath(__file__)))
try:
    from . import sha256 as _sha256
except ImportError:
    import sha256 as _sha256

_BLOCK = 64
_OUT = 32


def _normalize_key(key: bytes) -> bytes:
    if len(key) > _BLOCK:
        key = _sha256.sha256(key)
    return key + b"\x00" * (_BLOCK - len(key))


def hmac_sha256(key, msg) -> bytes:
    """计算 HMAC-SHA256 摘要,返回 32 字节(bytes)。key/msg 可为 str 或 bytes。"""
    if isinstance(key, str):
        key = key.encode("utf-8")
    if isinstance(msg, str):
        msg = msg.encode("utf-8")

    k = _normalize_key(key)
    ipad = bytes(b ^ 0x36 for b in k)
    opad = bytes(b ^ 0x5c for b in k)

    inner = _sha256.sha256(ipad + msg)
    return _sha256.sha256(opad + inner)


def hmac_sha256_hex(key, msg) -> str:
    """返回十六进制摘要字符串。"""
    return hmac_sha256(key, msg).hex()


if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python hmac_sha256.py <key> <message>")
        sys.exit(1)
    print(hmac_sha256_hex(sys.argv[1], sys.argv[2]))
