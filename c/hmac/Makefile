CC      ?= gcc
CFLAGS  ?= -std=c11 -O2 -Wall -Wextra
TARGET  = demo-test
# HMAC depends on the SHA-256 module; pull its source in explicitly.
SRC     = $(wildcard *.c) ../sha256/sha256.c
OBJS    = $(patsubst %.c,%.o,$(SRC))

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS)

.PHONY: all clean
all: $(TARGET)

clean:
	rm -f $(OBJS) $(TARGET)
