package jspec.lib;

import jspec.utils.Node;
import jspec.utils.list.DoublyLinkedList;
import jspec.utils.ValueNotFound;

public class RunnerSpec extends Group {
  public String desc = "class: lib.Runner";

  public static void main(String[] args) {
    RunnerSpec spec = new RunnerSpec();
    new Runner(spec)
      .run(false)
      .resultStrings()
      .forEach((node, i) -> System.out.println(node.getValue()));
  }

  public class Creation extends Group {
    public String desc = "Creating a Runner";

    public String descConstructorNoArgs = "Can be created w/ no arguments";
    public void testConstructorNoArgs() {
      Runner r = new Runner();

      assert r instanceof Runner : "r should be a Runner";
    }

    public String descConstructorOneArg = "Can be created w/ one Group";
    public void testConstructorOneArg() {
      Runner r = new Runner(new Group());

      assert r instanceof Runner : "r should be a Runner";
    }

    public String descConstructorManyArgs = "Can be created w/ many Groups";
    public void testConstructorManyArgs() {
      Runner r = new Runner(new Group(), new Group(), new Group(), new Group());

      assert r instanceof Runner : "r should be a Runner";
    }

    public String descAddAGroup = "A Group can be added to the Runner later using a Builder pattern";
    public void testAddAGroup() {
      Runner r = RunnerFactory.create();
      Object actual = r.addGroup(new Group());

      assert actual instanceof Runner : "addGroup should return the Runner";
    }

    public String descAddGroupWorks = "Adding a Group to the Runner actually works";
    public void testAddGroupWorks() {
      Runner r = RunnerFactory.create();
      Group g = new Group();
      r.addGroup(g);

      assert r.getGroups().contains(g);
    }
  }

  public class Running extends Group {
    public String desc = "Running a Runner's Groups";

    public String descRunningTests = "Can run tests for a given Group";
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
          .run(true)
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

    public String descRunningTestsForMany = "Can run tests for all given Groups";
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
      ResultsTree results = r.run(true).getResults();

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

    public String descRunningTestsForNested = "Can run tests for nested Groups";
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
          public void testInner() {
            assert true;
          }
        }
      }

      // Then build a runner and add them to it
      Runner r = RunnerFactory.create();
      r.addGroup(new G1());

      // Run the runner & get the test results
      ResultsTree results = r.run(true).getResults();

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
  }

  public class Results extends Group {
    public String desc = "A Runner can generate a list of strings from test results";

    Runner r;

    public void beforeEach() {
      // Create two test Groups
      class G1 extends Group {
        public void testATest() {
          assert true;
        }

        public void testAnother() {
          assert false : "this fails";
        }

        class G2 extends Group {
          public void testInner() {
            assert true;
          }
        }
      }

      // Then build a runner and add them to it
      this.r = RunnerFactory.create();
      this.r.addGroup(new G1());
      this.r.run(true);
    }

    public String descVerbose = "Defaults to verbose output";
    public void testVerbose() {
      // Run the runner & get the results as list of strings
      DoublyLinkedList<String> actual = this.r.resultStrings();

      // Build list of expected values to compare to
      DoublyLinkedList<String> expected = new DoublyLinkedList<String>()
        .append("")            // should be padded w/ 4 empty lines
        .append("")
        .append("")
        .append("")
        .append("G1")          // then contain the name of the first group
        .append("testATest")   // then the name of the first test
        .append("testAnother") // then the name of the second test
        .append("")            // then be padded by an empty line
        .append("G2")          // then contain the name of the inner group
        .append("testInner")   // then contain the name of the inner test
        .append("")                           // a failure is padded by two empty lines
        .append("")
        .append("=".repeat(80))               // then bordered by a line of `=`
        .append("FAILURE: testAnother")       // contain FAILURE & the test name
        .append("-".repeat(80))               // then bordered by a line of `-`
        .append("AssertionError: this fails") // then the error type & message
        .append("")                           // then padded by an empty line
        .append("G1.testAnother");            // then contain the name of the group & test

      // loop over expected lines & compare to actual lines at the same index
      expected.forEach((exp, idx) -> {
        String actualVal = actual.get(idx).getValue();

        // because tests are evaluated in non-deterministic order w/in a
        // single group, G1 could be followed by testATest or testAnother
        // these tests occur at indexes 4 & 5, we'll test them differently
        // the rest to allow for this possibility of changing order
        if (idx != 4 && idx != 5) {
          String expectedVal = exp.getValue();

          // if expecting an empty line, test for equality
          if (expectedVal.compareTo("") == 0)
            assert actualVal.compareTo(expectedVal) == 0
              : actualVal + " != " + expectedVal;
          // otherwise test for actual containing the expected text
          else
            assert actualVal.contains(expectedVal)
              : actualVal + " doesn't contain " + expectedVal;
        } else {
          // when testing indexes 4 & 5, we check that the actual value
          // contains either the value at expected index 4 or the value
          // at expected index 5
          String exp4 = expected.get(4).getValue();
          String exp5 = expected.get(5).getValue();

          assert actualVal.contains(exp4) || actualVal.contains(exp5)
            : actualVal + " doesn't contain " + exp4 + " or " + exp5;
        }
      });

      // finally test that the output ends with the stats
      // starting w/ a border
      int actualLength = actual.getLength();
      String actualVal = actual.get(actualLength - 4).getValue();
      String expectedVal = "=====";
      assert actualVal.contains(expectedVal)
        : actualVal + " doesn't contain " + expectedVal;

      // then an empty line
      actualVal = actual.get(actualLength - 3).getValue();
      expectedVal = "";
      assert actualVal.compareTo(expectedVal) == 0
        : actualVal + " != " + expectedVal;

      // then a count of how many tests passed out of the total
      actualVal = actual.get(actualLength - 2).getValue();
      expectedVal = "2/3 tests passed (66.7%)";
      assert actualVal.compareTo(expectedVal) == 0
        : actualVal + " != " + expectedVal;

      // then ends w/ an empty line
      actualVal = actual.get(actualLength - 1).getValue();
      expectedVal = "";
      assert actualVal.compareTo(expectedVal) == 0
        : actualVal + " != " + expectedVal;
    }

    public String descConcise = "Can give concise output";
    public void testConcise() {
      // Run the runner & get the results as list of strings
      DoublyLinkedList<String> actual = this.r.resultStrings(true);

      // start with first line
      int position = 0;
      // test that output starts with FAILURE info
      String actualVal = actual.get(position).getValue();
      String expectedVal = "";
      assert actualVal.compareTo(expectedVal) == 0
        : "line " + position + " should be empty, instead contains " + actualVal;

      ++position;
      actualVal = actual.get(position).getValue();
      assert actualVal.compareTo(expectedVal) == 0
        : "line " + position + " should be empty, instead contains " + actualVal;

      ++position;
      actualVal = actual.get(position).getValue();
      assert actualVal.compareTo(expectedVal) == 0
        : "line " + position + " should be empty, instead contains " + actualVal;

      ++position;
      actualVal = actual.get(position).getValue();
      expectedVal = "=====";
      assert actualVal.contains(expectedVal)
        : "line " + position + " should contain" + expectedVal + ", instead contains " + actualVal;

      ++position;
      actualVal = actual.get(position).getValue();
      expectedVal = "FAILURE: testAnother";
      assert actualVal.contains(expectedVal)
        : "line " + position + " should contain " + expectedVal + ", instead contains " + actualVal;

      // skip failure info
      while (!actual.get(position).getValue().contains("=====")) ++position;

      // then ends with the STATS info
      // starting with an empty line
      ++position;
      actualVal = actual.get(position).getValue();
      expectedVal = "";
      assert actualVal.compareTo(expectedVal) == 0
        : position + ": " + actualVal + " != " + expectedVal;

      // then a count of how many tests passed out of the total
      ++position;
      actualVal = actual.get(position).getValue();
      expectedVal = "2/3 tests passed (66.7%)";
      assert actualVal.compareTo(expectedVal) == 0
        : position + ": " + actualVal + " != " + expectedVal;

      // then ends w/ an empty line
      ++position;
      actualVal = actual.get(position).getValue();
      expectedVal = "";
      assert actualVal.compareTo(expectedVal) == 0
        : position + ": " + actualVal + " != " + expectedVal;
    }
  }
}

class RunnerFactory {
  public static Runner create() {
    return new Runner();
  }
}
