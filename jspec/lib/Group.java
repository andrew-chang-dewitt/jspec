package jspec.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class Group {
  static String testPrefix = "test";
  static String descPrefix = "desc";

  protected String desc = null;

  public VisitResults visit(ArrayList<Result> results) {
    // this.getClass() will return a Group or any descendent of it
    // using ? extends Group allows for that possibility
    Class<? extends Group> instanceClass = this.getClass();
    Method[] tests = instanceClass.getDeclaredMethods();
    Class<?>[] nestedClasses = instanceClass.getDeclaredClasses();

    return new VisitResults(
        this.evaluate(results, tests, instanceClass), 
        this.findChildren(nestedClasses, this));
  }

  // overload w/ default value of empty Results collection
  // when not given one
  public VisitResults visit() {
    return this.visit(new ArrayList<Result>());
  }

  private ArrayList<Result> evaluate(
    ArrayList<Result> outResults, 
    Method[] tests,
    Class<? extends Group> instanceClass
  ) {
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
          outResults.add(result.pass());
        } catch (InvocationTargetException exc) {
          outResults.add(result.fail(exc));
        } catch (IllegalAccessException exc) {
          outResults.add(result.fail(exc));
        }
      }
    }

    return outResults;
  }

  private ArrayList<Group> findChildren(Class<?>[] nested, Group parent) {
    ArrayList<Group> children = new ArrayList<Group>();

    for (Class<?> c : nested) {
      if (Group.class.isAssignableFrom(c)) {
        try {
          Constructor<?> constructor = c.getDeclaredConstructor(parent.getClass());
          Group group = (Group)constructor.newInstance(parent);
          children.add(group);
        } catch (IllegalAccessException exc) {
          System.err.println("Error executing constructor on class: " + c);
          System.err.println(exc);
        } catch (InvocationTargetException exc) {
          System.err.println("Error executing constructor on class: " + c);
          System.err.println(exc);
        } catch (InstantiationException exc) {
          System.err.println("Error executing constructor on class: " + c);
          System.err.println(exc);
        } catch (NoSuchMethodException exc) {
          System.err.println("Error executing constructor on class: " + c);
          System.err.println(exc);
        }
      }
    }

    return children;
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

class VisitResults {
  private ArrayList<Group> children;
  private ArrayList<Result> results;

  VisitResults(ArrayList<Result> results, ArrayList<Group> children) {
    this.children = children;
    this.results = results;
  }

  public ArrayList<Group> getChildren() {
    return this.children;
  }

  public ArrayList<Result> getResults() {
    return this.results;
  }
}
