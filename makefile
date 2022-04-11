CC = javac
DBG_FLAGS = -g

PRD= $(CC)
DBG= $(CC) $(DBG_FLAGS)
TST= java -ea

clean:
	find . -name \*.class -type f -delete

test: testAll
	$(TST) jspec.TestAll

testAll: jspec/TestAll.java
	$(DBG) jspec/TestAll.java
