package jspec.lib;

import jspec.utils.list.DoublyLinkedList;

public class Result {
  private String codeName;
  private String descName;
  private boolean testResult;

  private boolean pass;
  private Throwable exc;

  Result(String codeName) {
    this.codeName = codeName;
    this.testResult = false;
  }

  public Result describe(String description) {
    this.descName = description;

    return this;
  }

  public Result pass() {
    this.testResult = true;
    this.pass = true;

    return this;
  }

  public Result fail(Throwable exc) {
    this.testResult = true;
    this.pass = false;
    this.exc = exc;

    return this;
  }

  public String getName() {
    if (this.descName != null && this.descName != "")
      return this.descName;

    return this.codeName;
  }
  public String getCodeName() {
    return this.codeName;
  }

  public String getDescription() {
    return this.descName;
  }

  public boolean isTest() {
    return this.testResult;
  }

  public boolean didPass() throws NotATestResult {
    if (this.isTest()) return this.pass;

    throw new NotATestResult(this.codeName, "didPass");
  }

  public Throwable getFailureExc() throws NotATestResult {
    if (this.isTest()) return this.exc;

    throw new NotATestResult(this.codeName, "getFailureExc");
  }

  public String statusString() {
    return this.statusString("");
  }

  public String statusString(String prefix) {
    // text should be long description, if it exists
    String text = this.getName();

    try {
      // symbol to indicate if test passed or failed
      String symb = this.didPass()
        ? "✅"
        : "❌";

      // prepend prefix & append text with symbol
      return prefix + text + " " + symb;
    } catch (NotATestResult exc) {
      // if Result is a Group Result, just return the prefixed text
      return prefix + text;
    }
  }

  public DoublyLinkedList<String> failureStrings() throws NotATestResult {

    if (this.isTest()) {
      // create new list
      DoublyLinkedList<String> out = new DoublyLinkedList<String>()
        .append("")
        .append("")
        .append("=".repeat(80))
        .append("❌ FAILURE: " + this.getName())
        .append("-".repeat(80))
        .append(this.exc.toString())
        .append("");

      DoublyLinkedList<String> trace = DoublyLinkedList
        // from Throwable's stack trace array
        .fromArray(this.exc.getStackTrace())
        // then convert to list of strings
        // using StackTraceElement.toString()
        .map((trc, idx) -> "        at " + trc.getValue().toString());

      return out.concat(trace);
    }

    throw new NotATestResult(this.codeName, "failureStrings");
  }
}
