package com.zerone.crypto.simple;

/**
 * 维吉尼亚密码 —— 教学实现
 * 原理:基于凯撒密码的多表替换,密钥字母决定每一位所用偏移量。
 */
public final class Vigenere {

    public static String encrypt(String plaintext, String key) {
        StringBuilder sb = new StringBuilder();
        int ki = 0;
        for (int i = 0; i < plaintext.length(); i++) {
            char c = plaintext.charAt(i);
            if (Character.isLetter(c)) {
                int k = keyShift(key.charAt(ki % key.length()));
                sb.append(shift(c, k, true));
                ki++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static String decrypt(String ciphertext, String key) {
        StringBuilder sb = new StringBuilder();
        int ki = 0;
        for (int i = 0; i < ciphertext.length(); i++) {
            char c = ciphertext.charAt(i);
            if (Character.isLetter(c)) {
                int k = keyShift(key.charAt(ki % key.length()));
                sb.append(shift(c, k, false));
                ki++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    private static int keyShift(char k) {
        if (k >= 'a' && k <= 'z') return k - 'a';
        if (k >= 'A' && k <= 'Z') return k - 'A';
        return 0;
    }

    private static char shift(char c, int k, boolean encode) {
        if (c >= 'a' && c <= 'z') {
            int d = c - 'a';
            int nd = encode ? (d + k) % 26 : (d - k + 26) % 26;
            return (char) ('a' + nd);
        }
        if (c >= 'A' && c <= 'Z') {
            int d = c - 'A';
            int nd = encode ? (d + k) % 26 : (d - k + 26) % 26;
            return (char) ('A' + nd);
        }
        return c;
    }

    private Vigenere() {}
}
