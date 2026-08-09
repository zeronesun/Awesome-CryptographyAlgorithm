# C 各模块统一构建 / 测试入口
# 约定: 每个模块目录 <mod>/ 含 <mod>.c(库) 与 <mod>_test.c(含 main),
#       子 Makefile 链接二者生成 demo-test 并运行 KAT 自检。

CC      ?= cc
CFLAGS  ?= -std=c99 -O2 -Wall -Wextra

MODULES := aes des md2 md5 rc4 sha1 sha256 base64 caesar vigenere

.PHONY: all test clean $(MODULES)

all: $(MODULES)

$(MODULES):
	$(MAKE) -C $@

test: $(MODULES)
	@for m in $(MODULES); do \
		echo "=== $$m ==="; \
		$(MAKE) -C $$m >/dev/null && (cd $$m && ./demo-test) || exit 1; \
	done

clean:
	@for m in $(MODULES); do $(MAKE) -C $$m clean; done
