package jspec.lib;

public class RunnerSpec extends Group {
  final String desc = "class: Runner";

  public static void main(String[] args) {
    RunnerSpec runner = new RunnerSpec();

    runner
      .visit()
      .forEach(
        result -> System.out.println(result.getDescription() +
                                     (result.didPass()
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
}
