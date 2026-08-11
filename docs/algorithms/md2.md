# MD2 哈希算法

## 1. 概述

- **类型**: 密码散列(哈希)函数,128-bit 输出
- **标准**: RFC 1319
- **定位**: 历史遗留,主要出现于 90 年代 X.509 / PKCS #1 v1.5 应用
- **生产安全性**: ❌ **已废弃**。现代系统默认禁用(OpenSSL 3.x 已不再默认支持 MD2)

MD2 是最早的 MD 系列消息摘要算法之一,输出 128-bit(16 字节),面向 8 位处理器设计。

## 2. 算法原理

MD2 将输入按 **16 字节块**处理,内部状态为 **48 字节**: 24 字节 checksum + 24 字节缓冲区。

主要步骤:
1. 将输入填充到 16 倍:补 `0x80` + 零,末尾 16 字节为长度
2. 迭代每 16 字节块,先进行校验和操作,再进行压缩
3. 输出 128-bit 摘要

## 3. 算法流程

1. 如果输入长度不是 16 的倍数,补 `0x80` + 零使其为 16 的倍数
2. 最后 16 字节存放原始消息 bit 长度(小端)
3. 为每个 16 字节块执行:
   - 更新 checksum (与各块异或 + MD2 S 盒)
   - 处理块进 buffer (与 checksum + S 盒混合)
4. 最终 16 字节的 checksum 作为 MD2 摘要输出

## 4. 安全性说明

- MD2 已被证明存在碰撞弱点,90 年代就已经不再被认为是安全哈希
- NIST 明确在 PKCS#1 中弃用 MD2
- **结论**: 只作历史研究;新系统禁止使用

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/md2/md2.c` + `md2.h` | `md2(data,len,digest)` |
| Python simple | `python/simple/md2.py` | `md2(data)` 返回 bytes |
| Python stdlib | `python/stdlib/md2.py` | 优先 `hashlib.new('MD2')`, fallback simple |

## 6. KAT 测试向量 (RFC 1319)

```text
MD2("")        = 8350e5a3e24c153df2275c9f80692773
MD2("abc")     = da853c0a3412973069834e5304d97045
MD2("message digest")  = ab4f496bafb2a530575f3ed05218708e
```

## 7. 参考

- RFC 1319 (MD2 Message-Digest Algorithm)

## 8. 最小可运行示例

```c
#include "md2.h"
#include <stdio.h>
int main(void) {
    const char *msg = "abc";
    BYTE digest[16];
    md2((const BYTE*)msg, strlen(msg), digest);
    for(int i=0;i<16;i++) printf("%02x",digest[i]);
    printf("\n");
    return 0;
}
```

Python:
```bash
python python/simple/md2.py "abc"
```
