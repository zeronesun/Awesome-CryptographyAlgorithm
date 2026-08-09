package com.zerone.crypto.simple;

import java.nio.charset.StandardCharsets;

/**
 * 演示入口:快速展示各算法的加解密/摘要能力。
 */
public class Main {
    public static void main(String[] args) {
        String text = "Hello, Crypto!";

        System.out.println("Caesar(7): " + Caesar.encrypt(text, 7));
        System.out.println("Vigenere(KEY): " + Vigenere.encrypt(text, "KEY"));
        System.out.println("MD5: " + MD5.hex(text.getBytes(StandardCharsets.UTF_8)));
        System.out.println("SHA256: " + SHA256.hex(text.getBytes(StandardCharsets.UTF_8)));
        System.out.println("Base64: " + Base64Cipher.encode(text.getBytes(StandardCharsets.UTF_8)));

        byte[] rc4 = RC4.encrypt(text.getBytes(StandardCharsets.UTF_8), "secret".getBytes());
        System.out.println("RC4(hex): " + bytesToHex(rc4));
    }

    private static String bytesToHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }
}
