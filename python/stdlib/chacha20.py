# -*- coding: utf-8 -*-

"""
stdlib/chacha20.py — 标准库 ChaCha20 对照版
依赖: cryptography 包（多数环境需手动安装），否则 fallback 到 simple 模块
"""

def chacha20_crypt(key: bytes, nonce: bytes, counter: int, plaintext: bytes) -> bytes:
    """ChaCha20 加密/解密（RFC 7539 IETF 变体）

    Args:
        key (32B): 256-bit 密钥
        nonce (12B): 96-bit nonce
        counter (int): 32-bit 起始计数器
        plaintext: 明文或密文（异或操作）

    Returns:
        cipher bytes

    Notes:
        优先使用 cryptography 包的 RFC 7539 实现；其不可用或不兼容时
        自动回退到 simple 模块的手写实现。
    """
    try:
        from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
        # cryptography >= 43 原生支持 IETF 变体（12 字节 nonce + 32 位计数器）
        try:
            algo = algorithms.ChaCha20(key, nonce, counter=counter)
        except TypeError:
            # 旧版 cryptography 不支持 counter 参数，回退到手写实现
            from simple.chacha20 import chacha20_crypt as _pure
            return _pure(key, nonce, counter, plaintext)
        cipher = Cipher(algo, mode=None)
        encryptor = cipher.encryptor()
        return encryptor.update(plaintext) + encryptor.finalize()
    except (ImportError, ValueError):
        # cryptography 未安装或不支持该 nonce 长度时，回退到手写实现
        from simple.chacha20 import chacha20_crypt as _pure
        return _pure(key, nonce, counter, plaintext)

if __name__ == "__main__":
    import sys, binascii
    KEY = bytes.fromhex("000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F")
    NONCE = bytes.fromhex("0000000900004A0000000031")
    COUNTER = 1
    PLAIN = b"Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it."
    EXPECT = bytes.fromhex("6E2E359A2568F98041BA0728DD0D6981E97E7AEC1D4360C20A27AFCCFD9FAE0BF91B65C5524733AB8F593DABCD62B3571639D624E65152AB8F530C359F0861D807CA0DBF500D6A6156A38E088A22B65E52BC514D16CCF806818CE91AB77937365AF90BBF74A35BE6B40B8EEDF2785E42874D")
    cipher = chacha20_crypt(KEY, NONCE, COUNTER, PLAIN)
    print("ciphertext(hex):", binascii.hexlify(cipher).decode())
    print("match RFC 7539 §2.4.2:", "OK" if cipher == EXPECT else "FAIL")
