# AES

## 1. 概述

- **类型**: 对称**分组密码**，分组 128-bit
- **密钥长度**: 128 / 192 / 256 bit
- **标准**: FIPS 197（Rijndael，Belgium 作者 Vincent Rijmen 与 Joan Daemen）
- **生产可用性**: ✅ **当前标准**，得到广泛部署

AES 是当今最广泛使用的分组密码，用于 TLS、磁盘加密、无线(WPA2/3)、区块链、文件加密等。支持 AES-128 / AES-192 / AES-256（本仓库 `c/aes/aes.c` 支持三种，ECB 模式）。

## 2. 算法原理

AES 是 **Substitution-Permutation Network (SPN)**，以 **state（4×4 字节矩阵）** 为处理单元。

**轮数**
| 密钥长度 | 轮数 |
|---|---|
| 128-bit | 10 |
| 192-bit | 12 |
| 256-bit | 14 |

**每轮四个变换**
1. `SubBytes`：用一个固定 **S-box**（GF(2^8) 逆元 + 仿射变换）替换每个字节
2. `ShiftRows`：state 每行循环左移（行0不移、行1移1、行2移2、行3移3）
3. `MixColumns`：每列与固定矩阵在 GF(2^8) 上做列混合
4. `AddRoundKey`：state 与轮密钥异或

最后一轮省略 `MixColumns`。

**密钥扩展（Key Schedule）**：从主密钥通过 `RotWord / SubWord / Rcon` 生成每轮 128-bit 轮密钥。

## 3. 算法流程

1. 密钥扩展，生成 `Nr+1` 组轮密钥
2. 初始 `AddRoundKey`
3. 前 `Nr-1` 轮：`SubBytes → ShiftRows → MixColumns → AddRoundKey`
4. 最后一轮：`SubBytes → ShiftRows → AddRoundKey`
5. 输出 state → 密文

**解密**：对上述四步使用逆变换（`InvSubBytes / InvShiftRows / InvMixColumns`），轮密钥顺序相反。

## 4. 安全性说明

- AES 是当前**最可信**的分组密码，NIST 标准化，无公开实际破解
- 本仓库仅 **ECB 模式**（教学）；实际使用必须是 **CBC / GCM / CTR** 等安全模式，且 CBC 需随机 IV
- ECB 模式会泄露同样的明文块 → 相同的密文块（如画像轮廓暴露），生产禁用
- 密钥长度：128-bit 足够安全，推荐 256-bit

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/aes/aes.c` + `aes.h` | `aes_key_setup(key, schedule, bits)` / `aes_encrypt` / `aes_decrypt` |
| Python simple | `python/simple/aes.py` | 手写 AES (ECB) |
| Python stdlib | — | 标准库无 AES，未提供 |
| Java | `java/.../simple/AES.java` | 手写或 JCE `Cipher.getInstance("AES/ECB/NoPadding")` |

## 6. KAT 测试向量 (FIPS 197 附录 A `suite`)

**AES-128（ECB）**
```text
key  = 000102030405060708090a0b0c0d0e0f
plain= 00112233445566778899aabbccddeeff
cipher=69c4e0d86a7b0430d8cdb78070b4c55a
```

**AES-256（ECB）**
```text
key(32B) = 603deb1015ca71be2b73aef0857d7781
           1f352c073b6108d72d9810a30914dff4
plain    = 00112233445566778899aabbccddeeff
cipher   = 8ea2b7ca516745bfeafc49904b496089
```

## 7. 参考

- FIPS 197 (Advanced Encryption Standard)
- [Wikipedia: Advanced Encryption Standard](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)

## 8. 最小可运行示例

```c
#include "aes.h"
#include <stdio.h>
#include <string.h>
int main(void) {
    BYTE key[16] = {0x00,0x01,0x02,0x03,0x04,0x05,0x06,0x07,0x08,0x09,0x0A,0x0B,0x0C,0x0D,0x0E,0x0F};
    BYTE pt[16] = {0x00,0x11,0x22,0x33,0x44,0x55,0x66,0x77,0x88,0x99,0xAA,0xBB,0xCC,0xDD,0xEE,0xFF};
    BYTE ct[16];
    aes_encrypt(key, pt, ct, 128); // AES-128 ECB
    for(int i=0;i<16;i++) printf("%02X",ct[i]);
    printf("\n");
    return 0;
}
```

Python (使用 simple 实现):
```bash
python python/simple/aes.py
```
