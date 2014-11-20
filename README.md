## Part 1: MiniJava Type checker
### Procedure
1. Prepare JavaCC and JTB
2. Feed ``minijava.jj`` to JTB and JTB will produce ``jtb.out.jj``
3. Feed ``jtb.out.jj`` to JavaCC and JavaCC will produce some java files,
  and the only useful Java file is ``MiniJavaParser.java``
4. create main file ``Typecheck.java``.
5. In ``Typecheck.java``, ``Node root = new MinijavaParser(System.in).Goal()``
  should build up a syntax tree from standard input
6. The remaining task is to implement vistors to perform type checking.
  The root will accept two different vistors and finish type checking. According to the book, the type checking process consists of two phases
  1. build symbol table
  2. type check the statements and expressions

  So, at least two visitors are required. One will build up symbol table and the other one will do type checking with the help of symbol table.

### Details
* ``BuildSymboltable.java`` will build up symbol table and check type rules related to declation at the meantime.
* ``TypeCheckingVisitor.java`` will first check acyclic and overloading first using the symbol table. Then, it will start traversing the tree and check statements and expressions.

### Usage
Simply refer to the [release page](https://github.com/marklrh/MiniJava/releases) and download v2.0

## Part 2: MiniJava to Vapor translation
### Procedure
1. add more functionalities to ``SymbolTable``
2. build a class called ``QueryTable`` upon ``SymbolTable`` to provide some convenient APIs for visitor
3. implement ``IndentPrinter`` to provide convenient APIs for visitor to print vapor instructions
3. implement ``VaporVisitor`` which takes one ``SymbolTable`` and one ``QueryTable``
