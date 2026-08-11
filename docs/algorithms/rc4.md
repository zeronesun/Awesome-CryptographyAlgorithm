# RC4（Rivest Cipher 4）流密码

## 1. 概述

- **类型**: 对称**流密码** (Stream Cipher)
- **设计者**: Ronald Rivest，1987（最早由 RSA Security 内部使用）
- **安全**: ❌ 已被严重攻破，**禁止**在新系统使用

RC4 是一种基于密钥调度算法（KSA）和伪随机生成算法（PRGA）的流密码。历史上广泛用于 SSL/TLS 和 WEP/WPA，但因多种致命弱点已被淘汰。

## 2. 算法原理

RC4 由两个阶段组成:

**a) 密钥调度算法 KSA（初始化 S 盒）**
```
S[0..255] = 0..255
j = 0
for i in 0..255:
    j = (j + S[i] + key[i mod keylen]) mod 256
    swap(S[i], S[j])
```

**b) 伪随机生成算法 PRGA（生成每字节密钥流）**
```
i = j = 0
for each output byte:
    i = (i + 1) mod 256
    j = (j + S[i]) mod 256
    swap(S[i], S[j])
    output_k = S[(S[i] + S[j]) mod 256]
```

## 3. 算法流程

1. 输入密钥（40–2048 bit）,通过 KSA 初始化 256 字节的 S 盒
2. PRGA 生成与明文等长的密钥流
3. 将密钥流与明文异或得到密文;解密时再异或得到明文

## 4. 安全性说明

- IV 重用危害极大（已知 Keystream 导致明文恢复）
- RC4 存在亮度弱点(80/256)和循环还原弱点
- **结论**: 仅作历史教学;新系统请用 ChaCha20 或 AES-CTR

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/rc4/rc4.c` + `rc4.h` | `rc4_crypt(key,keylen,msg,msglen,out)` |
| Python simple | `python/simple/rc4.py` | `rc4(key,msg)` |
| Python stdlib | `python/stdlib/rc4.py` | 回退到 simple(无 stdlib) |

## 6. KAT 测试向量 (与网络或 OpenSSL 一致)

```text
key = 0x0102030405
msg = "Hello"
cipher(以十六进制表示) = [根据实现计算]
```

## 7. 参考

- [Wikipedia: RC4](https://en.wikipedia.org/wiki/RC4)

## 8. 最小可运行示例

```c
#include "rc4.h"
#include <stdio.h>
#include <string.h>
int main(void) {
    const char *key = "Key";
    const char *src = "Plaintext";
    BYTE out[64];
    rc4_crypt((const BYTE*)key, strlen(key), (const BYTE*)src, strlen(src), out);
    printf("rc4: %s\n", out);
    return 0;
}
```

Python:
```bash
python python/simple/rc4.py "Key" "Plaintext"
```
