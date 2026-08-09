package com.zerone.crypto.simple;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * MD5 (RFC 1321) —— 教学实现,32 位小端整数运算。
 */
public final class MD5 {

    private static final int[] S = concat(
            reps(new int[]{7, 12, 17, 22}, 4),
            reps(new int[]{5, 9, 14, 20}, 4),
            reps(new int[]{4, 11, 16, 23}, 4),
            reps(new int[]{6, 10, 15, 21}, 4));

    private static int[] concat(int[]... arrs) {
        int n = 0;
        for (int[] a : arrs) n += a.length;
        int[] r = new int[n];
        int o = 0;
        for (int[] a : arrs) { System.arraycopy(a, 0, r, o, a.length); o += a.length; }
        return r;
    }

    private static int[] reps(int[] a, int times) {
        int[] r = new int[a.length * times];
        for (int t = 0; t < times; t++) System.arraycopy(a, 0, r, t * a.length, a.length);
        return r;
    }

    private static int[] K() {
        int[] k = new int[64];
        for (int i = 0; i < 64; i++)
            k[i] = (int) ((long) (Math.abs(Math.sin(i + 1)) * (1L << 32)) & 0xFFFFFFFFL);
        return k;
    }

    public static byte[] digest(byte[] msg) {
        int a0 = 0x67452301, b0 = 0xEFCDAB89, c0 = 0x98BADCFE, d0 = 0x10325476;
        int[] K = K();

        ByteBuffer pad = ByteBuffer.allocate(((msg.length + 8) / 64 + 1) * 64)
                .order(ByteOrder.LITTLE_ENDIAN);
        pad.put(msg);
        pad.put((byte) 0x80);
        while (pad.position() % 64 != 56) pad.put((byte) 0);
        pad.putLong((long) msg.length * 8);
        byte[] m = pad.array();

        for (int off = 0; off < m.length; off += 64) {
            int[] M = new int[16];
            for (int i = 0; i < 16; i++)
                M[i] = ByteBuffer.wrap(m, off + i * 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            int A = a0, B = b0, C = c0, D = d0;
            for (int i = 0; i < 64; i++) {
                int F;
                int g;
                if (i < 16) { F = (B & C) | (~B & D); g = i; }
                else if (i < 32) { F = (D & B) | (~D & C); g = (5 * i + 1) % 16; }
                else if (i < 48) { F = B ^ C ^ D; g = (3 * i + 5) % 16; }
                else { F = C ^ (B | ~D); g = (7 * i) % 16; }
                F = add(F, A); F = add(F, K[i]); F = add(F, M[g]);
                F = rotl(F, S[i]);
                F = add(F, B);
                A = D; D = C; C = B; B = F;
            }
            a0 = add(a0, A); b0 = add(b0, B); c0 = add(c0, C); d0 = add(d0, D);
        }

        ByteBuffer out = ByteBuffer.allocate(16).order(ByteOrder.LITTLE_ENDIAN);
        out.putInt(a0); out.putInt(b0); out.putInt(c0); out.putInt(d0);
        return out.array();
    }

    public static String hex(byte[] data) {
        return toHex(digest(data));
    }

    private static int add(int a, int b) { return (int) ((a & 0xFFFFFFFFL) + (b & 0xFFFFFFFFL)); }
    private static int rotl(int n, int s) { return (n << s) | (n >>> (32 - s)); }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private MD5() {}
}
