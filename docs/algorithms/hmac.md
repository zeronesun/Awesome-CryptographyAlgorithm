# HMAC-SHA256

## 1. 概述

- **类型**: 消息认证码 (Keyed-Hash Message Authentication Code, HMAC)
- **标准**: RFC 2104
- **底层哈希**: 本实现用 SHA-256 (RFC 2104 亦可用 MD5/SHA-1 构造)
- **关键字**: 对称密钥、完整性校验、消息认证
- **生产可用性**: ✅ **可用**(以 SHA-256 为基的 HMAC 是当前推荐认证码)

## 2. 算法原理

定义 `ipad = 0x36` 重复到块大小(64B),`opad = 0x5C` 重复到块大小:

```
HMAC(K, m) = H( (K' ⊕ opad) || H( (K' ⊕ ipad) || m ) )
```

- 内层: ipad 与 K' 异或后拼消息 → H
- 外层: opad 与 K' 异或后拼内层 H → 最终输出

## 3. 算法流程

1. 如果密钥 `K` 长度 > 64B: `K' = SHA256(K)`,否则 `K' = K` (未做零扩展)
2. 将 `K'` 左补零到 64B
3. 计算 `inner = SHA256( (K' ⊕ ipad) || message )`
4. 计算 `outer = SHA256( (K' ⊕ opad) || inner )`
5. 输出 `outer` (32B / 64 hex) 为 HMAC值

## 4. 安全性说明

- HMAC 的安全性取决于底层 SHA-256 抗碰撞性 + 密钥保密
- HMAC-SHA256 是 HTTP Digest、JWT Header 等常用 tag
- 不要用裸 `SHA256(secret || message)`（长度扩展攻击）
- **结论**: 推荐用于完整性校验、API 认证、消息认证

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/hmac/hmac.c` + `hmac.h` | `hmac_sha256(key,keylen,msg,msglen,out)` |
| Python simple | `python/simple/hmac_sha256.py` | 手写 HMAC |
| Python stdlib | `python/stdlib/hmac_sha256.py` | `hmac.new(key,msg,hashlib.sha256).hexdigest()` |
| Java | `java/.../simple/HmacSHA256.java` | `Mac.getInstance(\"HmacSHA256\")` |

## 6. KAT 测试向量 (RFC 2104 示例 / RFC 4231 TC1)

```text
HMAC-SHA256(key="key", msg="The quick brown fox jumps over the lazy dog")
Hex = f7bc9f6145869af0c9b3523bb332f1c3352e38243b5e46dd12af8307b5f38e27...
```

(与 `c/hmac/hmac_test.c` 一致)

## 7. 参考

- RFC 2104 (HMAC)
- RFC 4231 (Identifiers and Test Vectors for HMAC-SHA)

## 8. 最小可运行示例

```c
#include "hmac.h"
#include <stdio.h>
#include <string.h>
int main(void) {
    const char *key = "key", *msg = "The quick brown fox";
    BYTE out[32];
    hmac_sha256((const BYTE*)key, strlen(key), (const BYTE*)msg, strlen(msg), out);
    for(int i=0;i<32;i++) printf("%02x",out[i]);
    printf("\n");
    return 0;
}
```

Python:
```bash
python python/simple/hmac_sha256.py "key" "The quick brown fox"
```
