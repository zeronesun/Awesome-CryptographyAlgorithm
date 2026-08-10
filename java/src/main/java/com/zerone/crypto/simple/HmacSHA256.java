package com.zerone.crypto.simple;

/**
 * HMAC-SHA256 (RFC 2104) —— 教学实现,基于本包的 SHA256。
 *   inner = SHA256( (key ^ ipad) || message )
 *   outer = SHA256( (key ^ opad) || inner )
 * SHA-256 分组长度 B = 64,输出长度 L = 32。
 */
public final class HmacSHA256 {

    private static final int BLOCK = 64;

    public static byte[] mac(byte[] key, byte[] msg) {
        byte[] k = new byte[BLOCK];
        if (key.length > BLOCK) {
            byte[] h = SHA256.digest(key);     // key longer than B -> hash it
            System.arraycopy(h, 0, k, 0, h.length);
        } else {
            System.arraycopy(key, 0, k, 0, key.length);
        }

        byte[] ipad = new byte[BLOCK];
        byte[] opad = new byte[BLOCK];
        for (int i = 0; i < BLOCK; i++) {
            ipad[i] = (byte) ((k[i] & 0xFF) ^ 0x36);
            opad[i] = (byte) ((k[i] & 0xFF) ^ 0x5c);
        }

        byte[] inner = SHA256.digest(concat(ipad, msg));
        return SHA256.digest(concat(opad, inner));
    }

    public static String hex(byte[] key, byte[] msg) {
        return toHex(mac(key, msg));
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    private static String toHex(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte x : b) sb.append(String.format("%02x", x));
        return sb.toString();
    }

    private HmacSHA256() {}
}
