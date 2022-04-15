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

  String descTestDefinition = "A test is defined by declaring a method with the prefix 'test'";
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

  String descTestFailure = "Evaluating a test captures failed assertions & alerts the user of a failed test";
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

  String descDescriptiveGroupName = "A Group can have a more descriptive name";
  public void testDescriptiveGroupName() {
    class GroupName extends Group {
      final String desc = "A descriptive name";
    }

    GroupName n = new GroupName();

    assert n.desc.compareTo("A descriptive name") == 0;
  }

  String descDescriptiveGroupNameDefaultNull = "A Group's default descriptive name is null";
  public void testDescriptiveGroupNameDefaultNull() {
    class GroupName extends Group {}

    GroupName n = new GroupName();

    assert n.desc == null;
  }

  String descTestMethodName = "A test gives its method name on invocation";
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

  String descDescriptiveMethodName = "A test method can be given a descriptive name";
  public void testDescriptiveMethodName() {
    class MethodName extends Group {
      String descATest = "a descriptive name";
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

  String descVisitReturnsResults = "Visiting a group returns the results of each test";
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

  String descVisitReturnsNestedGroups = "Visiting a group also returns any nested Groups";
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
