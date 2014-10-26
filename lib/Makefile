JFLAGS = -g
JC = javac

sources = $(wildcard *.java)
classes = $(sources:.java=.class)

all: $(classes)

clean:
	rm -f *.class

%.class: %.java
	$(JC) $(JFLAGS) $<
