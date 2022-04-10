package jspec.lib;

import jspec.utils.list.DoublyLinkedList;

public class Result {
  private String codeName;
  private String descName;
  private boolean testResult;

  private boolean pass;
  private Exception exc;

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

  public Result fail(Exception exc) {
    this.testResult = true;
    this.pass = false;
    this.exc = exc;

    return this;
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

  public Exception getFailureExc() throws NotATestResult {
    if (this.isTest()) return this.exc;

    throw new NotATestResult(this.codeName, "getFailureExc");
  }

  public String statusString() {
    return this.statusString("");
  }

  public String statusString(String prefix) {
    // text should be long description, if it exists
    String text = this.getDescription() == ""
      ? this.getCodeName()
      : this.getDescription();

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
      return DoublyLinkedList
        // from Throwable's stack trace array
        .fromArray(this.exc.getStackTrace())
        // then convert to list of strings
        // using StackTraceElement.toString()
        .map((trc, idx) -> trc.toString());
    }

    throw new NotATestResult(this.codeName, "failureStrings");
  }
}

class NotATestResult extends Exception {
  NotATestResult(String codeName, String opName) {
    super("Result " + codeName + "is not a test result, unable to use method " + opName);
  }
}
