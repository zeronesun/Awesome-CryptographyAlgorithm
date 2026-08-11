# SHA-1

## 1. 概述

- **类型**: 密码散列(哈希)函数,160-bit 输出
- **标准**: FIPS 180-1(1995),NIST
- **生产安全性**: ❌ **官方已弃用**(已发现碰撞)。禁止在新系统中使用

SHA-1 输出 160-bit(20 字节),历史上广泛用于 TLS 证书、Git 对象哈希、旧系统校验。因 2017 年 Google/CMU 公布实际碰撞(SHAttered),NIST 已将其弃用。

## 2. 算法原理

基于 **Merkle–Damgård** 结构:
- 分组: 512-bit(64 字节)
- 输出: 160-bit(5 × 32-bit 状态字)
- 80 轮,每轮一个常数 `K_t`

**初始状态(H0..H4)**
```
67452301  efcdab89  98badcfe  10325476  c3d2e1f0
```

**每 512-bit 块处理**
1. 块扩展为 80 个 32-bit 字 `W[t]`:
   - `W[0..15] =` 块的大端 16 个 32-bit 字
   - `W[t] = rotl1(W[t-3] ^ W[t-8] ^ W[t-14] ^ W[t-16])`
2. 载入状态 `a,b,c,d,e` 和 `H0..H4`
3. 80 轮:
```
Round 0-19 :  f = (b&c)|(~b&d);        K = 5A827999
Round 20-39:  f = b^c^d;             K = 6ED9EBA1
Round 40-59:  f = (b&c)|(b&d)|(c&d); K = 8F1BBCDC
Round 60-79:  f = b^c^d;             K = CA62C1D6

temp = rotl5(a) + f + e + K + W[t]
e = d; d = c; c = rotl30(b); b = a; a = temp
```
4. `H0+=a, H1+=b, ..., H4+=e`

**输出**: `H0..H4` 大端拼接 → 160-bit / 40 hex

## 3. 算法流程

1. **填充**:补 `0x80` + 零使长度 ≡ 448 mod 512,末尾 64-bit 存消息 bit 长度
2. 初始化 5 个 32-bit 状态 `H0..H4`
3. 每 512-bit 块:复制到 `W[0..15]` → 扩展为 80 个 `W[t]` → 80 轮压缩 → 累加
4. 输出 `H0..H4` 大端拼接为 40 hex

## 4. 安全性说明

- 2017 年 Google 与 CWI 发布 **SHAttered**,首次公开构造完整 SHA-1 碰撞
- NIST SP 800-131A:禁止 SHA-1 用于数字签名、证书、口令存储
- Git 仍用 SHA-1 做对象寻址(内容完整性,非防对抗篡改),正推进 SHA-256 迁移
- **结论**: 仅教学与历史兼容;新代码一律使用 SHA-256/SHA-3

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/sha1/sha1.c` + `sha1.h` | `sha1_init` / `sha1_update` / `sha1_final` |
| Python simple | `python/simple/sha1.py` | 手写,返回 hex |
| Python stdlib | `python/stdlib/sha1.py` | `hashlib.sha1(data).hexdigest()` |

## 6. KAT 测试向量 (FIPS 180-1 / NIST)

```text
SHA1("")       = da39a3ee5e6b4b0d3255bfef95601890afd80709
SHA1("abc")    = a9993e364706816aba3e25717850c26c9cd0d89d
SHA1("abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq")
                = 84983e441c3bd26ebaae4aa1f95129e5e54670f1
SHA1(1,000,000 × "a") = 34aa973cd4c4daa4f61eeb2bdbad27316534016f
```

## 7. 参考

- FIPS 180-1 (Secure Hash Standard)
- [Wikipedia: SHA-1](https://en.wikipedia.org/wiki/SHA-1)
- [SHAttered: Collision for full SHA-1 (2017)](https://shattered.io)

## 8. 最小可运行示例

```c
#include "sha1.h"
#include <stdio.h>
int main(void) {
    const char *msg = "abc";
    BYTE digest[20];
    SHA1_CTX ctx;
    sha1_init(&ctx);
    sha1_update(&ctx, (const BYTE*)msg, strlen(msg));
    sha1_final(&ctx, digest);
    for(int i=0;i<20;i++) printf("%02x",digest[i]);
    printf("\n");
    return 0;
}
```

Python:
```bash
python python/simple/sha1.py "abc"
```
