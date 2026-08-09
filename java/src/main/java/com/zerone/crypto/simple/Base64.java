package com.zerone.crypto.simple;

import java.nio.charset.StandardCharsets;

/**
 * Base64 —— 教学实现
 * 原理:把 3 个字节(24 位)拆成 4 个 6 位组,每组映射到 64 字符表,
 * 末尾不足 3 字节用 '=' 填充。
 */
public class Base64 {

    private static final char[] TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

    public static String encode(byte[] data) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i + 3 <= data.length) {
            int n = ((data[i] & 0xff) << 16) | ((data[i + 1] & 0xff) << 8) | (data[i + 2] & 0xff);
            sb.append(TABLE[(n >>> 18) & 63]);
            sb.append(TABLE[(n >>> 12) & 63]);
            sb.append(TABLE[(n >>> 6) & 63]);
            sb.append(TABLE[n & 63]);
            i += 3;
        }
        int remain = data.length - i;
        if (remain == 1) {
            int n = (data[i] & 0xff) << 16;
            sb.append(TABLE[(n >>> 18) & 63]);
            sb.append(TABLE[(n >>> 12) & 63]);
            sb.append("==");
        } else if (remain == 2) {
            int n = ((data[i] & 0xff) << 16) | ((data[i + 1] & 0xff) << 8);
            sb.append(TABLE[(n >>> 18) & 63]);
            sb.append(TABLE[(n >>> 12) & 63]);
            sb.append(TABLE[(n >>> 6) & 63]);
            sb.append('=');
        }
        return sb.toString();
    }

    public static byte[] decode(String s) {
        java.util.List<Byte> out = new java.util.ArrayList<>();
        java.util.Map<Character, Integer> rev = new java.util.HashMap<>();
        for (int i = 0; i < TABLE.length; i++) rev.put(TABLE[i], i);
        int buf = 0, bits = 0;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '=') break;
            Integer val = rev.get(c);
            if (val == null) continue;
            buf = (buf << 6) | val;
            bits += 6;
            while (bits >= 8) {
                out.add((byte) ((buf >>> (bits - 8)) & 0xff));
                bits -= 8;
            }
            buf &= (1 << bits) - 1;
        }
        byte[] r = new byte[out.size()];
        for (int j = 0; j < out.size(); j++) r[j] = out.get(j);
        return r;
    }

    private static final java.util.List<Byte> out = new java.util.ArrayList<>();

    public static void main(String[] args) {
        String s = "Hello";
        System.out.println(encode(s.getBytes()));
    }
}
