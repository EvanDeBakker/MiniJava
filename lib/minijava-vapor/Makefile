
.PHONY: build clean
SOURCE_FILES=Arguments.java BuildSymbolTableVisitor.java Clazz.java\
DataSegment.java IndentationPrinter.java J2V.java Meth.java QueryTable.java\
Quit.java Result.java SymbolTable.java VaporVisitor.java Var.java

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

tar: clean
	mkdir -p hw3
	cp $(SOURCE_FILES) ./hw3
	tar -cvzf hw3.tgz ./hw3

# passed
clean:
	find . -name "*.class" -type f -delete
	rm -rf lib_test/hw3-tester/Output
	rm -rf hw3 hw3.tgz 
