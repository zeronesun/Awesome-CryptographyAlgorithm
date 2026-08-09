package com.zerone.crypto.simple;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * SHA-256 (FIPS 180-4) —— 教学实现,32 位大端整数运算。
 */
public final class SHA256 {

    private static final int[] K = {
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1,
            0x923f82a4, 0xab1c5ed5, 0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3,
            0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174, 0xe49b69c1, 0xefbe4786,
            0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147,
            0x06ca6351, 0x14292967, 0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13,
            0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85, 0xa2bfe8a1, 0xa81a664b,
            0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a,
            0x5b9cca4f, 0x682e6ff3, 0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208,
            0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2,
    };

    public static byte[] digest(byte[] msg) {
        int[] H = {
                0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f,
                0x9b05688c, 0x1f83d9ab, 0x5be0cd19
        };

        ByteBuffer pad = ByteBuffer.allocate(((msg.length + 8) / 64 + 1) * 64)
                .order(ByteOrder.BIG_ENDIAN);
        pad.put(msg);
        pad.put((byte) 0x80);
        while (pad.position() % 64 != 56) pad.put((byte) 0);
        pad.putLong((long) msg.length * 8);
        byte[] m = pad.array();

        for (int off = 0; off < m.length; off += 64) {
            int[] w = new int[64];
            for (int i = 0; i < 16; i++)
                w[i] = ByteBuffer.wrap(m, off + i * 4, 4).order(ByteOrder.BIG_ENDIAN).getInt();
            for (int i = 16; i < 64; i++) {
                int s0 = rotr(w[i - 15], 7) ^ rotr(w[i - 15], 18) ^ (w[i - 15] >>> 3);
                int s1 = rotr(w[i - 2], 17) ^ rotr(w[i - 2], 19) ^ (w[i - 2] >>> 10);
                w[i] = add(w[i - 16], add(s0, add(w[i - 7], s1)));
            }

            int a = H[0], b = H[1], c = H[2], d = H[3], e = H[4], f = H[5], g = H[6], h = H[7];
            for (int i = 0; i < 64; i++) {
                int S1 = rotr(e, 6) ^ rotr(e, 11) ^ rotr(e, 25);
                int ch = (e & f) ^ (~e & g);
                int t1 = add(h, add(S1, add(ch, add(K[i], w[i]))));
                int S0 = rotr(a, 2) ^ rotr(a, 13) ^ rotr(a, 22);
                int maj = (a & b) ^ (a & c) ^ (b & c);
                int t2 = add(S0, maj);
                h = g; g = f; f = e; e = add(d, t1); d = c; c = b; b = a; a = add(t1, t2);
            }
            H[0] = add(H[0], a); H[1] = add(H[1], b); H[2] = add(H[2], c); H[3] = add(H[3], d);
            H[4] = add(H[4], e); H[5] = add(H[5], f); H[6] = add(H[6], g); H[7] = add(H[7], h);
        }

        ByteBuffer out = ByteBuffer.allocate(32).order(ByteOrder.BIG_ENDIAN);
        for (int x : H) out.putInt(x);
        return out.array();
    }

    public static String hex(byte[] data) { return toHex(digest(data)); }

    private static int add(int a, int b) { return (int) ((a & 0xFFFFFFFFL) + (b & 0xFFFFFFFFL)); }
    private static int rotr(int n, int s) { return (n >>> s) | (n << (32 - s)); }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private SHA256() {}
}
