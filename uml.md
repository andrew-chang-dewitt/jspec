UML
===

Group
---

The main class a User will interact with.
An abstract class, intended to be inherited & have some methods (`before`, `beforeEach`, `afterEach`, & `after`) overwritten as needed.
The other classes will interact with this one to provide functionality for the User.

```
# String? description // overwrite this to give a more descriptive string to display in results
---
+ static void main(String[] args) // initiate an instance of Runner and pass it an instance of this group
+ void before() // no-op by default, override to “enable”
+ void beforeEach()
+ void afterEach()
+ void after()
+ Group[]? visit(ResultsTree results) // used by Runner evaluate tests in a Group and for traversing nested Groups
```

Runner
---

Aggregates `Group`s & provides methods for running the tests on them then aggregating the results into a tree & rendering them

```
- Group[] groups
- ResultsTree results
---
+ Runner(…Group group) // constructor accdepts an indeterminate number of groups
+ void run() // traverse each Group in this.groups recursively using group.visit() and saving results to a tree on Runner
+ void renderResults(OutputStream outputTarget) // render results to given stream
```

ResultsTree
---

Organizes test results by test method & Group.
Provides methods for building the tree & for traversing it.

```
- ResultsNode* leftRoot // a reference to the first Node of the tree, name accepts the possibility of multiple roots as a linked list
---
+ ResultsTree(ResultsNode* firstNode)
+ void preorder(FnICallback cb) // preorder traversal, executing given visit function on every node
+ void addNode(ResultsNode* newNode, ResultsNode*? parent, ResultsNode*? headChild, ResultsNode*? tailChild, ResultsNode*? nextSibling, ResultsNode*? previousSibling)
```

FnICallback
---

A functional interface defining a method signature.
Used by `ResultsTree` to describe type signature of function given to its `preorder()` method.
This function is called on each node during traversal of the tree.
By describing the callback as a functional interface, the callback can actually be given as an anonymous lambda expression.

```
---
+ void fn(ResultsNode* node)
```

ResultsNode
---

Implements a Node as a member of a Tree (by having 0..1 parent and 0..many children, and as a doubly linked list by having 0..1 previous Node and 0..1 next Node, as well as a head Node and tail Node

```
- String codeName
- String? descName
- bool test // if true, pass needs set, indicates if Node represents Group or test method
- bool? pass // if false, exc should be set, indicates if test passed or failed
- Exception? exc // indicates what exception was thrown in test failure
- String? stdout // can store captured output to stdout
- ResultsNode* parent
- ResultsNode* headChild
- ResultsNode* tailChild
- ResultsNode* nextSibling
- ResultsNode* previousSibling
---
+ ResultsNode(Method method, String stdout, bool pass = true, Exception exc = null) // Method constructor
+ ResultsNode(Group group) // Group constructor
+ String toString(bool verbose)
+ ResultsNode* getParent()
+ void setParent(ResultsNode* value)
+ ResultsNode* getHeadChild()
+ void setHeadChild(ResultsNode* value)
+ ResultsNode* getTailChild()
+ void setTailChild(ResultsNode* value)
+ ResultsNode* getNextSibling()
+ void setNextSibling(ResultsNode* value)
+ ResultsNode* getPreviousSibling()
+ void setPreviousSibling(ResultsNode* value)
```

CLI
---

The most likely entry-point for a User when running tests.
Usage message:

```
$ jspec --help

Usage: jspec [flags]
       (to execute all tests found in current directory or it's children)
  or   jspec [flags] pattern
       (to find & evaluate tests in any file matching the given glob pattern)
  or   jspec [flags] path
       (to find & evaluate tests in a given path—including any sub-paths)

Flags:
  TODO: Not sure about the -m flag
  -m, --module [name]     execute all tests found in a given fully qualified
                          package or module name
  -o, --output [file]     direct output to given file (stdout by default)
  -p, --pattern [glob]    execute all tests found in any files matching
                          a given glob pattern
  -v, --verbose           enable verbose output (off by default)

```

UML:

```
---
+ static void main(String[] args)  // entrypoint driver—parses args into flags & patterns, then initiates test discovery before passing all found Groups to a Runner for evaluation & rendering
- Groups[] discoverGroups(java.nio.file.PathMatcher? pattern) throws NoTestsFound // discovers root groups using PathMatcher & walkFileTree and pushes found Groups to list to return
- void evaluateTests(Groups[] groups, File? outFile) // passes groups to Runner, evaluates w/ Runner.run() & renders results to stdout or outFile using Runner.renderResults()
```

NoTestsFound
---

A custom Exception to be thrown when no tests are found for the default or given pattern.

```
+ NoTestsFoundString (String pattern) // builds message: "No tests found matching ${pattern}."
```

ICliOpts
---

An interface defining allowable CLI option arguments & value types.

```
+ bool verbose
+ File? outFile
+ java.nio.file.PathMatcher? pattern // refer to this source for more on using PathMatcher https://javapapers.com/java/glob-with-java-nio/
---
+ ICliOpts(String[] args) throws UnknownArg // parses an array of strings for allowable args; throws exception on unknown arguments
```

UnknownArg extends Exception
---

A simple custom Exception to be thrown when a User gives an invalid argument.

```
+UnknownArg(String arg) // Exception w/ message "Unknown argument: ${ arg }", intended to result in printing of error message, then usage message
```
