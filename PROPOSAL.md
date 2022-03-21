JSpec testing library
===

A simple unit testing library for TDD & cli for running tests.

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

### Notes/Thoughts on Data Design:

So far, I think there's a few obvious conclusions about how the solution might work.

1. There's 3 separate parts here: a `Group` (& its contained tests/sub-groups), a `Runner`, & a CLI.
  A CLI requires a `Runner` & a `Runner` requires at least one `Group`, but a `Group` doesn't need to know anything about the `Runner` & the `Runner` doesn't need to know anything about the CLI.
2. A `Runner` may execute tests contained in multiple `Group`s, so it will need to know how to organize them & what order (if any) to do the tests in.
3. There's no need for data permanence, as tests are simply functions.
  If the test-writer needs the test to work with any data, that is a separate problem that will need to be solved by the writer.

Point 2 implies a 1:many `Group`:`Runner` relationship & point 1 implies a 1:1 CLI:`Runner` relationship.
Given the 1:many `Group`:`Runner` relationship, there will need to be some sort of collection on `Runner` to hold the aggregation of `Group`s.
Additionally, the `Runner` will need to not only execute all the tests belonging to a given `Group`, but also all the tests belonging to any nested `Group`s, which can contain nested `Group`s of their own, continuing to an arbitrary depth.

These relationships & the need for a traversal of unknown breadth & depth probably lead to a recursive algorithm.
This could either happen on the fly as the tree of test `Group`s is traversed using `Class.getMethods()` & `Class.getClasses()`.
On the fly is simple, but might be more difficult to find room for efficiency.
Alternatively, the `Group` classes & their nested `Group`s could be traversed first, without any execution of their tests.
This would require building a tree of some sort containing pointers to all the test methods (and possibly other data too like descriptive names or shared state).
This alternative feels more complex, but might make it easier to execute tests simultaneously & asynchronously for faster total test execution time.

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
FAILURE: Some group name/description: Some test name/description
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

Stack trace output follows last
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass
  Some Method() nn:mm in SomeModule
  Some Method() nn:mm on SomeClass


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
See more info about each class below:

### Group

A User creates a `Group` of tests by defining a new class that inherits from this class.
Nested groups are created by defining a member class inside a child of `Group` that also inherits from `Group`.
Most User stories belonging to the **Definition** group above will be built as features on `Group`.

There might be subclasses of `Group` that add some sort of functionality that changes a typical `Group`'s behavior, i.e. asynchronous test execution.

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
