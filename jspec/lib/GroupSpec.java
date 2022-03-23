package jspec.lib;

public class GroupSpec extends Group {
  public static void main(String[] args) {
    GroupSpec group = new GroupSpec();

    group
      .visit()
      .forEach(result -> System.out.println("result: " + result.didPass()));
  }

  String descTestDefinition = "A test is defined by declaring a method with the prefix 'test'";
  public void testTestDefinition() {
    assert true;
  }

  String descFindsAllTests = "Any method beginning with 'test' will be evaluated.";
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

    assert m.visit().size() == 2;
  }

  String descTestFailure = "Evaluating a test captures failed assertions & alerts the user of a failed test";
  public void testTestFailure() {
    class Failure extends Group {
      public void testFailure() {
        assert false : "This will fail";
      }
    }

    Failure f = new Failure();
    
    assert f.visit().get(0).didPass() == false;
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
    String actual = m.visit().get(0).getMethodName();

    assert actual.compareTo("testATest") == 0;
  }

  String descDescriptiveMethodName = "A test method can be given a descriptive name";
  public void testDescriptiveMethodName() {
    class MethodName extends Group {
      String descATest = "a descriptive name";
      public void testATest() {}
    }

    MethodName m = new MethodName();
    String actual = m.visit().get(0).getDescription();

    assert actual.compareTo("a descriptive name") == 0;
  }
}
