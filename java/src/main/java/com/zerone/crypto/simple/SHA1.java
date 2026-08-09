package com.zerone.crypto.simple;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * SHA-1 (FIPS 180-4) —— 教学实现,32 位大端整数运算。
 */
public final class SHA1 {

    private static final int[] K = {
            0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6
    };

    public static byte[] digest(byte[] msg) {
        int h0 = 0x67452301, h1 = 0xEFCDAB89, h2 = 0x98BADCFE, h3 = 0x10325476, h4 = 0xC3D2E1F0;

        ByteBuffer pad = ByteBuffer.allocate(((msg.length + 8) / 64 + 1) * 64)
                .order(ByteOrder.BIG_ENDIAN);
        pad.put(msg);
        pad.put((byte) 0x80);
        while (pad.position() % 64 != 56) pad.put((byte) 0);
        pad.putLong((long) msg.length * 8);
        byte[] m = pad.array();

        for (int off = 0; off < m.length; off += 64) {
            int[] w = new int[80];
            for (int i = 0; i < 16; i++)
                w[i] = ByteBuffer.wrap(m, off + i * 4, 4).order(ByteOrder.BIG_ENDIAN).getInt();
            for (int i = 16; i < 80; i++)
                w[i] = rotl(w[i - 3] ^ w[i - 8] ^ w[i - 14] ^ w[i - 16], 1);

            int a = h0, b = h1, c = h2, d = h3, e = h4;
            for (int i = 0; i < 80; i++) {
                int f;
                int k;
                if (i < 20) { f = (b & c) | (~b & d); k = K[0]; }
                else if (i < 40) { f = b ^ c ^ d; k = K[1]; }
                else if (i < 60) { f = (b & c) | (b & d) | (c & d); k = K[2]; }
                else { f = b ^ c ^ d; k = K[3]; }
                int tmp = add(rotl(a, 5), f);
                tmp = add(tmp, e);
                tmp = add(tmp, k);
                tmp = add(tmp, w[i]);
                e = d; d = c; c = rotl(b, 30); b = a; a = tmp;
            }
            h0 = add(h0, a); h1 = add(h1, b); h2 = add(h2, c); h3 = add(h3, d); h4 = add(h4, e);
        }

        ByteBuffer out = ByteBuffer.allocate(20).order(ByteOrder.BIG_ENDIAN);
        out.putInt(h0); out.putInt(h1); out.putInt(h2); out.putInt(h3); out.putInt(h4);
        return out.array();
    }

    public static String hex(byte[] data) { return toHex(digest(data)); }

    private static int add(int a, int b) { return (int) ((a & 0xFFFFFFFFL) + (b & 0xFFFFFFFFL)); }
    private static int rotl(int n, int s) { return (n << s) | (n >>> (32 - s)); }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private SHA1() {}
}
