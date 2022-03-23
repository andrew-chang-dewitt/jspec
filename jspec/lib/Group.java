package jspec.lib;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import jspec.lib.Result;

public class Group {
  static String prefix = "test";

  protected String desc = null;

  public ArrayList<Result> visit(ArrayList<Result> results) {
    Method[] tests = this.getClass().getDeclaredMethods();

    for (Method test : tests) {
      String name = test.getName();
      if (name.startsWith(Group.prefix)) {
        try {
          test.invoke(this);
          results.add(new Result("").pass());
        } catch (InvocationTargetException exc) {
          results.add(new Result("").fail(exc));
        } catch (IllegalAccessException exc) {
          results.add(new Result("").fail(exc));
        }
      }
    }

    return results;
  }

  // overload w/ default value of empty Results collection
  // when not given one
  public ArrayList<Result> visit() {
    return this.visit(new ArrayList<Result>());
  }
}
