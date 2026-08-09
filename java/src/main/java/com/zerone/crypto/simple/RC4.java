package com.zerone.crypto.simple;

/**
 * RC4 流密码 —— 教学实现
 * 原理:KSA 用密钥打乱 S 盒,PRGA 生成密钥流与明文 XOR。
 */
public final class RC4 {

    private int[] s = new int[256];

    public byte[] crypt(byte[] data, byte[] key) {
        ksa(key);
        byte[] out = new byte[data.length];
        int i = 0, j = 0;
        for (int k = 0; k < data.length; k++) {
            i = (i + 1) & 0xff;
            j = (j + s[i]) & 0xff;
            int tmp = s[i]; s[i] = s[j]; s[j] = tmp;
            int t = (s[i] + s[j]) & 0xff;
            out[k] = (byte) (data[k] ^ s[t]);
        }
        return out;
    }

    public byte[] encrypt(byte[] plaintext, byte[] key) {
        return crypt(plaintext, key);
    }

    public byte[] decrypt(byte[] ciphertext, byte[] key) {
        return crypt(ciphertext, key);
    }

    private void ksa(byte[] key) {
        for (int i = 0; i < 256; i++) s[i] = i;
        int j = 0;
        for (int i = 0; i < 256; i++) {
            j = (j + s[i] + (key[i % key.length] & 0xff)) & 0xff;
            int tmp = s[i]; s[i] = s[j]; s[j] = tmp;
        }
    }

    private RC4() {}
}
