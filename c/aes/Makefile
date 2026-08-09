CC      ?= gcc
CFLAGS  ?= -std=c11 -O2 -Wall -Wextra
TARGET  = demo-test
SRC     = $(wildcard *.c)
OBJS    = $(patsubst %.c,%.o,$(SRC))

$(TARGET): $(OBJS)
	$(CC) $(CFLAGS) -o $@ $(OBJS)

.PHONY: all clean
all: $(TARGET)

clean:
	rm -f $(OBJS) $(TARGET)
