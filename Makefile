Cell Direction Grid Level Lifecycle Queue State Variable:
	javac -sourcepath src -d bin src/model/$@.java
	java -cp bin model.$@

Controller Display Help Score Table Ticker:
	javac -Xlint:unchecked -sourcepath src -d bin src/view/$@.java
	java -cp bin view.$@

Item Maze:
	javac -sourcepath src -d bin src/maze/$@.java
	java -cp bin maze.$@

Baby Balloon Boulder Dead Entity LeftArrow Monster Player RightArrow \
Space Test Thing Wanderer:
	javac -sourcepath src -d bin src/wanderer/$@.java
	java -cp bin wanderer.$@

clean:
	rm -f bin/*/*.class

# If the Depend program from https://github.com/csijh/java-depend is
# available, use it to display dependencies in each package
depend:
	@java Depend bin/model
	@echo
	@java Depend bin/view
	@echo
	@java Depend bin/maze
	@echo
	@java Depend bin/wanderer

jar:
	rm -f wanderer.jar
	jar -cfe wanderer.jar wanderer.Wanderer -C bin .
