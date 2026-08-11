# DES / 3DES

## 1. 概述

- **类型**: 对称**分组密码**，分组与密钥均为 64-bit（有效密钥仅 56-bit，另 8 bit 为校验）
- **标准**: FIPS 46-3，由 IBM 在 1970s 基于 Lucifer 设计
- **生产安全**: ❌ **已废弃**。56-bit 密钥可被现代设备暴力破解
- **本仓库实现**: `c/des/des.c` 提供 DES 与 3DES（三重 DES）

## 2. 算法原理

DES 是 **Feistel 网络**结构:
- 分组 64-bit，密钥 64-bit（经 PC-1 去掉校验位后有效 56-bit）
- 初始置换 IP + 末尾置换 FP
- 16 轮 Feistel: 每轮右半部分经 **f 函数**（扩展 E + S 盒 + 置换 P）与 48-bit 子密钥异或，再与左半部分交换
- 密钥调度: 从 56-bit 主密钥经 PC-1 / 循环移位 / PC-2 导出 16 个 48-bit 子密钥

**3DES（三重 DES）**

```
C = E_K3( D_K2( E_K1( P ) ) )
```

通常 `K1=K3` (EDE) 提供有效 112-bit 安全。

## 3. 算法流程（单 DES）

1. 初始 IP 置换，拆成 L/R
2. 16 轮:
```
L_i = R_{i-1}
R_i = L_{i-1} ⊕ f(R_{i-1}, K_i)
```
3. 合并，FP 置换得密文

## 4. 安全性说明

- **暴力破解**: 56-bit 密钥空间约 7.2×10^16，现代 GPU/FPGA 可在小时级穷举
- **已知弱点**: 互补性、弱密钥、线性/差分密码分析（理论）
- DES 已被 AES 取代；3DES 仅用于向后兼容，NIST 已宣布逐步停用
- **结论**: 只作密码学教学用途；新系统一律用 AES

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/des/des.c` + `des.h` | `des_encrypt(key,pt,ct)`, `des_decrypt(key,ct,pt)`, `des3_encrypt` |
| Python simple | `python/simple/des.py` | 手写 DES |
| Python stdlib | — | 标准库无 DES,未提供 |
| Java | `java/.../simple/DES.java` | 手写或 JCE `Cipher.getInstance(\"DES/ECB/NoPadding\")` |

## 6. KAT 测试向量 (FIPS 46-3)

```text
key = 01 23 45 67 89 AB CD EF
pt  = 01 23 45 67 89 AB CD EF
ct  = 85 E8 13 54 0F 0A B4 05
```

## 7. 参考

- FIPS 46-3 (Data Encryption Standard)
- [Wikipedia: Data Encryption Standard](https://en.wikipedia.org/wiki/Data_Encryption_Standard)

## 8. 最小可运行示例

```c
#include "des.h"
#include <stdio.h>
int main(void) {
    BYTE key[8]  = {0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF};
    BYTE pt[8]   = {0x01,0x23,0x45,0x67,0x89,0xAB,0xCD,0xEF};
    BYTE ct[8];
    des_encrypt(key, pt, ct);
    for(int i=0;i<8;i++) printf("%02X",ct[i]);
    printf("\n");
    return 0;
}
```

Python:
```bash
python python/simple/des.py
```
