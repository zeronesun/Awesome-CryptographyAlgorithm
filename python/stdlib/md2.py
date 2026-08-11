# -*- coding: utf-8 -*-

"""
stdlib/md2.py —— MD2 (RFC 1319) 标准库对照版

说明
----
- 标准库 `hashlib` 在默认 security provider 下**不支持** MD2（OpenSSL 3.0 默认禁用）。
- 因此本文件优先 try 标准库；若不可用则退回可复用的 `simple/md2.py` 纯手写实现。
- 这是**教学对照**：展示「有标准库 vs 无标准库」两种路径，不是加密推荐项。
  MD2 已近 30 年前被证明不安全（collision 已公开），现代系统不应使用 MD2 做安全用途。
"""

import hashlib


def _try_stdlib_md2(data: bytes) -> bytes | None:
    """尝试用 hashlib 获取 MD2 摘要（多数平台 Default  Provider 不可用）。"""
    try:
        return hashlib.new("MD2", data).digest()
    except ValueError:
        # digest: OpenSSL 未提供 MD2
        return None


def md2(data: bytes, prefer_stdlib: bool = True) -> bytes:
    """返回 data 的 MD2 摘要 (16 bytes)。"""
    digest = _try_stdlib_md2(data)
    if digest is not None:
        return digest
    # 没有标准库 MD2 → 回退到手写教学版
    try:
        from simple.md2 import md2 as _pure_md2
        return _pure_md2(data)
    except ImportError:
        raise NotImplementedError(
            "MD2: hashlib 默认 provider 不支持,且 simple/md2.py 不可用;"
            "请安装支持 MD2 的 OpenSSL legacy provider 或确保 simple/md2.py 存在。"
        )


def md2_hex(data: bytes) -> str:
    """返回 MD2 十六进制摘要"""
    return md2(data).hex()


if __name__ == "__main__":
    import sys
    msg = sys.argv[1].encode("utf-8") if len(sys.argv) > 1 else b"abc"
    print("MD2 hex:", md2_hex(msg))
