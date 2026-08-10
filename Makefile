# Awesome-CryptographyAlgorithm 顶层调度
#
# 用法:
#   make             构建 C demo / 打包 Java / 运行 Python 测试
#   make test        运行全部自测(C KAT / Java JUnit / Python pytest)
#   make c|java|python   单独处理某一语言
#   make clean       清理各语言构建产物

.PHONY: all test c java python clean

all: c java python

c:
	$(MAKE) -C c

java:
	cd java && mvn -q -DskipTests package

python:
	cd python && python -m pytest tests/ -q

test: test-c test-java test-python

test-c:
	$(MAKE) -C c test

test-java:
	cd java && mvn test

test-python:
	cd python && python -m pytest tests/ -q

clean:
	$(MAKE) -C c clean
	cd java && mvn -q clean
	cd python && rm -rf .pytest_cache __pycache__ tests/__pycache__
