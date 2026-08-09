SRC	=$(wildcard *.c)
OBJS	=$(patsubst %.c,%.o,$(SRC))
CC	= gcc
TARGET  = demo-test

$(TARGET):$(OBJS)
	$(CC) -o $(TARGET) $(OBJS)

.PHONY:clean
clean:
	rm -rf  *.o $(TARGET)