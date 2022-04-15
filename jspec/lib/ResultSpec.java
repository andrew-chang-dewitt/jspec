package jspec.lib;

public class ResultSpec extends Group {
  public String desc = "class: lib.Result";

  public static void main(String[] args) {
    ResultSpec spec = new ResultSpec();
    new Runner(spec)
      .run(false)
      .resultStrings()
      .forEach((node, i) -> System.out.println(node.getValue()));
  }

  public void testRepresentPass() {
    Result r = ResultFactory.create().pass();

    try {
      assert r.didPass();
    } catch (NotATestResult exc) {
      assert false : exc.getMessage();
    }
  }

  public void testRepresentFailure() {
    Result r = ResultFactory.create().fail(new Exception("an exception"));

    try {
      assert !r.didPass();
    } catch (NotATestResult exc) {
      assert false : exc.getMessage();
    }
  }

  public void testFailureIncludesExceptionInfo() {
    String expected = "message";
    Result r = ResultFactory.create().fail(new Exception(expected));

    try {
      String actual = r.getFailureExc().getMessage();

      assert actual.compareTo(expected) == 0;
    } catch (NotATestResult exc) {
      assert false : exc.getMessage();
    }
  }

  public void testResultKnowsTestMethodName() {
    String expected = "testName";
    Result r = new Result(expected);

    String actual = r.getCodeName();

    assert actual.compareTo(expected) == 0;
  }

  public void testResultMayKnowTestDescName() {
    String expected = "a description";
    Result r = ResultFactory.create().describe(expected);

    String actual = r.getDescription();

    assert actual.compareTo(expected) == 0;
  }

  public void testResultCanRenderAStatus() {
    String name = "testName";
    Result r = new Result(name);

    String actual = r.statusString();
    String expected = name;

    assert actual.compareTo(expected) == 0
      : "Expected '" + actual + "' to equal '" + expected + "'";
  }

  public void testStatusIncludesPassIndicatorIfTest() {
    String name = "testName";
    Result r = new Result(name);
    r.pass();

    String actual = r.statusString();
    String expected = name + " ✅";

    assert actual.compareTo(expected) == 0
      : "Expected '" + actual + "' to equal '" + expected + "'";
  }

  public void testStatusCanBeGivenAPrefix() {
    String name = "testName";
    Result r = new Result(name);

    String actual = r.statusString("prefix");
    String expected = "prefix" + name;

    assert actual.compareTo(expected) == 0
      : "Expected '" + actual + "' to equal '" + expected + "'";
  }
}

class ResultFactory {
  static Result create() {
    return new Result("");
  }
}
