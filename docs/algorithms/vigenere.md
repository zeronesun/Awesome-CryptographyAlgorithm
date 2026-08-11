# Vigenère 维吉尼亚密码

## 1. 概述

- **类型**: 古典多表替换 (Polyalphabetic substitution)
- **用途**: 教学,了解如何用密钥字消除单字母频率特征
- **安全**: ❌ 仅对短密钥/短文有混淆,现代环境下不可靠

维吉尼亚密码是对凯撒密码的推广:不再使用单一固定位移,而是用**密钥字**中每个字母决定每一位的位移。曾被称为 "le chiffre indéchiffrable" (不可破的密码)。

## 2. 算法原理

给定密钥字 `K = K_1 K_2 ... K_n` (每个 `K_i` 是 `A-Z`),对明文的第 `i` 个字母 `p_i`:

```
加密: c_i = (p_i + K_i) mod 26
解密: p_i = (c_i - K_i) mod 26
```

密钥长度 `n` 循环使用: `K_i = K_{i mod n}`。

## 3. 算法流程

1. 加载密钥字并移除非字母字符(仅保留 A-Z)
2. **加密**: 对明文的每个字母,用同位置密钥字母偏移,大写/小写分开处理
3. **解密**: 对密文逐字母减去密钥字母偏移,取模 26
4. 非字母字符原样保留

## 4. 安全性说明

- 密钥空间与密钥字长度相关,短密钥易受频率分析
- Kasiski 攻击可破解密钥长度;重复密钥与长文会暴露
- **结论**: 教学目的;现代系统不再使用

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/vigenere/vigenere.c` + `vigenere.h` | `vigenere_encode(plain,cipher,key)`, `vigenere_decode(cipher,plain,key)` |
| Python simple | `python/simple/vigenere.py` | `vigenere_encode(plain,key)`, `vigenere_decode(cipher,key)` |

## 6. KAT 测试向量

| 密钥 | 明文 | 密文 |
|---|---|---|
| `LEMON` | `ATTACKATDAWN` | `LXFOPVEFRNHR` |
| `key` | `hello` | `rijvs` |
| `lemon` | `attack at dawn` | `lxf...` |

## 7. 参考

- [Wikipedia: Vigenère cipher](https://en.wikipedia.org/wiki/Vigen%C3%A8re_cipher)

## 8. 最小可运行示例

```c
#include "vigenere.h"
#include <stdio.h>
int main(void) {
    char src[] = "ATTACKATDAWN";
    char enc[64], dec[64];
    vigenere_encode(src, enc, "LEMON");
    vigenere_decode(enc, dec, "LEMON");
    printf("enc=%s dec=%s\n", enc, dec);
    return 0;
}
```

Python:
```bash
python python/simple/vigenere.py "ATTACKATDAWN" "LEMON"
```
