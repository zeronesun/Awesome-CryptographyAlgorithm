package com.zerone.crypto.simple;

import java.util.Base64;

/**
 * Base64 标准库对照实现
 * 教学目的:演示标准库 API 的使用
 */
public final class Base64Cipher {

    /** Base64 编码 (标准库对照) */
    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /** Base64 解码 (标准库对照) */
    public static byte[] decode(String encoded) {
        return Base64.getDecoder().decode(encoded);
    }

    private Base64Cipher() {}
}
