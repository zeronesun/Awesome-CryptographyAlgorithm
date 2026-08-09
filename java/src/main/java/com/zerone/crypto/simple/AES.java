package com.zerone.crypto.simple;

/**
 * AES (FIPS 197) —— 教学实现,支持 128/192/256 位密钥,ECB 模式。
 */
public final class AES {

    private static final int[] SBOX = {
            0x63,0x7c,0x77,0x7b,0xf2,0x6b,0x6f,0xc5,0x30,0x01,0x67,0x2b,0xfe,0xd7,0xab,0x76,
            0xca,0x82,0xc9,0x7d,0xfa,0x59,0x47,0xf0,0xad,0xd4,0xa2,0xaf,0x9c,0xa4,0x72,0xc0,
            0xb7,0xfd,0x93,0x26,0x36,0x3f,0xf7,0xcc,0x34,0xa5,0xe5,0xf1,0x71,0xd8,0x31,0x15,
            0x04,0xc7,0x23,0xc3,0x18,0x96,0x05,0x9a,0x07,0x12,0x80,0xe2,0xeb,0x27,0xb2,0x75,
            0x09,0x83,0x2c,0x1a,0x1b,0x6e,0x5a,0xa0,0x52,0x3b,0xd6,0xb3,0x29,0xe3,0x2f,0x84,
            0x53,0xd1,0x00,0xed,0x20,0xfc,0xb1,0x5b,0x6a,0xcb,0xbe,0x39,0x4a,0x4c,0x58,0xcf,
            0xd0,0xef,0xaa,0xfb,0x43,0x4d,0x33,0x85,0x45,0xf9,0x02,0x7f,0x50,0x3c,0x9f,0xa8,
            0x51,0xa3,0x40,0x8f,0x92,0x9d,0x38,0xf5,0xbc,0xb6,0xda,0x21,0x10,0xff,0xf3,0xd2,
            0xcd,0x0c,0x13,0xec,0x5f,0x97,0x44,0x17,0xc4,0xa7,0x7e,0x3d,0x64,0x5d,0x19,0x73,
            0x60,0x81,0x4f,0xdc,0x22,0x2a,0x90,0x88,0x46,0xee,0xb8,0x14,0xde,0x5e,0x0b,0xdb,
            0xe0,0x32,0x3a,0x0a,0x49,0x06,0x24,0x5c,0xc2,0xd3,0xac,0x62,0x91,0x95,0xe4,0x79,
            0xe7,0xc8,0x37,0x6d,0x8d,0xd5,0x4e,0xa9,0x6c,0x56,0xf4,0xea,0x65,0x7a,0xae,0x08,
            0xba,0x78,0x25,0x2e,0x1c,0xa6,0xb4,0xc6,0xe8,0xdd,0x74,0x1f,0x4b,0xbd,0x8b,0x8a,
            0x70,0x3e,0xb5,0x66,0x48,0x03,0xf6,0x0e,0x61,0x35,0x57,0xb9,0x86,0xc1,0x1d,0x9e,
            0xe1,0xf8,0x98,0x11,0x69,0xd9,0x8e,0x94,0x9b,0x1e,0x87,0xe9,0xce,0x55,0x28,0xdf,
            0x8c,0xa1,0x89,0x0d,0xbf,0xe6,0x42,0x68,0x41,0x99,0x2d,0x0f,0xb0,0x54,0xbb,0x16
    };
    private static final int[] RCON = {0x01,0x02,0x04,0x08,0x10,0x20,0x40,0x80,0x1b,0x36};

    public static byte[] encryptBlock(byte[] block, byte[] key) {
        return block(block, key, false);
    }

    public static byte[] decryptBlock(byte[] block, byte[] key) {
        return block(block, key, true);
    }

    public static byte[] encryptECB(byte[] data, byte[] key) {
        return ecb(data, key, false);
    }

    public static byte[] decryptECB(byte[] data, byte[] key) {
        return ecb(data, key, true);
    }

    private static byte[] ecb(byte[] data, byte[] key, boolean decrypt) {
        int pad = 16 - (data.length % 16);
        byte[] in = decrypt ? data : java.util.Arrays.copyOf(data, data.length + pad);
        if (!decrypt) for (int i = data.length; i < in.length; i++) in[i] = (byte) pad;
        byte[] out = new byte[in.length];
        for (int i = 0; i < in.length; i += 16)
            System.arraycopy(decrypt ? decryptBlock(sub(in, i), key) : encryptBlock(sub(in, i), key),
                    0, out, i, 16);
        if (decrypt) {
            int p = out[out.length - 1] & 0xff;
            return java.util.Arrays.copyOf(out, out.length - p);
        }
        return out;
    }

    private static byte[] sub(byte[] a, int off) {
        return java.util.Arrays.copyOfRange(a, off, off + 16);
    }

    private static byte[] block(byte[] input, byte[] key, boolean decrypt) {
        int nk = key.length / 4;
        int nr = nk == 4 ? 10 : nk == 6 ? 12 : 14;
        int[][] w = keyExpansion(key, nk, nr);

        int[][] st = state(input);
        addRoundKey(st, w, 0);
        if (!decrypt) {
            for (int r = 1; r < nr; r++) {
                subBytes(st); shiftRows(st); mixColumns(st); addRoundKey(st, w, r);
            }
            subBytes(st); shiftRows(st); addRoundKey(st, w, nr);
        } else {
            invShiftRows(st); invSubBytes(st); addRoundKey(st, w, nr);
            for (int r = nr - 1; r >= 1; r--) {
                addRoundKey(st, w, r); invMixColumns(st); invShiftRows(st); invSubBytes(st);
            }
            addRoundKey(st, w, 0);
        }
        return fromState(st);
    }

    private static int[][] keyExpansion(byte[] key, int nk, int nr) {
        int[][] w = new int[nr + 1][4];
        for (int i = 0; i < nk; i++) {
            w[i][0] = key[4 * i] & 0xff;
            w[i][1] = key[4 * i + 1] & 0xff;
            w[i][2] = key[4 * i + 2] & 0xff;
            w[i][3] = key[4 * i + 3] & 0xff;
        }
        for (int i = nk; i < nr + 1; i++) {
            int[] temp = w[i - 1].clone();
            if (i % nk == 0) {
                temp = rotWord(temp); temp = subWord(temp);
                temp[0] ^= RCON[i / nk - 1];
            } else if (nk > 6 && i % nk == 4) {
                temp = subWord(temp);
            }
            w[i] = new int[]{w[i - nk][0] ^ temp[0], w[i - nk][1] ^ temp[1],
                    w[i - nk][2] ^ temp[2], w[i - nk][3] ^ temp[3]};
        }
        return w;
    }

    private static int[] rotWord(int[] a) { return new int[]{a[1], a[2], a[3], a[0]}; }
    private static int[] subWord(int[] a) {
        return new int[]{SBOX[a[0]], SBOX[a[1]], SBOX[a[2]], SBOX[a[3]]};
    }

    private static int[][] state(byte[] b) {
        int[][] s = new int[4][4];
        for (int c = 0; c < 4; c++) for (int r = 0; r < 4; r++) s[r][c] = b[r + 4 * c] & 0xff;
        return s;
    }

    private static byte[] fromState(int[][] s) {
        byte[] b = new byte[16];
        for (int c = 0; c < 4; c++) for (int r = 0; r < 4; r++) b[r + 4 * c] = (byte) s[r][c];
        return b;
    }

    private static void addRoundKey(int[][] st, int[][] w, int r) {
        for (int c = 0; c < 4; c++) for (int i = 0; i < 4; i++) st[i][c] ^= w[4 * r + c][i];
    }

    private static void subBytes(int[][] st) {
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) st[i][j] = SBOX[st[i][j]];
    }

    private static void shiftRows(int[][] st) {
        st[1] = rot(st[1], 1); st[2] = rot(st[2], 2); st[3] = rot(st[3], 3);
    }

    private static int[] rot(int[] a, int n) {
        int[] r = new int[4];
        for (int i = 0; i < 4; i++) r[i] = a[(i + n) % 4];
        return r;
    }

    private static int gmul(int a, int b) {
        int p = 0;
        for (int i = 0; i < 8; i++) {
            if ((b & 1) != 0) p ^= a;
            int hi = a & 0x80;
            a = (a << 1) & 0xff;
            if (hi != 0) a ^= 0x1b;
            b >>= 1;
        }
        return p & 0xff;
    }

    private static void mixColumns(int[][] st) {
        for (int c = 0; c < 4; c++) {
            int[] a = {st[0][c], st[1][c], st[2][c], st[3][c]};
            st[0][c] = gmul(a[0], 2) ^ gmul(a[1], 3) ^ a[2] ^ a[3];
            st[1][c] = a[0] ^ gmul(a[1], 2) ^ gmul(a[2], 3) ^ a[3];
            st[2][c] = a[0] ^ a[1] ^ gmul(a[2], 2) ^ gmul(a[3], 3);
            st[3][c] = gmul(a[0], 3) ^ a[1] ^ a[2] ^ gmul(a[3], 2);
        }
    }

    private static int[] invSbox() {
        int[] inv = new int[256];
        for (int i = 0; i < 256; i++) inv[SBOX[i]] = i;
        return inv;
    }
    private static final int[] INV_SBOX = invSbox();

    private static void invSubBytes(int[][] st) {
        for (int i = 0; i < 4; i++) for (int j = 0; j < 4; j++) st[i][j] = INV_SBOX[st[i][j]];
    }

    private static void invShiftRows(int[][] st) {
        st[1] = rot(st[1], 3); st[2] = rot(st[2], 2); st[3] = rot(st[3], 1);
    }

    private static void invMixColumns(int[][] st) {
        for (int c = 0; c < 4; c++) {
            int[] a = {st[0][c], st[1][c], st[2][c], st[3][c]};
            st[0][c] = gmul(a[0],14)^gmul(a[1],11)^gmul(a[2],13)^gmul(a[3],9);
            st[1][c] = gmul(a[0],9)^gmul(a[1],14)^gmul(a[2],11)^gmul(a[3],13);
            st[2][c] = gmul(a[0],13)^gmul(a[1],9)^gmul(a[2],14)^gmul(a[3],11);
            st[3][c] = gmul(a[0],11)^gmul(a[1],13)^gmul(a[2],9)^gmul(a[3],14);
        }
    }

    private AES() {}
}
