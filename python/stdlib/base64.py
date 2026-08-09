# -*- coding: utf-8 -*-
"""stdlib/base64.py —— 基于标准库 base64 的封装"""

import base64


def base64_encode(data: bytes) -> str:
    return base64.b64encode(data).decode("ascii")


def base64_decode(code: str) -> bytes:
    return base64.b64decode(code)
