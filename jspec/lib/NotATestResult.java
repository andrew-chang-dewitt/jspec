 package jspec.lib;

public class NotATestResult extends Exception {
  NotATestResult(String codeName, String opName) {
    super("Result " + codeName + "is not a test result, unable to use method " + opName);
  }
}
