# -*- coding: utf-8 -*-

"""
stdlib/chacha20.py — 标准库 ChaCha20 对照版
依赖: cryptography 包（多数环境需手动安装），否则 fallback 到 simple 模块
"""

import sys

def chacha20_crypt(key: bytes, nonce: bytes, counter: int, plaintext: bytes) -> bytes:
    """ChaCha20 加密/解密

    Args:
        key (32B): 256-bit 密钥
        nonce (12B): 96-bit nonce
        counter (int): 32-bit 起始计数器
        plaintext: 明文或密文（异或操作）

    Returns:
        cipher bytes

    Raises:
        ImportError (若 cryptography 不可用)
    """
    # 尝试使用 cryptography
    from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
    from cryptography.hazmat.backends import default_backend
    try:
        cipher = Cipher(algorithms.ChaCha20(key, nonce), mode=None, backend=default_backend())
        encryptor = cipher.encryptor()
        return encryptor.update(plaintext) + encryptor.finalize()
    except Exception as e:
        # fallback 到 simple 实现
        print(f"Warning: cryptography 失败 ({e}), 回退 to simple.", file=sys.stderr)
        from simple.chacha20 import chacha20_crypt as _pure
        return _pure(key, nonce, counter, plaintext)

if __name__ == "__main__":
    import sys, binascii
    KEY = bytes.fromhex("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F")
    NONCE = bytes.fromhex("0000000900004A0000000031")
    COUNTER = 1
    PLAIN = b"Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it."
    EXPECT = bytes.fromhex("6E2E359A2568F98041BA0728DD0D6981E97E7AEC1D4360C20A27AFFCD9FAE0BF91B65C5524733AB8F593DAB62CD2BB0992704736F61E9C05D0B6BC3E36F29856F1342115E901F9EA852A430304AA46B564FB4F037468B5E5F3604342529252291873C57F3EE8D08B36E4E45B5C408")
    cipher = chacha20_crypt(KEY, NONCE, COUNTER, PLAIN)
    print("ciphertext(hex):", binascii.hexlify(cipher).decode())
    print("match RFC A.1:", "OK" if cipher == EXPECT else "FAIL")
