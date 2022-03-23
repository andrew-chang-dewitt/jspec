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
}
