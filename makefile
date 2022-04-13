CC = javac
DBG_FLAGS = -g

PRD= $(CC)
DBG= $(CC) $(DBG_FLAGS)
TST= java -ea jspec.cli.CLI

clean:
	find . -name \*.class -type f -delete

build: jspec/cli/CLI.java
	$(CC) jspec/cli/CLI.java

test: build
	$(TST)
