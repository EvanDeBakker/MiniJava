.PHONY: build clean

build: clean
	javac J2V.java

testbt: build
	java J2V < ./lib_test/testland/BinaryTree.java

testbbs: build
	java J2V < ./lib_test/testland/BubbleSort.java

testls: build
	java J2V < ./lib_test/testland/LinearSearch.java

clean:
	find . -name "*.class" -print0 | xargs -0 rm -rf
