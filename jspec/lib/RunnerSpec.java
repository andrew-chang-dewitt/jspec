package jspec.lib;

public class RunnerSpec extends Group {
  final String desc = "class: Runner";

  public static void main(String[] args) {
    RunnerSpec spec = new RunnerSpec();

    spec
      .visit()
      .getResults()
      .forEach((result, i) -> System.out.println(
        result.getValue().getDescription() +
        " " +
        (result.getValue().didPass()
          ? "✅"
          : "❌")));
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

    assert r.run().get(0).didPass();
  }

  String descRunningTestsForMany = "A Runner can run tests for all given Groups";
  public void testRunningTestsForMany() {
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

    Runner r = RunnerFactory.create();
    r.addGroup(new G1());
    r.addGroup(new G2());

    assert r.run().get(0).didPass();
    assert !r.run().get(1).didPass();
    assert r.run().get(2).didPass();
  }
}

class RunnerFactory {
  public static Runner create() {
    return new Runner();
  }
}
