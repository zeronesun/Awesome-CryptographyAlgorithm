# RC4 Stream Cipher - 纯 Python 实现
# RC4流密码的纯Python实现（PRGA/KSA）


class RC4:
    """RC4 流密码加密算法，加密与解密均为 XOR 操作。"""

    def __init__(self):
        self.S = list(range(256))

    def init_key(self, key: bytes):
        """KSA：用密钥置换 S 盒。"""
        keylen = len(key)
        j = 0
        for i in range(256):
            j = (j + self.S[i] + key[i % keylen]) % 256
            self.S[i], self.S[j] = self.S[j], self.S[i]

    def crypt(self, data: bytes) -> bytes:
        """对 data 进行 RC4 加/解密（XOR 密钥流）。"""
        S = self.S
        out = bytearray(len(data))
        i = j = 0
        for k, b in enumerate(data):
            i = (i + 1) % 256
            j = (j + S[i]) % 256
            S[i], S[j] = S[j], S[i]
            t = (S[i] + S[j]) % 256
            out[k] = b ^ S[t]
        return bytes(out)

    def rc4_encrypt(self, plaintext: bytes, key: bytes) -> bytes:
        self.init_key(key)
        return self.crypt(plaintext)

    def rc4_decrypt(self, ciphertext: bytes, key: bytes) -> bytes:
        return self.rc4_encrypt(ciphertext, key)


def test_rc4():
    """已知答案测试（NIST / 标准测试向量）。"""
    cases = [
        (b"Key", b"Plaintext", bytes.fromhex("BBF316E8D940AF0AD3")),
        (bytes.fromhex("0123456789ABCDEF"), bytes.fromhex("0123456789ABCDEF"),
         bytes.fromhex("750F7B1A5241317D77C6F7")),
    ]
    ok = True
    for key, pt, expected in cases:
        rc4 = RC4()
        ct = rc4.rc4_encrypt(pt, key)
        dec = rc4.rc4_decrypt(ct, key)
        passed = ct == expected and dec == pt
        ok = ok and passed
        print(f"key={key.hex()} pt={pt.hex()} ct={ct.hex()} "
              f"expected={expected.hex()} -> {'PASS' if passed else 'FAIL'}")
    return ok


if __name__ == "__main__":
    print("RC4 tests:", "PASS" if test_rc4() else "FAIL")
