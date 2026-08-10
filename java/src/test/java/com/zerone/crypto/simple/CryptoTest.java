package com.zerone.crypto.simple;

import org.junit.Test;
import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;

public class CryptoTest {

    private static final byte[] EMPTY = new byte[0];

    @Test
    public void testCaesar() {
        String enc = Caesar.encrypt("Hello, World!", 7);
        assertEquals("Hello, World!", Caesar.decrypt(enc, 7));
    }

    @Test
    public void testVigenere() {
        String c = Vigenere.encrypt("attack at dawn", "LEMON");
        assertEquals("attack at dawn", Vigenere.decrypt(c, "LEMON"));
        assertEquals("LXFOPVEFRNHR", Vigenere.encrypt("ATTACKATDAWN", "LEMON"));
    }

    @Test
    public void testRC4() {
        byte[] ct = RC4.encrypt("Plaintext".getBytes(), "Key".getBytes());
        assertArrayEquals(new byte[]{(byte) 0xbb, (byte) 0xf3, 0x16, (byte) 0xe8,
                (byte) 0xd9, 0x40, (byte) 0xaf, 0x0a, (byte) 0xd3}, ct);
        assertArrayEquals("Plaintext".getBytes(),
                RC4.decrypt(ct, "Key".getBytes()));
    }

    @Test
    public void testMD5() {
        assertEquals("5d41402abc4b2a76b9719d911017c592",
                MD5.hex("hello".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testSHA1() {
        assertEquals("aaf4c61ddcc5e8a2dabede0f3b482cd9aea9434d",
                SHA1.hex("hello".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testSHA256() {
        assertEquals("2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824",
                SHA256.hex("hello".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testBase64() {
        String s = "Hello, Base64!";
        assertEquals(s, new String(Base64Cipher.decode(Base64Cipher.encode(s.getBytes()))));
    }

    @Test
    public void testMD2() {
        assertEquals("8350e5a3e24c153df2275c9f80692773",
                MD2.hex("".getBytes(StandardCharsets.UTF_8)));
        assertEquals("da853b0d3f88d99b30283a69e6ded6bb",
                MD2.hex("abc".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testHmacSHA256() {
        byte[] key = new byte[20];
        for (int i = 0; i < key.length; i++) key[i] = 0x0b;
        assertEquals("b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7",
                HmacSHA256.hex(key, "Hi There".getBytes(StandardCharsets.UTF_8)));
        assertEquals("5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843",
                HmacSHA256.hex("Jefe".getBytes(StandardCharsets.UTF_8),
                        "what do ya want for nothing?".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testDES() {
        byte[] key = hex("133457799BBCDFF1");
        byte[] pt = hex("0123456789ABCDEF");
        assertArrayEquals(hex("85E813540F0AB405"), DES.encryptBlock(pt, key));
        assertEquals("hello!!", new String(DES.decryptECB(DES.encryptECB("hello!!".getBytes(), key), key)));
    }

    @Test
    public void testAES() {
        byte[] key = hex("000102030405060708090a0b0c0d0e0f");
        byte[] pt = hex("00112233445566778899aabbccddeeff");
        assertArrayEquals(hex("69C4E0D86A7B0430D8CDB78070B4C55A"), AES.encryptBlock(pt, key));
        assertEquals("hello!!", new String(AES.decryptECB(AES.encryptECB("hello!!".getBytes(), key), key)));
    }

    private static byte[] hex(String s) {
        byte[] out = new byte[s.length() / 2];
        for (int i = 0; i < out.length; i++)
            out[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        return out;
    }
}
