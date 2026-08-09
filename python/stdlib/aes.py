# -*- coding: utf-8 -*-
"""stdlib/aes.py —— 优先使用标准库 cryptography，回退到 simple 实现"""


def aes_encrypt_ecb(plaintext: bytes, key: bytes) -> bytes:
    try:
        from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
        from cryptography.hazmat.backends import default_backend
        cipher = Cipher(algorithms.AES(key), modes.ECB(), backend=default_backend())
        encryptor = cipher.encryptor()
        return encryptor.update(plaintext) + encryptor.finalize()
    except ImportError:
        from simple.aes import aes_encrypt_ecb as _f
        return _f(plaintext, key)


def aes_decrypt_ecb(ciphertext: bytes, key: bytes) -> bytes:
    try:
        from cryptography.hazmat.primitives.ciphers import Cipher, algorithms, modes
        from cryptography.hazmat.backends import default_backend
        cipher = Cipher(algorithms.AES(key), modes.ECB(), backend=default_backend())
        decryptor = cipher.decryptor()
        return decryptor.update(ciphertext) + decryptor.finalize()
    except ImportError:
        from simple.aes import aes_decrypt_ecb as _f
        return _f(ciphertext, key)
