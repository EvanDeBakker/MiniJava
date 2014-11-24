.PHONY: build clean

build: clean
	javac J2V.java


testfc: build
	java J2V < ./lib_test/testland/Factorial.java

testm4: build
	java J2V < ./lib_test/testland/MoreThan4.java

testbt: build
	java J2V < ./lib_test/testland/BinaryTree.java

testbbs: build
	java J2V < ./lib_test/testland/BubbleSort.java

testls: build
	java J2V < ./lib_test/testland/LinearSearch.java

testll: build
	java J2V < ./lib_test/testland/LinkedList.java

testqs: build
	java J2V < ./lib_test/testland/QuickSort.java

testtv: build
	java J2V < ./lib_test/testland/TreeVisitor.java

# passed
clean:
	find . -name "*.class" -print0 | xargs -0 rm -rf
