package jspec.lib;

import jspec.utils.list.DoublyLinkedList;

public class GroupSpec extends Group {
  public String desc = "class: lib.Group";

  public static void main(String[] args) {
    GroupSpec spec = new GroupSpec();
    new Runner(spec)
      .run(false)
      .resultStrings()
      .forEach((node, i) -> System.out.println(node.getValue()));
  }

  public class TestDefinition extends Group {
    public String desc = "A Group is used to define tests";

    public String descTestDefinition = "A test is defined by declaring a method with the prefix 'test'";
    public void testTestDefinition() {
      assert true;
    }

    public String descFindsAllTests = "Any method beginning with 'test' will be evaluated.";
    public void testFindsAllTests() {
      class Multiple extends Group {
        public void testOne() {
          assert true;
        }

        public void testTwo() {
          assert true;
        }
      }

      Multiple m = new Multiple();

      assert m.visit(true).getResults().getLength() == 2;
    }

    public String descTestFailure = "Evaluating a test captures failed assertions & alerts the user of a failed test";
    public void testTestFailure() {
      class Failure extends Group {
        public void testFailure() {
          assert false : "This will fail";
        }
      }

      Failure f = new Failure();

      try {
        assert f
          .visit(true)
          .getResults()
          .get(0)
          .getValue()
          .didPass() == false;
      } catch (NotATestResult exc) {
        assert false : exc.getMessage();
      }
    }
  }

  public class Names extends Group {
    public String desc = "A Group & its tests have names";

    public String descDescriptiveGroupName = "A Group can have a more descriptive name";
    public void testDescriptiveGroupName() {
      class GroupName extends Group {
        public String desc = "A descriptive name";
      }

      GroupName n = new GroupName();

      assert n.desc.compareTo("A descriptive name") == 0;
    }

    public String descDescriptiveGroupNameDefaultNull = "A Group's default descriptive name is null";
    public void testDescriptiveGroupNameDefaultNull() {
      class GroupName extends Group {}

      GroupName n = new GroupName();

      assert n.desc == null;
    }

    public String descTestMethodName = "A test gives its method name on invocation";
    public void testTestMethodName() {
      class MethodName extends Group {
        public void testATest() {}
      }

      MethodName m = new MethodName();
      String actual = m
        .visit(true)
        .getResults()
        .get(0)
        .getValue()
        .getCodeName();

      assert actual.compareTo("testATest") == 0;
    }

    public String descDescriptiveMethodName = "A test method can be given a descriptive name";
    public void testDescriptiveMethodName() {
      class MethodName extends Group {
        public String descATest = "a descriptive name";
        public void testATest() {}
      }

      MethodName m = new MethodName();
      String actual = m
        .visit(true)
        .getResults().get(0)
        .getValue()
        .getDescription();

      assert actual.compareTo("a descriptive name") == 0;
    }
  }

  public class Visit extends Group {
    public String desc = "A Group can be visited";

    public String descVisitReturnsResults = "Visiting a group returns the results of each test";
    public void testVisitReturnsResults() {
      class Example extends Group {
        public void testA() {
          assert true;
        }
        public void testB() {
          assert true;
        }
        public void testF() {
          assert false;
        }
      }

      Example g = new Example();
      DoublyLinkedList<Result> results = g.visit(true).getResults();

      try {
        Result result = results.get(0).getValue();
        assert result.didPass() : "first test passes";
        String actual = result.getCodeName();
        String expected = "testA";
        assert actual.compareTo(expected) == 0
          : "Expected '" + actual + "' to equal '" + expected + "'";

        result = results.get(1).getValue();
        assert result.didPass() : "second test passes";
        actual = result.getCodeName();
        expected = "testB";
        assert actual.compareTo(expected) == 0
          : "Expected '" + actual + "' to equal '" + expected + "'";

        result = results.get(2).getValue();
        assert !result.didPass() : "third test fails";
        actual = result.getCodeName();
        expected = "testF";
        assert actual.compareTo(expected) == 0
          : "Expected '" + actual + "' to equal '" + expected + "'";
      } catch (NotATestResult exc) {
        assert false : exc.getMessage();
      }
    }

    public String descVisitReturnsNestedGroups = "Visiting a group also returns any nested Groups";
    public void testVisitReturnsNestedGroups() {
      class Example extends Group {
        class Nested extends Group {}
      }

      Example g = new Example();
      DoublyLinkedList<Group> children = g.visit(true).getChildren();

      assert children.get(0).getValue().getClass() == Example.Nested.class
        : "given group should be in returned list of children";
    }
  }

  public class Before extends Group {
    public String desc = "A Group can perform setup code before running any tests";

    public String descDefaultsToNoOp = "Nothing happens if a before() method is not defined";
    public void testDefaultsToNoOp() {
      class Test extends Group {
        int state = 0;

        public void testState() {
          assert this.state == 1;
        }
      }

      Test t = new Test();
      Result r = t.visit(true).getResults().get(0).getValue();

      try {
        assert !r.didPass() : "state was modified when it shouldn't have been";
      } catch (NotATestResult exc) {
        assert false : "this shouldn't happen";
      }
    }

    public String descPerformsSetup = "Defining before() can perform setup tasks";
    public void testPerformsSetup () {
      class Test extends Group {
        int state = 0;

        public void before() {
          ++this.state;
        }

        public void testState() {
          assert this.state == 1;
        }
      }

      Test t = new Test();
      Result r = t.visit(true).getResults().get(0).getValue();

      try {
        assert r.didPass() : "state was not modified when it should have been";
      } catch (NotATestResult exc) {
        assert false : "this shouldn't happen";
      }
    }
  }

  public class After extends Group {
    public String desc = "A Group can perform teardown code after running all tests";

    public String descDefaultsToNoOp = "Nothing happens if a after() method is not defined";
    public void testDefaultsToNoOp() {
      class Test extends Group {
        public int state = 0;

        public void testState() {
          ++this.state;

          assert this.state == 1;
        }
      }

      Test t = new Test();
      t.visit(true);

      assert t.state == 1 : "state was modified when it shouldn't have been";
    }

    public String descPerformsTeardown = "Defining after() can perform teardown tasks";
    public void testPerformsTeardown () {
      class Test extends Group {
        public int state = 0;

        public void testState() {
          ++this.state;

          assert this.state == 1;
        }

        public void after() {
          this.state = 0;
        }
      }

      Test t = new Test();
      t.visit(true);

      assert t.state == 0 : "state wasn't modified when it should have been";
    }
  }
}
