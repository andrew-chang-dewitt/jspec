- [x] define TestGroup & write tests
- [\] define `Runner` & write tests
  - [ ] refine output text appearance
  - [ ] refine Group result output text
- [ ] define `CLI` & write tests
- [x] research Java packaging
  - more modern packaging is worth it for real world dev, use Gradle (preferred) or maybe Maven
  - not necessary for this project though, can just use make to handle compiling, building, & testing; can even create target that "installs" jspec to PATH to make it globally available or locally installs using direnv (if available)
- [ ] do more research on asynchronous Java programming
- [x] research tree structures
  - it appears that writing groups as a class containing tests as methods is trivial
  - using nested classes as a way of logically grouping tests that belong as a sub group becomes a tree
  - can get test methods using `groupInstance.getClass().getDeclaredMethods()`
  - can get nested group classes using `parentGroupInstance.getClass().getDeclaredClasses()`
  - can get Group attributes like `description` using `groupInstance.getClass().getDeclaredField("description")`
  - traversing the test tree will be [preorder traversal](http://cs360.cs.ua.edu/lectures-new/36%20Non-Binary%20Trees%20and%20Traversals.pdf), doing the following for each Node (a Group)
    1. Get the Group's `description` field, or Class name if no description
    2. Call the Group's `before()` method, if defined
    3. Get the Group's Methods, then iterate over them:
      1. Call the Group's `beforeEach()` method, if defined
      2. Evaluate the test & store the TestResult
        ^^ this indicates a need for some sort of tree of Results
      3. Call the Group's `afterEach()` method, if defined
    4. Call the Group's `after()` method, if defined
    5. Get the Group's nested Groups, & do repeat this process for each of them
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
