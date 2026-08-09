package com.zerone.crypto.simple;

/**
 * DES (FIPS 46-3) —— 教学实现,ECB 模式。
 */
public final class DES {

    private static final int[][] SBOX = {
            {{14,4,13,1,2,15,11,8,3,10,6,12,5,9,0,7},
             {0,15,7,4,14,2,13,1,10,6,12,11,9,5,3,8},
             {4,1,14,8,13,6,2,11,15,12,9,7,3,10,5,0},
             {15,12,8,2,4,9,1,7,5,11,3,14,10,0,6,13}},
            {{15,1,8,14,6,11,3,4,9,7,2,13,12,0,5,10},
             {3,13,4,7,15,2,8,14,12,0,1,10,6,9,11,5},
             {0,14,7,11,10,4,13,1,5,8,12,6,9,3,2,15},
             {13,8,10,1,3,15,4,2,11,6,7,12,0,5,14,9}},
            {{10,0,9,14,6,3,15,5,1,13,12,7,11,4,2,8},
             {13,7,0,9,3,4,6,10,2,8,5,14,12,11,15,1},
             {13,6,4,9,8,15,3,0,11,1,2,12,5,10,14,7},
             {1,10,13,0,6,9,8,7,4,15,14,3,11,5,2,12}},
            {{7,13,14,3,0,6,9,10,1,2,8,5,11,12,4,15},
             {13,8,11,5,6,15,0,3,4,7,2,12,1,10,14,9},
             {10,6,9,0,12,11,7,13,15,1,3,14,5,2,8,4},
             {3,15,0,6,10,1,13,8,9,4,5,11,12,7,2,14}},
            {{2,12,4,1,7,10,11,6,8,5,3,15,13,0,14,9},
             {14,11,2,12,4,7,13,1,5,0,15,10,3,9,8,6},
             {4,2,1,11,10,13,7,8,15,9,12,5,6,3,0,14},
             {11,8,12,7,1,14,2,13,6,15,0,9,10,4,5,3}},
            {{12,1,10,15,9,2,6,8,0,13,3,4,14,7,5,11},
             {10,15,4,2,7,12,9,5,6,1,13,14,0,11,3,8},
             {9,14,15,5,2,8,12,3,7,0,4,10,1,13,11,6},
             {4,3,2,12,9,5,15,10,11,14,1,7,6,0,8,13}},
            {{4,11,2,14,15,0,8,13,3,12,9,7,5,10,6,1},
             {13,0,11,7,4,9,1,10,14,3,5,12,2,15,8,6},
             {1,4,11,13,12,3,7,14,10,15,6,8,0,5,9,2},
             {6,11,13,8,1,4,10,7,9,5,0,15,14,2,3,12}},
            {{13,2,8,4,6,15,11,1,10,9,3,14,5,0,12,7},
             {1,15,13,8,10,3,7,4,12,5,6,11,0,14,9,2},
             {7,11,4,1,9,12,14,2,0,6,10,13,15,3,5,8},
             {2,1,14,7,4,10,8,13,15,12,9,0,3,5,6,11}}
    };

    private static final int[] PC1 = {
            57,49,41,33,25,17,9,1,58,50,42,34,26,18,10,2,59,51,43,35,27,19,11,3,
            60,52,44,36,63,55,47,39,31,23,15,7,62,54,46,38,30,22,14,6,61,53,45,
            37,29,21,13,5,28,20,12,4
    };
    private static final int[] PC2 = {
            14,17,11,24,1,5,3,28,15,6,21,10,23,19,12,4,26,8,16,7,27,20,13,2,
            41,52,31,37,47,55,30,40,51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32
    };
    private static final int[] IP = {
            58,50,42,34,26,18,10,2,60,52,44,36,28,20,12,4,62,54,46,38,30,22,14,6,
            64,56,48,40,32,24,16,8,57,49,41,33,25,17,9,1,59,51,43,35,27,19,11,3,
            61,53,45,37,29,21,13,5,63,55,47,39,31,23,15,7
    };
    private static final int[] FP = {
            40,8,48,16,56,24,64,32,39,7,47,15,55,23,63,31,38,6,46,14,54,22,62,30,
            37,5,45,13,53,21,61,29,36,4,44,12,52,20,60,28,35,3,43,11,51,19,59,27,
            34,2,42,10,50,18,58,26,33,1,41,9,49,17,57,25
    };
    private static final int[] E = {
            32,1,2,3,4,5,4,5,6,7,8,9,8,9,10,11,12,13,12,13,14,15,16,17,16,17,18,
            19,20,21,20,21,22,23,24,25,24,25,26,27,28,29,28,29,30,31,32,1
    };
    private static final int[] P = {
            16,7,20,21,29,12,28,17,1,15,23,26,5,18,31,10,2,8,24,14,32,27,3,9,19,
            13,30,6,22,11,4,25
    };
    private static final int[] ROT = {1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1};

    public static byte[] encryptBlock(byte[] block, byte[] key) {
        return run(block, key, false);
    }

    public static byte[] decryptBlock(byte[] block, byte[] key) {
        return run(block, key, true);
    }

    public static byte[] encryptECB(byte[] data, byte[] key) {
        return ecb(data, key, false);
    }

    public static byte[] decryptECB(byte[] data, byte[] key) {
        return ecb(data, key, true);
    }

    private static byte[] ecb(byte[] data, byte[] key, boolean decrypt) {
        int pad = 8 - (data.length % 8);
        byte[] in = decrypt ? data : java.util.Arrays.copyOf(data, data.length + pad);
        if (!decrypt) for (int i = data.length; i < in.length; i++) in[i] = (byte) pad;
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i += 8)
            System.arraycopy(decrypt ? decryptBlock(sub(in, i), key) : encryptBlock(sub(in, i), key),
                    0, out, i, 8);
        if (decrypt) {
            int p = out[out.length - 1] & 0xff;
            return java.util.Arrays.copyOf(out, out.length - p);
        }
        return out;
    }

    private static byte[] sub(byte[] a, int off) {
        return java.util.Arrays.copyOfRange(a, off, off + 8);
    }

    private static byte[] run(byte[] block, byte[] key, boolean decrypt) {
        int[] subkeys = subkeys(key);
        if (decrypt) {
            int[] rev = new int[16];
            for (int i = 0; i < 16; i++) rev[i] = subkeys[15 - i];
            subkeys = rev;
        }
        int[] b = bits(block);
        b = permute(b, IP);
        int[] L = java.util.Arrays.copyOfRange(b, 0, 32);
        int[] R = java.util.Arrays.copyOfRange(b, 32, 64);
        for (int i = 0; i < 16; i++) {
            int[] newR = xor(L, f(R, subkeys[i]));
            L = R;
            R = newR;
        }
        int[] merged = concat(R, L);
        merged = permute(merged, FP);
        return toBytes(merged);
    }

    private static int[] f(int[] R, int[] k) {
        int[] e = permute(R, E);
        int[] x = xor(e, k);
        int[] s = new int[32];
        for (int i = 0; i < 8; i++) {
            int[] g = java.util.Arrays.copyOfRange(x, i * 6, i * 6 + 6);
            int row = g[0] * 2 + g[5];
            int col = g[1] * 8 + g[2] * 4 + g[3] * 2 + g[4];
            int v = SBOX[i][row][col];
            for (int j = 3; j >= 0; j--) s[i * 4 + (3 - j)] = (v >> j) & 1;
        }
        return permute(s, P);
    }

    private static int[] subkeys(byte[] key) {
        int[] kb = bits(key);
        int[] k = permute(kb, PC1);
        int[] c = java.util.Arrays.copyOfRange(k, 0, 28);
        int[] d = java.util.Arrays.copyOfRange(k, 28, 56);
        int[][] res = new int[16][];
        for (int r = 0; r < 16; r++) {
            c = rot(c, ROT[r]);
            d = rot(d, ROT[r]);
            res[r] = permute(concat(c, d), PC2);
        }
        return collect(res);
    }

    private static int[] collect(int[][] m) {
        int[] out = new int[16 * 48];
        for (int i = 0; i < 16; i++) System.arraycopy(m[i], 0, out, i * 48, 48);
        return out;
    }

    private static int[] rot(int[] a, int n) {
        int[] r = new int[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[(i + n) % a.length];
        return r;
    }

    private static int[] permute(int[] b, int[] table) {
        int[] r = new int[table.length];
        for (int i = 0; i < table.length; i++) r[i] = b[table[i] - 1];
        return r;
    }

    private static int[] xor(int[] a, int[] b) {
        int[] r = new int[a.length];
        for (int i = 0; i < a.length; i++) r[i] = a[i] ^ b[i];
        return r;
    }

    private static int[] concat(int[] a, int[] b) {
        int[] r = new int[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    private static int[] bits(byte[] data) {
        int[] r = new int[data.length * 8];
        for (int i = 0; i < data.length; i++)
            for (int j = 7; j >= 0; j--) r[i * 8 + (7 - j)] = (data[i] >> j) & 1;
        return r;
    }

    private static byte[] toBytes(int[] bits) {
        byte[] out = new byte[bits.length / 8];
        for (int i = 0; i < out.length; i++) {
            int v = 0;
            for (int j = 0; j < 8; j++) v = (v << 1) | bits[i * 8 + j];
            out[i] = (byte) v;
        }
        return out;
    }

    private DES() {}
}
