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

#### Some thoughts on assertions

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

So far, I think there's a few obvious conclusions about how the solution might work.

1. There's 3 separate parts here: a Group (& its contained tests/sub-groups), a Runner, & a CLI.
  A CLI requires a Runner & a Runner requires at least one Group, but a Group doesn't need to know anything about the Runner & the Runner doesn't need to know anything about the CLI.
2. A Runner may execute tests contained in multiple Groups, so it will need to know how to organize them & what order (if any) to do the tests in.
3. There's no need for data permanence, as tests are simply functions.
  If the test-writer needs the test to work with any data, that is a separate problem that will need to be solved by the writer.

Point 2 implies a 1:many Group:Runner relationship & point 1 implies a 1:1 CLI:Runner relationship.
Given the 1:many Group:Runner relationship, there will need to be some sort of collection on Runner to hold the aggregation of Groups.
Additionally, the Runner will need to not only execute all the tests belonging to a given Group, but also all the tests belonging to any nested Groups, which can contain nested Groups of their own, continuing to an arbitrary depth.

These relationships & the need for a traversal of unknown breadth & depth probably lead to a recursive algorithm.
