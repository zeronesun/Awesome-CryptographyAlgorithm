package com.zerone.crypto.simple;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * ChaCha20 (RFC 7539) 纯 Java 教学实现.
 * 未使用任何外部库,完全基于 RFC 7539 的规范,便于跨平台理解。
 */
public final class ChaCha20 {
    private static final int[] SIGMA = {
        0x61707865,  /* "expa" */
        0x3320646E,  /* "nd 32" */
        0x79622D32,  /* "-by " */
        0x6B206574   /* "te k" */
    };

    private static int rotl32(final int v, final int n) {
        return ((v << n) | (v >>> (32 - n)));
    }

    private static void quarterRound(final int[] state,
                                     final int a, final int b,
                                     final int c, final int d) {
        state[a] += state[b];
        state[d] ^= state[a];
        state[d] = rotl32(state[d], 16);
        state[c] += state[d];
        state[b] ^= state[c];
        state[b] = rotl32(state[b], 12);
        state[a] += state[b];
        state[d] ^= state[a];
        state[d] = rotl32(state[d],  8);
        state[c] += state[d];
        state[b] ^= state[c];
        state[b] = rotl32(state[b],  7);
    }

    private static void doubleRound(final int[] state) {
        // Column rounds
        for (int i = 0; i < 2; i++) {
            quarterRound(state, 0, 4, 8,12);
            quarterRound(state, 1, 5, 9,13);
            quarterRound(state, 2, 6,10,14);
            quarterRound(state, 3, 7,11,15);
        }
        // Diagonal rounds
        quarterRound(state, 0, 5,10,15);
        quarterRound(state, 1, 6,11,12);
        quarterRound(state, 2, 7, 8,13);
        quarterRound(state, 3, 4, 9,14);
        quarterRound(state, 0, 5,10,15);
        quarterRound(state, 1, 6,11,12);
        quarterRound(state, 2, 7, 8,13);
        quarterRound(state, 3, 4, 9,14);
    }

    public static byte[] chacha20Block(final byte[] key,
                                       final byte[] nonce,
                                       final int counter) {
        if (key.length != 32) {
            throw new IllegalArgumentException("ChaCha20 key must be 32 bytes");
        }
        if (nonce.length != 12) {
            throw new IllegalArgumentException("ChaCha20 nonce must be 12 bytes");
        }
        final int[] state = new int[16];
        // 态: sigma(4) + key(8) + counter(1) + nonce(3)
        System.arraycopy(SIGMA, 0, state, 0, 4);
        System.arraycopy(bytesToInt32Array(key), 0, state, 4, 8);
        state[12] = counter;
        System.arraycopy(bytesToInt32Array(nonce), 0, state, 13, 3);

        // 记录初始状态（稍后加法）
        final int[] init = state.clone();
        // 10 次 doubleRound
        for (int i = 0; i < 10; i++) {
            doubleRound(state);
        }
        // 每个整数加初始值
        for (int i = 0; i < 16; i++) {
            state[i] += init[i];
        }
        // 序列化为小端 64 字节
        return int32ArrayToBytes(state);
    }

    public static byte[] chacha20Crypt(final byte[] key,
                                       final byte[] nonce,
                                       final int counter,
                                       final byte[] plaintext) {
        if (counter < 0 || counter > 0xFFFFFFFFL) {
            throw new IllegalArgumentException("counter out of 32-bit range");
        }
        final byte[] out = new byte[plaintext.length];
        int offset = 0;
        int currentCounter = counter;
        while (offset < plaintext.length) {
            byte[] keystream = chacha20Block(key, nonce, currentCounter);
            int step = Math.min(keystream.length, plaintext.length - offset);
            for (int i = 0; i < step; i++) {
                out[offset + i] = (byte)(plaintext[offset + i] ^ keystream[i]);
            }
            offset += step;
            currentCounter++;
        }
        return out;
    }

    private static int[] bytesToInt32Array(final byte[] bytes) {
        final int count = bytes.length / 4;
        final int[] result = new int[count];
        for (int i = 0; i < count; i++) {
            // 小端
            final int b3 = (bytes[4*i+3] & 0xFF) << 24;
            final int b2 = (bytes[4*i+2] & 0xFF) << 16;
            final int b1 = (bytes[4*i+1] & 0xFF) <<  8;
            final int b0 = (bytes[4*i]   & 0xFF);
            result[i] = b3 | b2 | b1 | b0;
        }
        return result;
    }

    private static byte[] int32ArrayToBytes(final int[] values) {
        final byte[] out = new byte[values.length * 4];
        for (int i = 0; i < values.length; i++) {
            final int v = values[i];
            out[4*i+0] = (byte)(v);
            out[4*i+1] = (byte)(v >>>  8);
            out[4*i+2] = (byte)(v >>> 16);
            out[4*i+3] = (byte)(v >>> 24);
        }
        return out;
    }

    private static final char[] HEX_CHARS = "0123456789abcdef".toCharArray();

    private static String bytesToHex(final byte[] bytes) {
        final char[] out = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            final int v = bytes[i] & 0xFF;
            out[2*i]   = HEX_CHARS[v >>> 4];
            out[2*i+1] = HEX_CHARS[v & 0x0F];
        }
        return new String(out);
    }

    public static void main(final String[] args) {
        final byte[] key = hexStringToByteArray(
                "000102030405060708090A0B0C0D0E0F101112131415161718191A1B1C1D1E1F");
        final byte[] nonce = hexStringToByteArray("0000000900004A0000000031");
        final int counter = 1;
        final byte[] plain = "Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it."
                .getBytes(java.nio.charset.StandardCharsets.US_ASCII);
        final byte[] expect = hexStringToByteArray(
                "6E2E359A2568F98041BA0728DD0D6981E97E7AEC1D4360C20A27AFFCD9FAE0BF91B65C5524733AB8F593DAB62CD2BB0992704736F61E9C05D0B6BC3E36F29856F1342115E901F9EA852A430304AA46B564FB4F037468B5E5F3604342529252291873C57F3EE8D08B36E4E45B5C408");
        final byte[] cipher = chacha20Crypt(key, nonce, counter, plain);
        final boolean ok = java.util.Arrays.equals(cipher, expect);
        System.out.println("RFC 7539 A.1: " + (ok ? "PASS" : "FAIL"));
    }

    private static byte[] hexStringToByteArray(final String s) {
        final int len = s.length();
        final byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
