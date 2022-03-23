package jspec.lib;

import java.util.ArrayList;

public class Result {
  private boolean pass;
  private Exception exc;
  private String methodName;
  private String descName;

  Result(String methodName) {
    this.methodName = methodName;
  }

  public Result describe(String description) {
    this.descName = description;

    return this;
  }

  public Result pass() {
    this.pass = true;

    return this;
  }

  public Result fail(Exception exc) {
    this.pass = false;
    this.exc = exc;

    return this;
  }

  public boolean didPass() {
    return this.pass;
  }

  public Exception getFailureExc() {
    return this.exc;
  }

  public String getMethodName() {
    return this.methodName;
  }

  public String getDescription() {
    return this.descName;
  }
}
