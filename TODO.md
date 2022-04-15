Symbols:

- x: completed
- -: partially complete
- /: in progress


List:

- [x] A User defines a group of tests as methods belonging to a class.
- [x] A User describes a group of tests by giving a string description as the `desc` property on the class.
- [x] A User describes a test method by declaring it with a descriptive name, beginning with the prefix `test`.
- [x] A User can nest groups of tests by declaring a nested class.
- [ ] A User can define code that needs to be run once before executing a group of tests by defining a method called `before`.
- [ ] A User can define code that needs to be run before each test in a group of tests by defining a method called `beforeEach`.
- [ ] A User can define code that needs to be run once after executing a group of tests by defining a method called `after`.
- [ ] A User can define code that needs to be run after each test in a group of tests by defining a method called `afterEach`.
- [x] A User can run a test class (& any nested classes) manually by defining a main() method that creates an instance of itself, passes it to Runner instance, & calls the Runner's run() method.
- [x] A User receives a concise indicator of test result status after running a test class with a '.' to indicate a successful test & an 'F' to indicate a failure.
- [x] A User receives a count of successful tests out of total tests at the end of the report.
- [x] A User receives details about any failed test, including the test class name, test method name, failure reason (exception name & message), & any stack trace.
- [x] A User recieves a verbose output of test result status, consisting of the test class name, followed by a list of test method names & "Success" or "Failure", depending on the result.
- [ ] A User can opt not to receive the verbose output
- [x] A User can use a cli command (`java -ea jspec.cli.CLI`, or use the shell script `./jspec`) to have jspec recursively traverse a given (could default to PWD or project) directory & subdirectories & discover any files ending in `Spec.java`, then run the tests for all files found.
- [x] A User can give a glob pattern to use when searching for spec files, replacing the default `Spec.java` pattern.
- [x] A User can specify a single test file to run.
- [ ] Update PROPOSAL Algorithm section
- [ ] Update UML
- [ ] figure out packaging as executable & library in single JAR
- [ ] write presentation
  - [ ] goals
  - [ ] outline
    - [ ] topics only
    - [ ] add details
    - [ ] refine to slides
  - [ ] slides?
  - [ ] live-coding?

Principles used:

- OOP
  - Inheritance - Group & test definitions, ResultsTree & Tree<T>
  - Encapsulation - Group, Result, Runner, Node<T>, Tree<T>, DoublyLinkedList<T> ...
  - Aggregation - Node<T> & Node<Result>, Node<T> & Tree<T>, Node<T> & DoublyLinkedList<T>
  - Composition - Tree<Result> & Runner
  - Polymorphism - Tree/DoublyLinkedList w/ map, reduce, contains, etc... via FindPredicate, MapConsumer, ReduceConsumer, & ForEachConsumer
- Data Structures
  - custom implementations of:
    - Doubly linked list
    - n-ary tree
  - use of:
    - Stack
    - ArrayList
- Algorithms
  - preorder traversal of n-ary tree, two ways (Group/sub-Groups & Tree<T>.reduce callers)
  ? postorder traversal of n-ary tree
  ? inorder traversal of n-ary tree
