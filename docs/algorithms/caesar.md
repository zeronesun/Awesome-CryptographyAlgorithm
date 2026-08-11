# CAESAR 凯撒密码

## 1. 概述

- **类型**: 古典替换密码 (Classical Substitution Cipher)
- **用途**: 历史教学,理解最基础的字母移位加密
- **安全**: ❌ 极不安全,任何现代环境下均不可用于保护数据

凯撒密码是最古老的加密算法,据传由尤利乌斯·凯撒使用。它将明文中的每个字母按照字母表**固定位数**平移得到密文。

## 2. 算法原理

对字母 `p`,加密位移为 `k`(密钥):

```
加密:  c = (p + k) mod 26
解密:  p = (c - k) mod 26
```

字母表按 `A-Z`(或 `a-z`)排列,`mod 26` 保证移位循环回到表头。

**核心弱点**: 密钥空间仅 26 种可能,暴力枚举即可破解;且保留明文统计特征,频率分析可秒破。

## 3. 算法流程

1. 取密钥 `k`(1~25 的整数)
2. **加密**: 遍历明文每个字母,计算 `(ord(c) + k) mod 26` 得到对应密文字母
3. **解密**: 遍历密文,计算 `(ord(c) - k) mod 26` 还原
4. 非字母(数字/标点)在本教学实现中处理策略:保留或转为空格(视版本而定)

## 4. 安全性说明

- 密钥空间 `= 26`,暴露一次即全破
- 完全不抵抗**唯密文攻击**与**频率分析**
- **结论**: 只能作教材示例,**禁止**用于任何实际加密

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/caesar/caesar.c` + `caesar.h` | `caesar_encode(plain, cipher, key)`, `caesar_decode(cipher, plain, key)` |
| Python simple | `python/simple/caesar.py` | `caesar_encode(plain, key)`, `caesar_decode(cipher, key)` |

## 6. KAT 测试向量

以下与 `c/caesar/caesar_test.c` 实测完全一致:

| 明文 | 密钥 | 密文 |
|---|---|---|
| `HELLO` | 3 | `KHOOR` |
| `hello` | 3 | `khoor` |
| `xyz` | 1 | `yza` |
| `Attack at dawn!` | 5 | `Fyyfhp fy ifbs!` |
| `` | 7 | `` |

## 7. 参考

- [Wikipedia: Caesar cipher](https://en.wikipedia.org/wiki/Caesar_cipher)

## 8. 最小可运行示例

```c
#include "caesar.h"
#include <stdio.h>
int main(void) {
    char plain[] = "HELLO WORLD";
    char enc[64], dec[64];
    caesar_encode(plain, enc, 3);
    caesar_decode(enc, dec, 3);
    printf("enc=%s dec=%s\n", enc, dec);
    return 0;
}
```

Python 直接运行:
```bash
python python/simple/caesar.py "HELLO WORLD" 3
```

