package jspec.lib;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Group {
  static String testPrefix = "test";
  static String descPrefix = "desc";

  protected String desc = null;

  public ArrayList<Result> visit(ArrayList<Result> results) {
    // this.getClass() will return a Group or any descendent of it
    // using ? extends Group allows for that possibility
    Class<? extends Group> instanceClass = this.getClass();
    Method[] tests = instanceClass.getDeclaredMethods();

    for (Method test : tests) {
      String name = test.getName();
      if (name.startsWith(Group.testPrefix)) {
        Result result = new Result(name);
        String desc = this.findDescName(name, instanceClass);

        if (desc != null) {
          result.describe(desc);
        }

        try {
          test.invoke(this);
          results.add(result.pass());
        } catch (InvocationTargetException exc) {
          results.add(result.fail(exc));
        } catch (IllegalAccessException exc) {
          results.add(result.fail(exc));
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

  private String findDescName(String name, Class<? extends Group> instanceClass) {
    int prefixLength = Group.testPrefix.length();
    String withoutPrefix = name.substring(prefixLength);

    try {
      return (String)instanceClass
        .getDeclaredField(Group.descPrefix + withoutPrefix)
        .get(this);
    } catch (IllegalAccessException exc) {
      return null;
    } catch (NoSuchFieldException exc) {
      return null;
    }
  }
}
