# Base64 编码

## 1. 概述

- **类型**: 二进制到文本的 **编码** (Encoding),并非加密算法
- **用途**: 在只支持文本的传输层(Email/JSON/URL/HTTP 头)中安全表示二进制数据
- **安全**: ⚠️ 不含任何保密性。任何人都可解码还原原始字节

Base64 将每 3 个字节(24 位)映射为 4 个可打印 ASCII 字符,是网络中最常见的编码方式。

## 2. 算法原理

使用 64 字符表:
```
A-Z a-z 0-9 + /
```

**编码**:把原始字节流按 3 字节分组,每组 24 位再切分为 4 个 6 位段,每段索引查表得一个字符。若最后一组只有 1/2 字节,补 `=` 填充。

**解码**:反向查表,丢弃 `=` 填充,恢复原始字节。

## 3. 算法流程

1. 编码:输入按 3 字节分组 → 每 24 位拆为 4 个 6 位单元 → 查 Base64 表 → 根据不足 bytes 补 1/2 个 `=`
2. 解码:剔除等号 → 字符反向查表 → 4 个 6 位单元拼成 3 字节输出

## 4. 安全性说明

- Base64 是编解码,不提供任何机密性
- 常作为承载层(如 JWT payload),需由上层决定 confidentiality

## 5. 多语言实现对照

| 语言 | 文件 | 主要 API |
|---|---|---|
| C | `c/base64/base64.c` + `base64.h` | `base64_encode(src,out,size)`, `base64_decode(src)` |
| Python simple | `python/simple/base64.py` | `base64_encode(data)`, `base64_decode(code)` |
| Python stdlib | `python/stdlib/base64.py` | `base64` 标准库封装 |

## 6. KAT 测试向量 (RFC 4648)

```text
""       =
"f"      "Zg==="
"fo"     "Zm8="
"foo"    "Zm9v"
"foob"   "Zm9vYg=="
"fooba"  "Zm9vYmE="
"foobar" "Zm9vYmFy"
```

## 7. 参考

- RFC 4648 (The Base16, Base32, and Base64 Data Encodings)

## 8. 最小可运行示例

```c
#include "base64.h"
#include <stdio.h>
int main(void) {
    const char *src = "Hello!";
    char out[128];
    base64_encode(src, out, sizeof(out));
    printf("%s\n", out);
    return 0;
}
```

Python:
```bash
python python/simple/base64.py "Hello!"
```
