# -*- coding: utf-8 -*-
"""
Python 已知答案(KAT)测试。

同时兼容两种运行方式:
  - 脚本:  python python/tests/run_tests.py
  - pytest: cd python && python -m pytest tests/ -v
"""

import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from simple import md5 as s_md5
from simple import sha1 as s_sha1
from simple import sha256 as s_sha256
from simple import caesar as s_caesar
from simple import vigenere as s_vigenere
from simple import rc4 as s_rc4
from simple import des as s_des
from simple import aes as s_aes
from simple import base64 as s_base64
from simple import hmac_sha256 as s_hmac
from simple import md2 as s_md2
from simple import chacha20 as s_chacha

import hashlib
import base64 as std_base64
import hmac as std_hmac


def check(name, got, want, fails):
    ok = got == want
    if not ok:
        fails.append(name)
    print(f"[{'PASS' if ok else 'FAIL'}] {name}")


def run_all():
    fails = []

    # 哈希: 与标准库对照
    data = b"hello"
    check("md5(hello)", s_md5.md5_hex(data), hashlib.md5(data).hexdigest(), fails)
    check("sha1(hello)", s_sha1.sha1_hex(data), hashlib.sha1(data).hexdigest(), fails)
    check("sha256(hello)", s_sha256.sha256_hex(data),
          hashlib.sha256(data).hexdigest(), fails)
    long = b"A" * 1000
    check("sha256(long)", s_sha256.sha256_hex(long),
          hashlib.sha256(long).hexdigest(), fails)

    # MD2 (RFC 1319 known-answer test; Python hashlib 不支持 MD2)
    check("md2(empty)", s_md2.md2_hex(b""), "8350e5a3e24c153df2275c9f80692773", fails)
    check("md2(abc)", s_md2.md2_hex(b"abc"), "da853b0d3f88d99b30283a69e6ded6bb", fails)

    # 古典密码: 往返一致性
    enc = s_caesar.caesar_encode("Hello, World!", 7)
    check("caesar roundtrip", s_caesar.caesar_decode(enc, 7), "Hello, World!", fails)

    vc = s_vigenere.vigenere_encode("KEY", "attack at dawn")
    check("vigenere roundtrip",
          s_vigenere.vigenere_decode("KEY", vc), "attack at dawn", fails)

    rc4 = s_rc4.RC4()
    ct = rc4.rc4_encrypt(b"Plaintext", b"Key")
    check("rc4 NIST", ct.hex(), "bbf316e8d940af0ad3", fails)

    # 分组密码: 标准测试向量 / ECB 往返
    dkey = bytes.fromhex("133457799BBCDFF1")
    dpt = bytes.fromhex("0123456789ABCDEF")
    check("des encrypt", s_des.des_encrypt(dpt, dkey).hex(), "85e813540f0ab405", fails)
    check("des ecb roundtrip",
          s_des.des_decrypt_ecb(s_des.des_encrypt_ecb(b"hello!!", dkey), dkey),
          b"hello!!", fails)

    akey = bytes.fromhex("000102030405060708090a0b0c0d0e0f")
    apt = bytes.fromhex("00112233445566778899aabbccddeeff")
    check("aes-128 block", s_aes._aes_block(apt, akey).hex(),
          "69c4e0d86a7b0430d8cdb78070b4c55a", fails)

    # Base64: 往返 + 与标准库一致
    raw = b"Hello, Base64!"
    check("base64 roundtrip",
          s_base64.base64_decode(s_base64.base64_encode(raw)).encode("latin1"),
          raw, fails)
    check("base64 vs stdlib",
          s_base64.base64_encode(raw), std_base64.b64encode(raw).decode(), fails)

    # HMAC-SHA256
    key, msg = b"secret", b"message"
    want = std_hmac.new(key, msg, hashlib.sha256).hexdigest()
    from stdlib import hmac_sha256 as h
    check("hmac_sha256 (stdlib)", h.hmac_sha256_hex(key, msg), want, fails)
    check("hmac_sha256 (simple)", s_hmac.hmac_sha256_hex(key, msg), want, fails)

    # ChaCha20 (RFC 7539 A.1) — 流密码已知答案测试
    cc_key = bytes.fromhex("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f")
    cc_nonce = bytes.fromhex("0000000900004a0000000031")
    cc_plain = b"Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it."
    cc_expect = ("6e2e359a2568f98041ba0728dd0d6981e97e7aec1d4360c20a27afccfd9fae0b"
                 "f91b65c5524733ab8f593dabcd62b3571639d624e65152ab8f530c359f0861d8"
                 "07ca0dbf500d6a6156a38e088a22b65e52bc514d16ccf806818ce91ab7793736"
                 "5af90bbf74a35be6b40b8eedf2785e42874d")
    check("chacha20 RFC7539 A.1 (simple)",
          s_chacha.chacha20_crypt(cc_key, cc_nonce, 1, cc_plain).hex(), cc_expect, fails)
    # 往返自反: 加密后再用相同参数解密应还原明文
    cc_dec = s_chacha.chacha20_crypt(cc_key, cc_nonce, 1,
                                     s_chacha.chacha20_crypt(cc_key, cc_nonce, 1, cc_plain))
    check("chacha20 roundtrip", cc_dec, cc_plain, fails)

    return fails


def test_kat():
    """pytest 入口。"""
    fails = run_all()
    assert not fails, f"{len(fails)} test(s) failed: {fails}"


if __name__ == "__main__":
    fails = run_all()
    print()
    if fails:
        print(f"{len(fails)} test(s) failed: {fails}")
        sys.exit(1)
    print("All Python tests passed.")
