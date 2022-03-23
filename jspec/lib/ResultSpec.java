package jspec.lib;

public class ResultSpec extends Group {
  public static void main(String[] args) {
    ResultSpec resultspec = new ResultSpec();

    resultspec
      .visit()
      .forEach(result -> System.out.println("result: " + result.didPass()));
  }

  public void testRepresentPass() {
    Result r = new Result().pass();

    assert r.didPass();
  }

  public void testRepresentFailure() {
    Result r = new Result().fail(new Exception("an exception"));

    assert !r.didPass();
  }

  public void testFailureIncludesExceptionInfo() {
    String expected = "message";
    Result r = new Result().fail(new Exception(expected));

    String actual = r.getFailureExc().getMessage();

    assert actual.compareTo(expected) == 0;
  }
}
