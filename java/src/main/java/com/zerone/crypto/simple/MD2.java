package com.zerone.crypto.simple;

/**
 * MD2 (RFC 1319) —— 教学实现。
 * 128 位摘要,16 字节分组,带校验和机制。本实现为一次性接口,
 * 内部按 16 字节分组处理(等价于 RFC 1319 的 init/update/final 流程)。
 */
public final class MD2 {

    private static final int[] S = {
            41, 46, 67, 201, 162, 216, 124, 1, 61, 54, 84, 161, 236, 240, 6,
            19, 98, 167, 5, 243, 192, 199, 115, 140, 152, 147, 43, 217, 188,
            76, 130, 202, 30, 155, 87, 60, 253, 212, 224, 22, 103, 66, 111, 24,
            138, 23, 229, 18, 190, 78, 196, 214, 218, 158, 222, 73, 160, 251,
            245, 142, 187, 47, 238, 122, 169, 104, 121, 145, 21, 178, 7, 63,
            148, 194, 16, 137, 11, 34, 95, 33, 128, 127, 93, 154, 90, 144, 50,
            39, 53, 62, 204, 231, 191, 247, 151, 3, 255, 25, 48, 179, 72, 165,
            181, 209, 215, 94, 146, 42, 172, 86, 170, 198, 79, 184, 56, 210,
            150, 164, 125, 182, 118, 252, 107, 226, 156, 116, 4, 241, 69, 157,
            112, 89, 100, 113, 135, 32, 134, 91, 207, 101, 230, 45, 168, 2, 27,
            96, 37, 173, 174, 176, 185, 246, 28, 70, 97, 105, 52, 64, 126, 15,
            85, 71, 163, 35, 221, 81, 175, 58, 195, 92, 249, 206, 186, 197,
            234, 38, 44, 83, 13, 110, 133, 40, 132, 9, 211, 223, 205, 244, 65,
            129, 77, 82, 106, 220, 55, 200, 108, 193, 171, 250, 36, 225, 123,
            8, 12, 189, 177, 74, 120, 136, 149, 139, 227, 99, 232, 109, 233,
            203, 213, 254, 59, 0, 29, 57, 242, 239, 183, 14, 102, 88, 208, 228,
            166, 119, 114, 248, 235, 117, 75, 10, 49, 68, 80, 180, 143, 237,
            31, 26, 219, 153, 141, 51, 159, 17, 131, 20
    };

    public static byte[] digest(byte[] msg) {
        int[] state = new int[48];
        int[] checksum = new int[16];
        int[] data = new int[16];

        int[] m = pad(msg);
        for (int off = 0; off < m.length; off += 16) {
            for (int j = 0; j < 16; j++) data[j] = m[off + j];
            transform(state, checksum, data);
        }

        // final: append the checksum block
        for (int j = 0; j < 16; j++) data[j] = checksum[j];
        transform(state, checksum, data);

        byte[] out = new byte[16];
        for (int i = 0; i < 16; i++) out[i] = (byte) state[i];
        return out;
    }

    public static String hex(byte[] msg) {
        return toHex(digest(msg));
    }

    private static void transform(int[] state, int[] checksum, int[] data) {
        for (int j = 0; j < 16; j++) {
            state[j + 16] = data[j];
            state[j + 32] = (data[j] ^ state[j]) & 0xFF;
        }

        int t = 0;
        for (int j = 0; j < 18; j++) {
            for (int k = 0; k < 48; k++) {
                state[k] = (state[k] ^ S[t]) & 0xFF;
                t = state[k];
            }
            t = (t + j) & 0xFF;
        }

        t = checksum[15];
        for (int j = 0; j < 16; j++) {
            checksum[j] = (checksum[j] ^ S[data[j] ^ t]) & 0xFF;
            t = checksum[j];
        }
    }

    private static int[] pad(byte[] msg) {
        int padLen = 16 - (msg.length % 16);
        if (padLen == 0) padLen = 16;          // always pad 1..16 bytes
        int[] m = new int[msg.length + padLen];
        for (int i = 0; i < msg.length; i++) m[i] = msg[i] & 0xFF;
        for (int i = msg.length; i < m.length; i++) m[i] = padLen;
        return m;
    }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private MD2() {}
}
