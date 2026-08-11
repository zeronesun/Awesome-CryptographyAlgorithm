# ChaCha20 (RFC 7539)

## 1. 概述

- **类型**: **流密码** (Stream Cipher)，专为高吞吐、高并行设计
- **设计者**: Daniel J. Bernstein，2008
- **标准**: RFC 7539（ChaCha 与 Poly1305 认证码），2015 年 IETF
- **当前地位**: ✅ **推荐使用**。建议 AEAD 形式为 ChaCha20-Poly1305（认证加密）

ChaCha20 是为解决 RC4 弱点、注重 ARMv8 等现代平台指令集性能而设计的流密码。它比 RC4 抗密钥重用攻击，且具有可证明的安全边界。

## 2. 算法原理

ChaCha20 基于 **加法型** 流密码，核心是一个 16 × 32 位整数的初始状态：

```
state[0..3]  = 常量 "expa" "nd 32" "-by" "te k"
state[4..11] = 256-bit 密钥
state[12]    = 32-bit 计数器（小端）
state[13..15]= 96-bit nonce（小端）
```

### Quarter Round（单轮）

**QR(a,b,c,d)** 是基础运算，目的是快速混合 32 位字：

```
a += b;  d ^= a;  d = ROTL16(d);
c += d;  b ^= c;  b = ROTL12(b);
a += b;  d ^= a;  d = ROTL08(d);
c += d;  b ^= c;  b = ROTL07(b);
```

### 双轮（Double Round）

ChaCha20 做 **10 次双轮**（共 20 次 QR），每次包含：

- **列轮**：`(0,4,8,12) (1,5,9,13) (2,6,10,14) (3,7,11,15)`
- **对角轮**：`(0,5,10,15) (1,6,11,12) (2,7,8,13) (3,4,9,14)`

20 个 QR 完成后，每个 32 位字加上其初始值，再将 16 个字按小端序列化为 64 字节密钥流。然后计数器加 1，重复生成下一块。

### 加密/解密

加密与解密是同一操作：**密钥流 XOR 明文**。每次调用处理最多 64 字节密钥流块，计数器递增。

## 3. 算法流程

1. 构造初始状态（常量 + 32 字节 key + 计数器 + 12 字节 nonce）
2. 执行 **10 次双轮**，共 20 次 QuarterRound
3. 每个字加上初始值（`state[i] += init[i]`）
4. 以小端格式输出 64 字节密钥流
5. 后续每 64 个字节重复步骤 2–4，计数器自增

## 4. 安全性说明

- **密钥空间**: 2^256
- **Nonce 重用风险**: 若同一 key + nonce 被两次使用，攻击者可恢复密钥流
- **推荐用法**: 每次通信随机生成 96-bit nonce，并随消息发送明文
- **AES vs ChaCha20**: ChaCha20 在许多平台（尤其是 ARM 和 NEON）上更高效；AES 硬件加速很强大
- **最佳**: 建议用 ChaCha20-Poly1305 (AEAD)

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/chacha20/chacha20.c` + `chacha20.h` | `chacha20_crypt_oneshot(key,nonce,counter,msg,msglen,out)` |
| Python simple | `python/simple/chacha20.py` | `chacha20_crypt(key,nonce,counter,plaintext)` |
| Python stdlib | `python/stdlib/chacha20.py` | `cryptography` 包，fallback simple |
| Java | `java/.../simple/ChaCha20.java` | 标准流密码异或调用(纯手写) |

## 6. KAT 测试向量（RFC 7539 附录 A.1）

```text
Key   = 00 01 02 03 04 05 06 07 08 09 0a 0b 0c 0d 0e 0f
        10 11 12 13 14 15 16 17 18 19 1a 1b 1c 1d 1e 1f
Nonce = 00 00 00 09 00 00 4a 00 00 00 00 31
Counter = 1

Plain (114 bytes）= "Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it."

Cipher (hex) = 6E2E359A2568F98041BA0728DD0D6981E97E7AEC1D4360C20A27AFFCD9FAE0BF91B65C5524733AB8F593DAB62CD2BB0992704736F61E9C05D0B6BC3E36F29856F1342115E901F9EA852A430304AA46B564FB4F037468B5E5F3604342529252291873C57F3EE8D08B36E4E45B5C408
```

## 7. 参考

- RFC 7539 (ChaCha20 and Poly1305 for IETF Protocols)
- [Wikipedia: ChaCha20](https://en.wikipedia.org/wiki/Salsa20)

## 8. 最小可运行示例 (C)

```c
#include "chacha20.h"
#include <stdio.h>
#include <string.h>

int main(void) {
    const uint8_t key[32] = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F,0x10,0x11,0x12,0x13,0x14,0x15,0x16,0x17,0x18,0x19,0x1A,0x1B,0x1C,0x1D,0x1E,0x1F};
    const uint8_t nonce[12]= {0x00,0x00,0x00,0x09,0x00,0x00,0x4A,0x00,0x00,0x00,0x00,0x31};
    const uint32_t counter = 1;
    const uint8_t plain[] = "Ladies and Gentlemen of the class of '99: If I could offer you only one tip for the future, sunscreen would be it.";
    uint8_t out[256];
    chacha20_crypt_oneshot(key, nonce, counter, plain, sizeof(plain)-1, out);
    for (size_t i = 0; i < sizeof(plain)-1; i++) printf("%02X", out[i]);
    printf("\n");
    return 0;
}
```

Python:
```bash
python python/simple/chacha20.py
```
