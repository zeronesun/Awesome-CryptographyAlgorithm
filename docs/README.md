# Awesome-CryptographyAlgorithm · 算法文档

本目录为仓库 `Awesome-CryptographyAlgorithm` 的 **算法逐篇说明**。每个算法一个文档,覆盖:**原理 → 算法流程 → 安全性 → 多语言实现对照 → KAT 测试向量 → 参考来源**。

> `docs/algorithms` 目录下，每个算法一篇详解

## 阅读导航

每个算法文档采用统一章节结构,便于快速查阅:

```markdown
1. 概述                —— 类型(哈希/流密码/分组密码/古典/编码) + 一句话定位
2. 算法原理            —— 核心数学与变换,含必要公式
3. 算法流程            —— 分步骤伪码/文字流程
4. 安全性说明          —— 生产可用性判断
5. 多语言实现对照      —— C / Python / Java 文件路径与 API 对应
6. KAT 测试向量        —— 与仓库 C 实现完全一致的已知答案
7. 参考来源            —— RFC / NIST / 论文
8. 最小可运行示例      —— C/Python/Java 使用示例
```

## 算法一览

| 算法 | 类别 | 文档 | C(`c/`) | Python `simple/` | Python `stdlib/` | Java `simple` |
|---|---|---|---|---|---|---|
| Caesar 凯撒 | 古典 · 移位 | [algorithms/caesar.md](algorithms/caesar.md) | ✅ | ✅ | ✅ | ✅ |
| Vigenère 维吉尼亚 | 古典 · 多表 | [algorithms/vigenere.md](algorithms/vigenere.md) | ✅ | ✅ | ✅ | ✅ |
| Base64 | 编码 | [algorithms/base64.md](algorithms/base64.md) | ✅ | ✅ | ✅ | ✅ |
| RC4  | 流密码 | [algorithms/rc4.md](algorithms/rc4.md) | ✅ | ✅ | ✅ | ✅ |
| ChaCha20 (RFC 7539) | 流密码 · 现代推荐 | [algorithms/chacha20.md](algorithms/chacha20.md) | ✅ | ✅ | ✅ | ✅ |
| MD2   | 哈希 | [algorithms/md2.md](algorithms/md2.md) | ✅ | ✅ | ✅ | ✅ |
| MD5   | 哈希 | [algorithms/md5.md](algorithms/md5.md) | ✅ | ✅ | ✅ | ✅ |
| SHA-1 | 哈希 | [algorithms/sha1.md](algorithms/sha1.md) | ✅ | ✅ | ✅ | ✅ |
| SHA-256 | 哈希 | [algorithms/sha256.md](algorithms/sha256.md) | ✅ | ✅ | ✅ | ✅ |
| HMAC-SHA256 | 消息认证 | [algorithms/hmac.md](algorithms/hmac.md) | ✅ | ✅ | ✅ | ✅ |
| DES（含3DES）| 分组密码 · 64-bit | [algorithms/des.md](algorithms/des.md) | ✅ | ✅ | ✅ | ✅ |
| AES（128/192/256）| 分组密码 · 128-bit | [algorithms/aes.md](algorithms/aes.md) | ✅ | ✅ | ✅ | ✅ |

## 安全性分级

- ✅ **推荐**: AES, SHA-256, HMAC, ChaCha20(Poly1305 AEAD)
- ⚠️ **仅特定用途**: Base64 (编码,非加密)
- ❌ **禁止生产**: MD2/MD5/SHA-1/RC4/DES (严重弱点/已攻破)

## 复杂度速查

详见 [`complexity.md`](complexity.md) —— 每个算法的 O(n)/O(1) 复杂度表 + 易错点（字节序、填充、Nonce 重用等）。

## 使用提示

- C 实现: `cd c/<algo>/ && make && ./<target>`
- Python: `python python/simple/<algo>.py <args>`
- Java: `cd java && mvn test`
