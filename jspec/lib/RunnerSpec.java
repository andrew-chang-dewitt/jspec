package jspec.lib;

import java.util.Iterator;

import jspec.utils.Node;
import jspec.utils.ValueNotFound;

public class RunnerSpec extends Group {
  final String desc = "class: Runner";

  public static void main(String[] args) {
    RunnerSpec spec = new RunnerSpec();

    spec
      .visit()
      .getResults()
      .forEach(( result, i ) -> {
        System.out.print(result.getValue().getDescription());
        System.out.print(" ");

        try {
            if (result.getValue().didPass()) {
                System.out.print("✅\n");
            } else {
                System.out.print("❌\n");
                System.out.println();
                result.getValue().getFailureExc().getCause().printStackTrace();
                System.out.println();
            }
        } catch (NotATestResult exc) {
        }
      });
  }

  String descConstructorNoArgs = "A Runner can be created w/ no arguments";
  public void testConstructorNoArgs() {
    Runner r = new Runner();

    assert r instanceof Runner : "r should be a Runner";
  }

  String descConstructorOneArg = "A Runner can be created w/ one Group";
  public void testConstructorOneArg() {
    Runner r = new Runner(new Group());

    assert r instanceof Runner : "r should be a Runner";
  }

  String descConstructorManyArgs = "A Runner can be created w/ many Groups";
  public void testConstructorManyArgs() {
    Runner r = new Runner(new Group(), new Group(), new Group(), new Group());

    assert r instanceof Runner : "r should be a Runner";
  }

  String descAddAGroup = "A Group can be added to the Runner later using a Builder pattern";
  public void testAddAGroup() {
    Runner r = RunnerFactory.create();
    Object actual = r.addGroup(new Group());

    assert actual instanceof Runner : "addGroup should return the Runner";
  }

  String descAddGroupWorks = "Adding a Group to the Runner actually works";
  public void testAddGroupWorks() {
    Runner r = RunnerFactory.create();
    Group g = new Group();
    r.addGroup(g);

    assert r.getGroups().contains(g);
  }

  String descRunningTests = "A Runner can run tests for a given Group";
  public void testRunningTests() {
    class G extends Group {
      public void testATest() {
        assert true;
      }
    }

    Runner r = RunnerFactory.create();
    r.addGroup(new G());

    try {
      assert r
        .run()
        .getResults()
        .find(
          (node) -> node.getValue().getCodeName() == "testATest"
        )
        .getValue()
        .didPass();
    } catch (ValueNotFound exc) {
      assert false : "testATest result not found in results";
    } catch (NotATestResult exc) {
      assert false : "testATest result should be a test result";
    }
  }

  String descRunningTestsForMany = "A Runner can run tests for all given Groups";
  public void testRunningTestsForMany() {
    // Create two test Groups
    class G1 extends Group {
      public void testATest() {
        assert true;
      }

      public void testAnother() {
        assert false;
      }
    }

    class G2 extends Group {
      public void testATest() {
        assert true;
      }
    }

    // Then build a runner and add them to it
    Runner r = RunnerFactory.create();
    r.addGroup(new G1());
    r.addGroup(new G2());

    // Run the runner & get the test results
    ResultsTree results = r.run().getResults();

    // The results should contain a node for each test group
    // Search for a group by checking if the result's codeName
    // contains the name defined in the inner class—the actual
    // name is something like "jspec.lib.RunnerSpec$1G1" since
    // the group is an inner local class
    assert results.contains(
      (node) -> node.getValue().getCodeName().contains("G1"));
    assert results.contains(
      (node) -> node.getValue().getCodeName().contains("G2"));
  }

  String descRunningTestsForNested = "A Runner can run tests for nested Groups";
  public void testRunningTestsForNested() {
    // Create two test Groups
    class G1 extends Group {
      public void testATest() {
        assert true;
      }

      public void testAnother() {
        assert false;
      }

      class G2 extends Group {
        public void innerTest() {
          assert true;
        }
      }
    }

    // Then build a runner and add them to it
    Runner r = RunnerFactory.create();
    r.addGroup(new G1());

    // Run the runner & get the test results
    ResultsTree results = r.run().getResults();

    // The results should contain a node for each test group
    try {
      // get the actual node containing the result
      Node<Result> rG1 = results.find(
        (node) -> node.getValue().getCodeName().contains("G1"));
      Node<Result> rG2 = results.find(
        (node) -> node.getValue().getCodeName().contains("G2"));
      // so that we can make an assertion about the relative
      // locations of each node
      // the results tree should look something like this:
      //             .
      //             |
      //            rG1
      //           / | \
      //  testATest  |  rG2
      //             |     \
      //        testAnother \
      //                     \
      //                     innerTest
      assert rG2.getParent() == rG1
        : "G1 result node should be parent of G2 result node";
    } catch (ValueNotFound exc) {
      assert false : exc.getMessage();
    }
  }

  public String descResultsAsStrings = "Runner can generate a list of strings from results";
  public void testResultsAsStrings() {
    // Create two test Groups
    class G1 extends Group {
      public void testATest() {
        assert true;
      }

      public void testAnother() {
        assert false;
      }

      class G2 extends Group {
        public void innerTest() {
          assert true;
        }
      }
    }

    // Then build a runner and add them to it
    Runner r = RunnerFactory.create();
    r.addGroup(new G1());

    // Run the runner & get the results as list of strings
    Iterator<String> strings = r.run().resultStrings().iterator();

    // FIXME: I think the error here might be in Result's status String method
    String actual = strings.next();
    String expected = "";
    assert actual == expected : "Expected '" + actual + "' to equal '" + expected + "'";
    actual = strings.next();
    expected = "G1";
    assert actual.contains(expected) : "Expected " + actual + " to contain " + expected;
    actual = strings.next();
    expected = "testATest";
    assert actual.contains(expected);
    assert actual.contains(expected) : "Expected " + actual + " to contain " + expected;
    actual = strings.next();
    expected = "testAnother";
    assert actual.contains(expected) : "Expected " + actual + " to contain " + expected;
    actual = strings.next();
    expected = "G2";
    assert actual.contains(expected) : "Expected " + actual + " to contain " + expected;
    actual = strings.next();
    expected = "innerTest";
    assert actual.contains(expected) : "Expected " + actual + " to contain " + expected;
  }
}

class RunnerFactory {
  public static Runner create() {
    return new Runner();
  }
}
