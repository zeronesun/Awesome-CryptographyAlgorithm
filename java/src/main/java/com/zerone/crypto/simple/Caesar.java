package com.zerone.crypto.simple;

/**
 * 凯撒密码 —— 教学实现
 * 原理:对明文字母按偏移量 key 做位移(模 26)。
 */
public class Caesar {

    /** 加密:每个英文字母向后移 key 位,非字母原样保留 */
    public static String encrypt(String plaintext, int key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);
            sb.append(shift(c, key % 26, true));
        }
        return sb.toString();
    }

    /** 解密:每个英文字母向前移 key 位 */
    public static String decrypt(String ciphertext, int key) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);
            sb.append(shift(c, key % 26, false));
        }
        return sb.toString();
    }

    private static char shift(char c, int k, boolean encode) {
        if (k < 0) k += 26;
        if (c >= 'a' && c <= 'z') {
            int base = 'a';
            int d = c - base;
            int nd = encode ? (d + k) % 26 : (d - k + 26) % 26;
            return (char) (base + nd);
        } else if (c >= 'A' && c <= 'Z') {
            int base = 'A';
            int d = c - base;
            int nd = encode ? (d + k) % 26 : (d - k + 26) % 26;
            return (char) (base + nd);
        }
        return c;
    }

    private Caesar() {}
}
