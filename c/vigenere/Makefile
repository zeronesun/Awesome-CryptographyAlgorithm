CC = gcc
CFLAGS = -Wall -Wextra -std=c11

TARGET = demo-test
SRC = $(wildcard *.c)

.PHONY: all demo-test clean

all: $(TARGET)

$(TARGET): $(SRC)
	$(CC) $(CFLAGS) -o $@ $(SRC)

clean:
	rm -f $(TARGET)
