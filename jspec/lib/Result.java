package jspec.lib;

import java.util.ArrayList;

public class Result {
  private boolean pass;
  private Exception exc;

  Result() {}

  public Result pass() {
    Result r = new Result();
    r.pass = true;

    return r;
  }

  public Result fail(Exception exc) {
    Result r = new Result();
    r.pass = false;
    r.exc = exc;

    return r;
  }

  public boolean didPass() {
    return this.pass;
  }

  public Exception getFailureExc() {
    return this.exc;
  }
}
