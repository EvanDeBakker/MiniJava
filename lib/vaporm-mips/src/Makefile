.PHONY: build clean tarball

JC=javac
JFLAGS=-g
MainJavaFile=VM2M.java
MainClass=VM2M
ParserJar=./vapor-parser.jar
JavaVm=java

build:
	$(JC) $(JFLAGS) \
	-classpath .:$(ParserJar) \
	*.java

clean:
	rm -f *.class

testfac: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/Factorial/Factorial.vaporm

testbt: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/BinaryTree/BinaryTree.vaporm

testbtop: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/BinaryTree/BinaryTree.opt.vaporm

testbs: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/BubbleSort/BubbleSort.vaporm


testll: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/LinkedList/LinkedList.vaporm


tarball:
	mkdir -p hw5
	cp *.java ./hw5
	tar -cvzf hw5.tgz ./hw5
