package jspec.lib;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

import jspec.utils.list.DoublyLinkedList;

public class Group {
  static String testPrefix = "test";
  static String descPrefix = "desc";

  protected String desc = null;

  public VisitResults visit() {
    // this.getClass() will return a Group or any descendent of it
    // using ? extends Group allows for that possibility
    Class<? extends Group> instanceClass = this.getClass();
    Method[] tests = instanceClass.getDeclaredMethods();
    Class<?>[] nestedClasses = instanceClass.getDeclaredClasses();

    return new VisitResults(
        this.evaluate(tests, instanceClass),
        this.findChildren(nestedClasses, this));
  }

  private DoublyLinkedList<Result> evaluate(
    Method[] tests,
    Class<? extends Group> instanceClass
  ) {
    DoublyLinkedList<Result> results = new DoublyLinkedList<Result>();

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
          results.append(result.pass());
        } catch (InvocationTargetException exc) {
          results.append(result.fail(exc));
        } catch (IllegalAccessException exc) {
          results.append(result.fail(exc));
        }
      }
    }

    return results;
  }

  private DoublyLinkedList<Group> findChildren(Class<?>[] nested, Group parent) {
    DoublyLinkedList<Group> children = new DoublyLinkedList<Group>();

    for (Class<?> c : nested) {
      if (Group.class.isAssignableFrom(c)) {
        try {
          Constructor<?> constructor = c.getDeclaredConstructor(parent.getClass());
          Group group = (Group)constructor.newInstance(parent);
          children.append(group);
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
