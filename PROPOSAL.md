JSpec testing library
===

A final project by Andrew Chang-DeWitt for CSCI 24000 at IUPUI, Spring 2022.

_**Note**_: See a maintained version of this proposal document [on GitHub at andrew-chang-dewitt/jspec/blob/main/PROPOSAL.md](https://github.com/andrew-chang-dewitt/jspec/blob/main/PROPOSAL.md) for the most up to date version.

A simple unit testing library for TDD & a cli for running tests.

- **Intended User:** Java developers to write & evaluate unit tests.
- **Problem solved:** Unit test writing & evaluation.
- **Technologies needed:** CLI commands, file i/0, & output to stdout



Use Case Analysis
---

Features belong to one of three categories: definition, running, & discovery.

Defining features here via User stories. In this case, a User is defined as the person writing & running tests.

### Test definition

Encompasses features that are used to write, group, set up, & tear down tests.

- A User defines a group of tests as methods belonging to a class.
- A User describes a group of tests by giving a string description as the `desc` property on the class.
- A User describes a test method by declaring it with a descriptive name, beginning with the prefix `test`.
- A User can nest groups of tests by declaring a nested class.
- A User can define code that needs to be run once before executing a group of tests by defining a method called `before`.
- A User can define code that needs to be run before each test in a group of tests by defining a method called `beforeEach`.
- A User can define code that needs to be run once after executing a group of tests by defining a method called `after`.
- A User can define code that needs to be run after each test in a group of tests by defining a method called `afterEach`.

#### _**Stretch goal:**_ Improved assertions

I'd like to have some features that are used to manage assertions.
It'd be nice to keep them simple (using basic `assert` keyword & assoc. syntax), but grant some nice features around the error messaging on failure.
Also would be nice to have some ability to have multiple assertions in one test and still check all when a preceding one fails.

Any sort of chaining of assertions or execution of multiple assertions will require an assertions library or some sort of preprocessor macro, e.g.:


```java
// … using multiple assertions

// plain assert statements with preprocessor
assert true : “this will pass”;
assert false : “this will fail”;
assert true : “this will pass after the failure”;

// or assertion library with methods
// something like
new Expect(1).to.eq(1);
new Expect(2).to.eq(1);
new Expect(1
).to.eq(1);

// either one should output should indicate the test failed, with a count of “sub-tests” or assertions indicating 2/3 passed
```

At the end of the day, either of those examples will require some sort of “sub test” feature. Implementing any sort of assertions library (the second example) will probably be a later, separate project.

### Test running

Encompasses features for executing tests, evaluating for success or failure, & communicating that state to the user.

- A User can run a test class (& any nested classes) manually by defining a main() method that creates an instance of itself & calls the instance's run() method.
- A User receives a concise indicator of test result status after running a test class with a '.' to indicate a successful test & an 'F' to indicate a failure.
- A User receives a count of successful tests out of total tests at the end of the report.
- A User receives details about any failed test, including the test class name, test method name, failure reason (exception name & message), & any stack trace.
- A User can opt to receive a verbose output of test result status, consisting of the test class name, followed by a list of test method names & "Success" or "Failure", depending on the result.

### Test discovery

Encompasses features for recursively finding all tests in a given directory & running them.

- A User can use a cli command (e.g. `jspec test` or `maven test`) have jspec recursively traverse a given (could default to PWD or project) directory & subdirectories & discover any files ending in `Spec.java`, then run the tests for all files found.
- A User can give a glob pattern to use when searching for spec files, replacing the default `Spec.java` pattern.
- A User can specify a single test file to run.



Data design
---

- _What data is your program really about?_

  In this case, the 'data' in question is code: a test is simply code used to set up & declare a fact, then evaluate if it is true or not.
  So to that end, data starts as code, then is transformed to a 'Success' or 'Failure' statement by evaluating the test's assertions.

- _What is the best way to represent that data? (database, object, arrays)_

  The data will be represented as Objects defined by the User & inherited from an Object provided by this library.
  Each Object will contain tests defined as methods on the Object.

- _Will the data need to be persistent? How will you make that happen?_

  The only data persistence will be the tests defined by the User in `*.java` files. For the most simple use case, there won't be any File I/O, as the User will set up the test Object to be compiled & run using the Java compiler & runtime.
  In more complex use cases, the User may use a CLI provided by the library that will utilize File I/O APIs to "discover" tests & then evaluate them.

- _Will the data need to be aggregated into a larger structure?_

  In the simple use case, the User will handle aggregation by defining their own `Runner` Object with its own `main()` method that adds `Group`s to the `Runner` before evaluating the `Group`s.
  When using the CLI, the program will need to aggregate `Group`s into a `Runner` that is then used to evaluate the tests.
  In both cases, the `Runner` will provide two options for adding `Group`s: via the `Runner()` constructor during initialization, or by calling the `Runner.addGroup()` method after initialization.
  Internally, the `Runner` will aggregate `Group`s in an array or vector.
  During test evaluation, the collection will be traversed & the `Group`s will be inspected using `java.lang.reflect` APIs to discover all test methods & any nested `Group`s. As tests & test groups are discovered they could either be evaluated immediately, or have references to them stored in a tree that will later be traversed for the actual evaluation of each test.



UI Design
---

The main portion of the UI is the text output of test results.
This is mainly broken down into 4 parts:

1. A progress indicator (using `.` & `F` to represent a test's completion & result)
2. A failure result (detailed output regarding a failed test, incl. stack trace, error type/message, test-writer messaging, maybe even code snippets?)
3. Verbose status output (Using Group's descriptions & test descriptions to generate output as a series of nested lists)
4. Summary (a count of all tests, a count of passed tests)

Sample output for each part below.

### Progress indicator

```
.....F....F..F.....
```

### Failure result

```
================================================================================
❌ FAILURE: Some group name/description: Some test name/description
================================================================================

SomeErrorType: An error message

Possible detailed error messaging
can go here, e.g.
1 != 0
or 
  Line same
+ Line differentA
- Line differentB
  Line same
  Line same

--------------------------------------------------------------------------------

Captured stdout: 

Some captured output from System.out.* calls
More output

--------------------------------------------------------------------------------

Stack trace:

Stack trace output follows last
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass

================================================================================

```

### Verbose status report

Assume 4 Groups, A, B, C, & D.
C is nested in B, & B is nested in A; D is unrelated.

```
Group A
  Some test description ✅
  Another test description ❌
  testATestMethodName ✅
  testSomeOtherTest ❌

    Group B
      Some test description ✅
      Another test description ❌

      Group C
        Some test description ✅
        testSomeOtherTest ✅

Group D
  Some test description ✅
  Another test description ✅
  testATestMethodName ✅
  testSomeOtherTest ✅

```

### Summary

Some failure(s):

```
9/15 Passed
```

No failures:

```
15/15 Passed! 
```



Algorithm
---

The program is strictly Object-Oriented with all code organized by classes. 
A UML diagram is provided here, with more details about each class below it.

![UML diagram](./uml.svg)

**OOP Relationships:**

- A User's test definition class inherits from Group
- A User's test runner class inherits from Runner
- One Group aggregates zero to many Group
- One Runner aggregates one to many Group
- One TestResult inherits from ResultABC
- One GroupResult inherits from ResultABC
- One Node<? extends ResultABC> composes one child of ResultAbC
- One Node\<T\> composes zero to many Node\<T\>
- One Tree\<T\> composes zero to many Node\<T\>
- One Runner aggregates one Tree\<ResultAbC\>
- One CLI aggregates one Runner
- One Crawler aggregates one to many Group
- One CLI composes one Crawler
- One CLI composes one ArgParser

_**Stretch Goal:**_

There might be room to abstract the tree behavior of Runner/Group & Tree/Node into an interface that each pairing implements.
Both will structure their Nodes slightly differently though:

1. Group discovers child node Groups by calling Class.getDeclaredClasses(), which returns an array of more Group classes.
  Each sibling Group doesn't have a reference to its other siblings.
2. Node discovers child node Nodes by accessing the head or tail Child reference, then iterating using the next or previous Sibling references.

This difference leads to any "abstraction" of the tree behavior being mostly limited to a traversal method, but it will have to be at least partially virtual, as both have some differences in their traversal methods.


### Group

A User creates a `Group` of tests by defining a new class that inherits from this class.
Nested groups are created by defining inner classes (that inherit from `Group`) inside a child of `Group`.
Most User stories belonging to the _**Test Definition**_ group above will be built as features on `Group`.

_**Stretch goal:**_

There might be subclasses of `Group` that add some sort of functionality that changes a typical `Group`'s behavior, i.e. asynchronous test execution.

#### Properties

- _protected static String **description**_: a property used to give a better description to a test group, defaults to null if not implemented in a child class 

#### Methods

- _**public static main(String[] args) -> void**_

  Implemented to allow for a very simple use case where a User only writes a test group as a child of Group, then compiles & runs it without having to set up a Runner or use a CLI.

  1. Creates a Runner instance with an instance of this group passed to it.
  2. Calls the runner's `run()` method to execute the tests.

- _**public visit() -> IGroupVisitResult**_

  1. Initialize empty ArrayList\<TestResult\>
  2. Get all methods on this instance of Group using `this.getClass().getDeclaredMethods()`
  3. Call `this.before()`
  4. Iterate over methods:
    1. If method begins with "test"
      1. Call `this.beforeEach()`
      2. pass to `this.evaluateTest()` & add returned Result to ArrayList
      3. Call `this.afterEach()`
    2. Else do nothing with it
  5. Call `this.after()`
  6. Get all inner classes on this instance of Group using `this.getClass().getDeclaredClasses()` & filter by those that are an instance of `Group`
  7. Return new `IGroupVisitResult` containing the lsit of test results & the list of child `Group`s

- _**private evaluateTest(Method test) -> TestResult**_

  1. Get test method's declared name using `test.getName()`
  2. Create new `Result` with test method's declared name
  3. Get descriptive name using `this.getDescName()` with the declared name
  4. If descriptive name is not null, add to `Result` with method `describe()`
  5. Try to invoke the test method, then mark `Result` as passed with method `pass()` & return the passed `Result`
    1. Catch any InvocationTargetException or IllegalAccessException & add to `Result` by passing the exception to it's `fail()` method
    2. Return the failed result

- Virtual functions:

  The following methods are defined as virtual functions that are all a no-op if not defined in a child class.
  Each is a method that will be called a different stage of test execution: once before _any_ tests are called, once before _each_ test method is called, once after _each_ test is called, & finally once after _all_ the tests are called.
  A child class can implement any or all of these to customize some behavior needed for all tests or perform some set-up or tear-down actions.

  - _**public before() -> void**_
  - _**public beforeEach() -> void**_
  - _**public afterEach() -> void**_
  - _**public after() -> void**_


### IGroupVisitResult

A simple interface to structure the return type of `Group.visit()`

#### Properties

- _ArrayList\<TestResult\> **testResults**_: the results of tests belonging to the Group that was visited
- _Group[] **children**_: An array of child Groups that have yet to be processed


### Runner

A User creates an instance of this to run any test `Group`s passed to it on creation, or by adding them to the `Runner` instance using the `addGroup()` method.

```java
import org.jspec.Runner;

class Runner extends Runner {
  public static void main(String[] args) {
    // Add test groups during Runner initialization
    Runner r = new Runner(GroupA, GroupB);

    // Add a group using Runner.addGroup()
    r.addGroup(GroupB);

    // run the tests in all groups given
    r.run();
  }
}
```

The CLI will work by auto-discovering test `Group`s in a directory & subdirectory, then creating a new `Runner` instance & giving all the found `Group`s to the new `Runner`.
Most likely, the User will never need to implement this class, but it's exposed if they would like to run their tests programmatically.

#### Properties

- _private ArrayList\<Group\> **groups**_: stores the groups to be evaluated by this `Runner`
- _private Tree\<ResultABC\> **results**_: stores the results after the groups are evaluated

#### Methods

- _**public Runner(Group ... groups)**_

  1. Iterate over arg, passing each one to `this.groups.add()`

- _**public addGroup(Group group) -> Runner**_

  1. Add to `this.groups` using `ArrayList::add()`.

- _**public run() -> Tree\<ResultABC\>**_

  1. Create an empty `Tree` of `GroupResult`s
  2. Iterate over `this.groups`, calling `this.processGroup` for each one & adding the resulting `Node<GroupResult>` to the `Tree` as a root using `Tree::addRoot`
  3. Return the `Tree`

- _**private processGroup(Group group) -> Node\<GroupResult\>**_
    1. Create a new `Node<GroupResult>` called `newNode` with the `group`'s declared name
    2. Add the `Group`'s descriptive name by getting `Group.description` & passing it to `GroupResult.describe()`
    3. Call the `visit()` method on the group
    4. Iterate over the `testResults` property of the returned object, doing the following for each one:
      1. Call `this.buildTestResultNode()`
      2. Pass the `Node<GroupResult>` as the parent node & the previous test result as the previous sibling node (if it exists) & if the current result is the last one of the list as `isLastChild`
    5. If returned object's `children` property is not null, iterate over it, calling `this.processGroup` on each one, adding the returned node as a child to `newNode` using `Node::addTailChild`
    6. Else, return the `newNode`

- _**private buildTestResultNode(
\    TestResult result,
\    Node\<GroupResult\> parent, 
\    Node\<TestResult\> prevSibling,
\    bool isLastChild,
  ) -> Node\<TestResult\>**_

  1. Create a new `Node<TestResult>` giving `result` as the value
  2. Add `parent` using `Node::addParent()`
  3. If `prevSibling` is not null
    1. Add `prevSibling` using `Node::addPrevSibling`
    2. Add new node as the next Sibling for the previous node using `Node::nextSibling`
  4. Else add new node as `headChild` to parent node using `Node::addHeadChild()`
  5. If `isLastChild` is true, add new node as `tailChild` to parent node using `Node::addTailChild()`
  6. Return new node

- _**public renderResults(OutputStream outStream) -> void**_

  1. Create an empty ArrayList\<String\>
  2. Call the `preorder()` method on `this.results`, giving it a lambda callback that: 
    1. Call the `toString()` method of each node
    2. Prefix it with a string spaces with a length equal to 2 multiplied by the depth of the node
    3. Add the prefixed string to the ArrayList created at the beginning
  3. Iterate over the ArrayList, passing each String to `System.out.println()`



### ResultABC

An abstract base class for defining a result value to store in a tree.
Defining shared properties & one shared method, all used to render the tree of values.

#### Properties

- _private String **codeName**_: the name of the method or class in the test code that this result is from
- _private String **descName**_: a more descriptive name given to this class or test method, may be null

#### Methods

- _**public toString() -> String[]**_

  Virtual method in ABC.
  Defined on ABC to guarantee existence, but doesn't implement it.
  
  1. Raise `NotImplementedError` if this version is called.

- _**protected getText() -> String[]**_

  1. If `this.descName` is not null, return it
  2. Else return `this.codeName`


### TestResult extends ResultABC

A concrete child of ResultABC.
Uses Builder Pattern for constructing an instance.

#### Properties

- _bool **pass**_:
- _Exception **exc**_:

#### Methods

- _**public TestResult(String codeName)**_

  1. Give arg to `this.codeName`

- _**public describe(String description) -> TestResult**_
  
  1. Give arg to `this.descName`
  2. Return `this`

- _**public pass() -> TestResult**_

  1. Set `this.pass` to `true`
  2. Return `this`

- _**public fail(Exception exc) -> TestResult**_

  1. Set `this.pass` to `false`
  2. Give arg to `this.exc`
  3. Return `this`

- _**public toString() -> String[]**_

  1. Get line text using `this.getText`, then append with check or x if passed or failed


### GroupResult extends ResultABC

A concrete child of ResultABC.

#### Methods

- _**public toString() -> String[]**_

  1. Get line text using `this.getText`


### Node\<T\>

Encapsulates logic for building a tree of nodes.

#### Properties

- _private T **value**_: the actual value object
- _private Node\<T\> **parent**_: a reference to the parent of this node, will be null if this is a root node
- _private Node\<T\> **headChild**_: a reference to one end of the list of child nodes (child nodes are represented as a doubly linked list), will be null if this is a leaf node
- _private Node\<T\> **tailChild**_: a reference to the other end of the list of child nodes (child nodes are represented as a doubly linked list), will be null if this is a leaf node
- _private Node\<T\> **nextSibling**_: a reference to the next sibling of this node (siblings a linked list), will be null if this is the tail child
- _private Node\<T\> **prevSibling**_: a reference to the previous sibling of this node (siblings a linked list), will be null if this is the head child

#### Methods

- _**public Node\<T\>(T value)**_

  1. Assign `value` to `this.value`


- Builder pattern:

  These methods all implement a Builder Pattern for creating new nodes.
  First the creator calls the constructor with the desired value, then sets any parent, child, or sibling nodes using the appropriate method below.
  These method calls can be chained, as they all return the instance of `Node<T>` after adding the given parent/child/sibling node.

  - _**public addParent(Node\<T\> node) -> Node\<T\>**_
  - _**public addHeadChild(Node\<T\> node) -> Node\<T\>**_
  - _**public addTailChild(Node\<T\> node) -> Node\<T\>**_
  - _**public addNextSibling(Node\<T\> node) -> Node\<T\>**_
  - _**public addPrevSibling(Node\<T\> node) -> Node\<T\>**_

- Getters:

  These methods are all simple getters for the associated private property.

  - _**public getParent() -> Node\<T\>**_
  - _**public getHeadChild() -> Node\<T\>**_
  - _**public getTailChild() -> Node\<T\>**_
  - _**public getNextSibling() -> Node\<T\>**_
  - _**public getPrevSibling() -> Node\<T\>**_

- Setters: There is currently no use case where setters will be needed as the Results Tree is build & never modified. 


### Tree\<T\>

Encapsulates logic for traversing a tree & a reference to the root node.
In this use case, there's a likely possibility that there's multiple trees, but its simpler to store them as though they are one tree with multiple root nodes.
This implementation stores the multiple root nodes as a linked list with references to the head root & tail root.

#### Properties

- _private Node\<T\> **headRoot**_
- _private Node\<T\> **tailRoot**_

#### Methods

- _**public Tree\<T\>() -> Tree\<T\>**_

- _**public preorder(FnICallback cb) -> void**_

- _**public addNode(
\    Node\<T\> node,
\    Node\<T\> parent,
\    Node\<T\> headChild,
\    Node\<T\> tailChild,
\    Node\<T\> nextSibling,
\    Node\<T\> prevSibling
\  ) -> Tree\<T\>**_

_**Note:**_ While there could be methods implemented for inserting nodes in existing tree structures ()or removing nodes), there's no use case yet for this functionality so it'd be a waste of time to build them at this time.


### Crawler

Encapsulates logic for crawling the project file tree for test definition files & getting the defined `Group` descendants from them.


### ArgParser

Encapsulates logic for parsing Strings as arguments.

#### Properties

- _private java.io.File? **outfile**_: a file to output results to, if given
- _private java.nio.file.PathMatcher **pattern**_: a search pattern to use when looking for test definition files, defaults to "./\*\*/\*Spec.java"
- _private bool **verbose**_: will render verbose output, if true; defaults to false

#### Methods

- _**public ArgParser()**_
  
  No-argument constructor—inherited from Object.
  Instances are to be build using Builder Pattern (e.g. `CLI cli = CLI().some_method().maybe_another_method()`).

- _**public arg(String argument) -> ArgParser**_

  Add an argument to the parser.
  
  1. Check if argument starts with one dash or two.
    - if one, dispatch to `this.shortFlag()`
    - else if two, dispatch to `this.longFlag()`
    - else dispatch to `this.addPattern()`

- _**private shortFlag(String arg) -> CLI**_

  Handle parsing & adding short flags.

  1. Remove single dash from front of string (i.e. "-text" becomes "text").
  2. Split string on "=" character; assign first part to `flag` & second to `value`.
  3. Switch on `flag` with `EShortFlag` enum
    - case "o": dispatch `value` to `this.addOutFile()` 
    - case "p": dispatch `value` to `this.addPattern()` 
    - case "v": dispatch `true` to `this.addVerbose()` 

- _**private longFlag(String arg) -> CLI**_

  Handle parsing & adding long flags.

  1. Remove double dash from front of string (i.e. "--text" becomes "text").
  2. Split string on "=" character; assign first part to `flag` & second to `value`.
  3. Switch on `flag` with `EShortFlag` enum
    - case "outFile": dispatch `value` to `this.addOutFile()` 
    - case "pattern": dispatch `value` to `this.addPattern()` 
    - case "verbose": dispatch `true` to `this.addVerbose()` 

- _**private addOutFile(String pathname) -> CLI**_

  1. Create new `File` from given `pathname` & give to `this.outFile`

- _**private addPattern(String glob) -> CLI**_

  1. Prepend given glob with syntax identifier, "glob:" & give to `prepended`
  2. Try to create a new PathMatcher object using prepended glob `FileSystems.getDefault().getPathMatcher(prepended)` and give to `this.pattern`
    - catch `java.util.regex.PatternSyntaxException`: throw `BadPattern`

- _**private addVerbose(bool arg) -> CLI**_

  1. Set `this.verbose` equal to given value


### CLI

Main entry point for the command line interface program.
Encapsulates logic to receive commands & arguments, then dispatches commands & composes results accordingly.

#### Properties

- _ArgParser **args**_
- _Crawler **crawler**_
- _Runner **runner**_

#### Methods

_**public static main(String[] args) -> void**_

  1. Initialize an instance of `ArgParser` & give it to `this.args`
  2. Iterate over `args` array, giving each one to `this.args` to be processed using `ArgParser.arg()`
    - if `UnknownArg` or `BadPattern` is thrown, exit program & display error to user
  3. Get the PWD from `System.getProperty("user.dir")`
  4. Initialize an instance of `Crawler` with the PWD & the pattern from `this.args.getPattern()` & give the new instance to `this.crawler`
  5. Discover test groups using `this.crawler`'s method `Crawler.crawl()`
    - if `NoTestsFound` is thrown, exit program & display error to user
  6. Initialize an instance of `Runner` & give it to `this.runner`
  7. Give each `Group` found by `this.crawler` to `this.runner` using `Crawler.getGroups()` & iterating over the result, calling `Runner.addGroup()` on each `Group`
  8. Run the tests using `Runner.run()`
  9. Render the results to the user



240 Concepts Used
---

**OOP:**

- Inheritence
- Encapsulation
- Polymorphism
- Abstraction

**Data structures:**

- ArrayList
- Linked Lists
- Trees

**Algorithms:**

- preorder tree traversal
- _**stretch goal:**_ some sort of sorting algo (for sorting tests/results?)




© Andrew Chang-DeWitt 2022
