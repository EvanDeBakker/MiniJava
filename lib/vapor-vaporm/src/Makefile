.PHONY: build clean tarball

JC=javac
JFLAGS=-g
MainJavaFile=V2VM.java
MainClass=V2VM
ParserJar=./vapor-parser.jar
JavaVm=java

build:
	$(JC) $(JFLAGS) *.java \
	-cp $(ParserJar)

clean:
	rm -f *.class

testfac: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/sample_programs/Factorial/Factorial.vapor

testbt: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/sample_programs/BinaryTree/BinaryTree.vapor

testll: build
	$(JavaVm) \
	-classpath .:$(ParserJar) \
	$(MainClass) -v \
	< ../lib_test/sample_programs/LinkedList/LinkedList.vapor


tarball:
	mkdir -p hw4
	cp *.java ./hw4
	tar -cvzf hw4.tgz ./hw4
