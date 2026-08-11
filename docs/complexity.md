# 算法复杂度与易错点 (Complexity Pitfalls)

> 快速参考 / 代码审查备注。本仓库实现以可读性优先,复杂度为大O不计常数。

---

## 哈希算法(Message Digest)

| 算法 | 分组 | 输出 | 时间复杂度 | 空间复杂度 | 生产可用性 |
|------|------|------|---|---|---|
| MD2   | (无固定分组) | 128-bit | O(n) | O(48+16) | ❌ 已废弃 |
| MD5   | 512-bit | 128-bit | O(n) | O(64) | ❌ 已碰撞 |
| SHA-1 | 512-bit | 160-bit | O(n) | O(80) | ❌ SHAttered |
| SHA-256 | 512-bit | 256-bit | O(n) | O(64) | ✅ 推荐单项 |

> 注: 所有 MD 家族均基于 Merkle–Damgård 构造,先 MD padding 再迭代压缩。

## 古典密码

| 算法 | 时间 | 空间 | 密钥 | 适用性 |
|------|------|------|------|---|
| Caesar 凯撒 | O(n) | O(1) | 25 | ❌ 教学仅 |
| Vigenère 维吉尼亚 | O(n + k) | O(k) | 周期长度 k | ❌ 可 Kasiski 攻破 |

## 流密码

| 算法 | 时间 | 空间 | 密钥 | 生产可用性 |
|------|------|------|------|---|
| RC4 | O(n) | O(256) | 40–2048 | ❌ 已淘汰(WEP 弱点) |
| ChaCha20 (RFC 7539) | O(n)每块 | 常量 state 16×int32 | 256-bit | ✅ 推荐流密码(结合 Poly1305 为 AEAD) |

## 分组密码

| 算法 | 块长 | 密钥 | 轮数 | 安全性 |
|------|----|----|----|----|
| DES | 64-bit | 56-bit（有效） | 16 | ❌ 56-bit 暴力可行 |
| 3DES (EDE) | 64 | 112/168 | 48 | ⚠️ 向后兼容,继续停用 |
| AES-128 | 128 | 128 | 10 | ✅ |
| AES-192 | 128 | 192 | 12 | ✅ |
| AES-256 | 128 | 256 | 14 | ✅ |

---

## 易错点(代码审查关注项)

### 1. 字节序(Endian)

- **问题**: C 实现 RFC 字节(如初始状态/常量/轮密钥)使用**小端**序列化, часто写错
- 修复: Python 用 `int.from_bytes(...,'little')` 和 `struct.pack('<...')`
- Java: 使用 `ByteBuffer.order(ByteOrder.LITTLE_ENDIAN)`

**错误示例**:
```c
// 错: 直接把 uint32 当 big-endian
uint32_t x = 0x01020304;
uint8_t out[4] = { (x >> 24), (x>>16), (x>>8), x };
// 得到 01 02 03 04,而非 RFC 期望的 04 03 02 01
```

### 2. MD Padding

- **问题**: MD2/MD5/SHA-1/SHA-256 均需标准填充:先补 `0x80`,再补零到长度 ≡ 448 mod 512,最后64-bit存原始消息 bit 长(大端)
- 修复: 严格遵守 MD padding 规范;忘记计数会导致错误摘要

### 3. Nonce 重用(ChaCha20/RC4)

- **问题**: 同一 (key,nonce) 组合使用两次 → 攻击者易 XOR 恢复明文。
- 修复: 生产系统必须每次通信随机生成 96-bit nonce,或用计数器严格管理。

### 4. 模式选择(AES)

- **问题**: ECB 模式泄露相同明文块(图像轮廓等),不可用于加密多于 1 块的数据。
- 修复: 使用 CBC/GCM/CTR。CBC 需要随机 IV(不机密但需不可预测);GCM 提供 AEAD。

### 5. 错误的哈希用法(长度扩展)

- **问题**: `SHA-256(secret || message)` 系统易受长度扩展攻击。
- 修复: 正确使用 HMAC-SHA256(或 secret 作 HMAC key)。

### 6. Python 标准库兼容

- **问题**: `hashlib.new("MD2")` 在 OpenSSL 3.x 默认禁用;`chacha20` 不内置标准库
- 修复: `stdlib/md2.py` 做 try 风险处理并给出显式报错;`stdlib/chacha20.py 尝试 cryptography, fallback simple`

---

## 参考

- RFC 7539: ChaCha20 and Poly1305 for IETF Protocols
- NIST SP 800-131A: Transitioning from SHA-1 及密码学生命周期
- [Cryptographic Right Answers (Colin Percival)](https://www.daemonology.net/blog/2009-06-11-cryptographic-right-answers.html)
