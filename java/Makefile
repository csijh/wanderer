Hatchery State Queue Direction Grid Cell Level:
	javac -d ../bin model/$@.java
	java -cp ../bin model.$@

Game Display Score Table Help Ticker Controller GUI:
	javac -Xlint:unchecked -d ../bin view/$@.java
	java -cp ../bin view.$@

.PHONY: Maze Wanderer
Item Maze:
	javac -d ../bin maze/$@.java
	java -cp ../bin maze.$@

Baby Balloon Boulder Dead Entity LeftArrow Monster Player RightArrow \
Space Test Thing Wanderer:
	javac -d ../bin wanderer/$@.java
	java -cp ../bin wanderer.$@

clean:
	rm -f bin/*/*.class

# If the Depend program from https://github.com/csijh/java-depend is
# available, use it to display dependencies in each package
depend:
	@java Depend ../bin/model
	@echo
	@java Depend view
	@echo
	@java Depend maze
	@echo
	@java Depend wanderer

jar:
	rm -f wanderer.jar
	jar -cfe wanderer.jar wanderer.Wanderer -C bin .
