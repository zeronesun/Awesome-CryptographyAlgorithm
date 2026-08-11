# MD5

## 1. 概述

- **类型**: 密码散列(哈希)函数,128-bit 输出
- **标准**: RFC 1321(1992),作者 Ronald Rivest
- **生产安全性**: ❌ **已破解**。广泛存在碰撞,禁止在安全敏感场景使用

MD5 是应用最广泛但也是被攻破最彻底的消息摘要算法之一,历史上用于文件校验、密码存储(早期)。

## 2. 算法原理

基于 **Merkle–Damgård** 结构:
- 分组大小: 512 bit(64 字节)
- 输出: 128 bit(4 × 32-bit 寄存器 `A,B,C,D`)
- 初始值(IV):
```
A = 0x67452301
B = 0xefcdab89
C = 0x98badcfe
D = 0x10325476
```

每 512-bit 块经 64 轮处理,每轮使用四个非线性函数 F/G/H/I。具体见 RFC 1321。

## 3. 算法流程

1. 将消息长度 bit 补到 ≡ 448 mod 512(即在末字节后接 0x80 + 零)
2. 追加 64-bit 原始长度(小端)
3. 将消息分成 512-bit 分组
4. 对每个分组:
   - 将分组复制为 16 × 32-bit 字 W[0..15]
   - 使用 MD5 轮常数 + 差分运算更新 A,B,C,D
   - 将本轮 A,B,C,D 累加到全局 A,B,C,D
5. 输出 A,B,C,D 串联(小端)→ 128-bit

## 4. 安全性说明

- 2004 年已被证明**碰撞**可在短时间计算,不再可用于防篡改/签名
- **结论**: 仅用于非密码学用途(如去重校验);禁止用于安全场景

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/md5/md5.c` + `md5.h` | `md5_init` / `md5_update` / `md5_final` |
| Python simple | `python/simple/md5.py` | `md5(data)` |
| Python stdlib | `python/stdlib/md5.py` | `hashlib.md5(data).hexdigest()` |

## 6. KAT 测试向量 (RFC 1321)

```text
MD5("")        = d41d8cd98f00b204e9800998ecf8427e
MD5("abc")     = 900150983cd24fb0d6963f7d28e17f72
MD5("message digest")  = f96b697d7cb7938d525a2f31aaf161d0
```

## 7. 参考

- RFC 1321 (MD5 Message-Digest Algorithm)

## 8. 最小可运行示例

```c
#include "md5.h"
#include <stdio.h>
int main(void) {
    const char *msg = "abc";
    BYTE digest[16];
    md5((const BYTE*)msg, strlen(msg), digest);
    for(int i=0;i<16;i++) printf("%02x",digest[i]);
    printf("\n");
    return 0;
}
```

Python:
```bash
python python/simple/md5.py "abc"
```
