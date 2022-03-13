Test Lib
===

A simple unit testing library for TDD & cli for running tests.

Features
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

**TODO:** Define some features that are used to manage assertions. It'd be nice to keep them simple (using basic `assert` keyword & assoc. syntax), but grant some nice features around the error messaging on failure. Also would be nice to have some ability to have multiple assertions in one test and still all when a preceding one fails. This will require some thought on how best to design these features.

#### Some thoughts on assertions

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

### Test discovery

Encompasses features for recursively finding all tests in a given directory & running them.
