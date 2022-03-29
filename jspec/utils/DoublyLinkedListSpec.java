package jspec.utils;

import jspec.lib.Group;

public class DoublyLinkedListSpec extends Group {
  final String desc = "utils.LinkedList";

  public static void main(String[] args) {
    DoublyLinkedListSpec spec = new DoublyLinkedListSpec();

    spec
      .visit()
      .forEach(result -> {
        System.out.println(
            result.getDescription() +
            " " +
            (result.didPass()
             ? "✅"
             : "❌"));

        if (!result.didPass()) {
          System.out.print("\n");
          result.getFailureExc().getCause().printStackTrace();
          System.out.print("\n");
        }
      });
  }

  public String descCreatingWithOneNode = "Create a list with only one node sets that node as the head & tail";
  public void testCreatingWithOneNode () {
    Node<String> n = new Node<String>("a value");
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n);

    assert l.getHead() == n;
    assert l.getTail() == n;
  }

  public String descCreatingWithTwoNodes = "Creating a list with two nodes sets the first one as the head & the second one as the tail";
  public void testCreatingWithTwoNodes() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value");
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    assert l.getHead() == n;
    assert l.getTail() == o;
  }

  public String descListIsIterable = "A list can be iterated over";
  public void testListIsIterable() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    Node<String> p = new Node<String>("a value").addPrevSibling(o);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, p);

    l.forEach((x, i) -> {
      if (i == 0) { assert x == n; }
      if (i == 1) { assert x == o; }
      if (i == 2) { assert x == p; }
    });
  }

  public String descListIsIterableInReverse = "A list can be iterated over in reverse";
  public void testListIsIterableInReverse() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    Node<String> p = new Node<String>("a value").addPrevSibling(o);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, p);

    l.forEach((x, i) -> {
      if (i == 0) { assert x == p; }
      if (i == 1) { assert x == o; }
      if (i == 2) { assert x == n; }
    }, true);
  }

  public String descReduce = "A list can be reduced";
  public void testReduce() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    Node<String> p = new Node<String>("a value").addPrevSibling(o);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, p);

    String actual = l.reduce((str, node, inx) -> {
      return str + node.getValue();
    }, "");
    String expected = "a valuea valuea value";

    // reduce returns expected value
    assert actual.compareTo(expected) == 0 : actual + " != " + expected;
    // without changing original list or it's nodes
    assert l.getHead() == n;
    assert l.getTail() == p;
    l.forEach((node, idx) -> {
      assert node.getValue().compareTo("a value") == 0;
    });
  }

  public String descListLength = "A list knows its length";
  public void testListLength() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    int actual = l.getLength();
    int expected = 2;

    assert actual == expected : actual + " != " + expected;
  }

  public String descAppendNode = "A new node can be appended to the list";
  public void testAppendNode() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    Node<String> p = new Node<String>("a value");
    l.append(p);

    // tail becomes newly appended node
    Node<String> actualTail = l.getTail();
    assert actualTail == p : actualTail + " != " + p;

    // length increases from 2 to 3
    int actualLength = l.getLength();
    int expectedLength = 3;
    assert actualLength == expectedLength : actualLength + " != " + expectedLength;
  }

  public String descPrependNode = "A new node can be prepended to the list";
  public void testPrependNode() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    Node<String> p = new Node<String>("a value");
    l.prepend(p);

    // head becomes newly prepended node
    Node<String> actualHead = l.getHead();
    assert actualHead == p : actualHead + " != " + p;

    // length increases from 2 to 3
    int actualLength = l.getLength();
    int expectedLength = 3;
    assert actualLength == expectedLength : actualLength + " != " + expectedLength;
  }
}
