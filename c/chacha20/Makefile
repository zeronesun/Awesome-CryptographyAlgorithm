CC ?= gcc
CFLAGS ?= -std=c11 -O2 -Wall -Wextra
TARGET = chacha20_test
SRC = $(wildcard *.c)
OBJS = $(patsubst %.c,%.o,$(SRC))

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS)

all: $(TARGET)

clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean
