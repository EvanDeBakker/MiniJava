.PHONY: build clean tarball

JC=javac
JFLAGS=-g
SOURCE_FILES=V2VM.java

build:
	$(JC) $(JFLAGS) *.java -classpath ./vapor-parser.jar

clean:
	rm -f *.class

testfac: build
	java V2VM < ../lib_test/sample_programs/Factorial/Factorial.vapor \
	-classpath ./vapor-parser.jar

tarball:
	mkdir -p hw4
	cp $(SOURCE_FILES) ./hw4
	tar -cvzf hw4.tgz ./hw4
