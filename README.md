# Awesome-CryptographyAlgorithm

收集各种经典密码学算法的**多语言教学实现**,每个算法都提供 C / Python / Java 三种版本,
并尽量配以已知答案测试(KAT),方便对照与学习。

## 目录结构

```
.
├── c/                 # C 实现(每个算法一个目录,自带 Makefile)
│   ├── aes des md2 md5 rc4 sha1 sha256 base64 caesar vigenere
├── python/
│   ├── simple/        # 纯 Python 手写实现(教学目的)
│   ├── stdlib/        # 基于标准库(hashlib/base64)的封装
│   └── tests/         # Python 已知答案测试
├── java/              # Maven 工程 (com.zerone.crypto.simple)
└── Makefile           # 顶层构建/测试入口
```

## 已实现算法

| 算法 | C | Python (simple) | Python (stdlib) | Java |
|------|---|-----------------|-----------------|------|
| Caesar 凯撒密码   | ✅ | ✅ | ✅ | ✅ |
| Vigenère 维吉尼亚 | ✅ | ✅ | ✅ | ✅ |
| Base64            | ✅ | ✅ | ✅ | ✅ (Base64Cipher) |
| RC4 流密码        | ✅ | ✅ | ✅ | ✅ |
| MD5               | ✅ | ✅ | ✅ | ✅ |
| SHA-1             | ✅ | ✅ | ✅ | ✅ |
| SHA-256           | ✅ | ✅ | ✅ | ✅ |
| DES               | ✅ | ✅ | ✅ | ✅ |
| AES               | ✅ | ✅ | ✅ | ✅ |
| MD2               | ✅ | ✅ | - | ✅ |
| HMAC-SHA256       | ✅ | ✅ | ✅ | ✅ |

> `simple/` 为不依赖任何第三方库的手写实现,便于理解算法细节;`stdlib/` 演示标准库用法。

## 构建与运行

### C

```bash
# 顶层:构建 C demo、打包 Java、运行 Python 测试
make
# 运行全部自测(C KAT / Java JUnit / Python pytest)
make test

# 单个模块(以 caesar 为例)
cd c/caesar && make        # 构建 demo
make test                  # 运行单元测试
```

### Python

```bash
cd python/tests
python run_tests.py        # 运行所有 KAT 测试
```

各模块可直接作为脚本运行,例如:

```bash
python python/simple/caesar.py "Hello" 3
python python/simple/md5.py "hello"
```

### Java

```bash
cd java
mvn test                   # 运行 JUnit 已知答案测试
mvn exec:java -Dexec.mainClass=com.zerone.crypto.simple.Main  # 演示
```

## 说明

- 所有 `simple` 实现均以**可读性**为先,不保证性能,请勿用于生产环境。
- 分组密码(DES/AES)仅提供 ECB 模式演示,未做填充绕过/IV 处理。
- 仅用于教学与安全研究,严禁用于任何非法用途。
