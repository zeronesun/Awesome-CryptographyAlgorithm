# -*- coding: utf-8 -*-
"""
simple/_base.py —— 教学版公共小工具
"""

def bytes_to_hex(data: bytes) -> str:
    """把字节流转成小写十六进制字符串,便于打印/测试比对"""
    return data.hex()


def hex_to_bytes(hexstr: str) -> bytes:
    """十六进制字符串 -> bytes"""
    hexstr = hexstr.strip().replace(" ", "").replace("\n", "")
    return bytes.fromhex(hexstr)
