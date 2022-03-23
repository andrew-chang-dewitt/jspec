package jspec.lib;

public class ResultSpec extends Group {
  public static void main(String[] args) {
    ResultSpec resultspec = new ResultSpec();

    resultspec
      .visit()
      .forEach(result -> System.out.println("result: " + result.didPass()));
  }

  public void testRepresentPass() {
    Result r = ResultFactory.create().pass();

    assert r.didPass();
  }

  public void testRepresentFailure() {
    Result r = ResultFactory.create().fail(new Exception("an exception"));

    assert !r.didPass();
  }

  public void testFailureIncludesExceptionInfo() {
    String expected = "message";
    Result r = ResultFactory.create().fail(new Exception(expected));

    String actual = r.getFailureExc().getMessage();

    assert actual.compareTo(expected) == 0;
  }

  public void testResultKnowsTestMethodName() {
    String expected = "testName";
    Result r = new Result(expected);

    String actual = r.getMethodName();

    assert actual.compareTo(expected) == 0;
  }

  public void testResultMayKnowTestDescName() {
    String expected = "a description";
    Result r = ResultFactory.create().describe(expected);

    String actual = r.getDescription();

    assert actual.compareTo(expected) == 0;
  }
}

class ResultFactory {
  static Result create() {
    return new Result("");
  }
}
