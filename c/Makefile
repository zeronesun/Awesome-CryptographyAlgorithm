CC      ?= gcc
CFLAGS  ?= -std=c11 -O2 -Wall -Wextra
MODULES := aes des md2 md5 rc4 sha1 sha256 base64 caesar vigenere hmac

all $(MODULES):
	$(MAKE) -C $@ CC="$(CC)" CFLAGS="$(CFLAGS)"

.PHONY: all $(MODULES) clean test

clean:
	@for m in $(MODULES); do $(MAKE) -C $$m clean; done

test: $(MODULES)
	@for m in $(MODULES); do \
		echo "=== $$m ==="; \
		$(MAKE) -C $$m >/dev/null && (cd $$m && ./demo-test) || exit 1; \
	done
