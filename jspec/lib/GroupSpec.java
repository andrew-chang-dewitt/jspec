package jspec.lib;

import java.util.ArrayList;

public class GroupSpec extends Group {
  final String desc = "class: Group";

  public static void main(String[] args) {
    GroupSpec spec = new GroupSpec();

    spec
      .visit()
      .getResults()
      .forEach(result -> System.out.println(
        result.getDescription() +
        " " +
        (result.didPass()
          ? "✅"
          : "❌")));
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

    assert m.visit().getResults().size() == 2;
  }

  String descTestFailure = "Evaluating a test captures failed assertions & alerts the user of a failed test";
  public void testTestFailure() {
    class Failure extends Group {
      public void testFailure() {
        assert false : "This will fail";
      }
    }

    Failure f = new Failure();

    assert f.visit().getResults().get(0).didPass() == false;
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
    String actual = m.visit().getResults().get(0).getMethodName();

    assert actual.compareTo("testATest") == 0;
  }

  String descDescriptiveMethodName = "A test method can be given a descriptive name";
  public void testDescriptiveMethodName() {
    class MethodName extends Group {
      String descATest = "a descriptive name";
      public void testATest() {}
    }

    MethodName m = new MethodName();
    String actual = m.visit().getResults().get(0).getDescription();

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
    ArrayList<Result> results = g.visit().getResults();

    assert results.get(0).didPass() : "first test passes";
    assert results.get(1).didPass() : "second test passes";
    assert !results.get(2).didPass() : "third test fails";
  }

  String descVisitReturnsNestedGroups = "Visiting a group also returns any nested Groups";
  public void testVisitReturnsNestedGroups() {
    class Example extends Group {
      class Nested extends Group {}
    }

    Example g = new Example();
    ArrayList<Group> children = g.visit().getChildren();

    assert children instanceof ArrayList
      : "children should be an array list";
    assert children.get(0) instanceof Group
      : "members of children should be additional groups";
  }
}
