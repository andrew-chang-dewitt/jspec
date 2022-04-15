package jspec.utils.list;

import jspec.lib.Group;
import jspec.lib.Runner;

import java.util.Iterator;
import jspec.utils.Node;

public class DoublyLinkedListSpec extends Group {
  public String desc = "class: utils.list.DoublyLinkedList";

  public static void main(String[] args) {
    DoublyLinkedListSpec spec = new DoublyLinkedListSpec();
    new Runner(spec)
      .run(false)
      .resultStrings()
      .forEach((node, i) -> System.out.println(node.getValue()));
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

  public String descCreatingAsEmpty = "Creating a list as empty sets head & tail as null";
  public void testCreatingAsEmpty() {
    DoublyLinkedList<String> l = new DoublyLinkedList<String>();

    assert l.getHead() == null;
    assert l.getTail() == null;
  }

  // FIXME: needs edge cases tested
  // 1. loop empty list
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

  // FIXME: needs edge cases tested
  // 1. reduce empty list
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

  // FIXME: needs edge cases tested
  // 1. get length of empty list
  // 2. get length of very long list
  public String descListLength = "A list knows its length";
  public void testListLength() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    int actual = l.getLength();
    int expected = 2;

    assert actual == expected : actual + " != " + expected;
  }

  // FIXME: needs edge cases tested:
  // 1. append to empty list
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

  public String descAppendToEmpty = "A node can be appended to an empty list";
  public void testAppendToEmpty() {
    DoublyLinkedList<String> l = new DoublyLinkedList<String>();

    Node<String> p = new Node<String>("a value");
    l.append(p);

    // head & tail both become newly appended node
    Node<String> actualHead = l.getHead();
    assert actualHead == p : actualHead + " != " + p;
    Node<String> actualTail = l.getTail();
    assert actualTail == p : actualTail + " != " + p;

    // length increases from 0 to 1
    int actualLength = l.getLength();
    int expectedLength = 1;
    assert actualLength == expectedLength : actualLength + " != " + expectedLength;
  }

  // FIXME: needs edge cases tested:
  // 1. prepend to empty list
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

  public String descPrependToEmpty = "A node can be prepended to an empty list";
  public void testPrependToEmpty() {
    DoublyLinkedList<String> l = new DoublyLinkedList<String>();

    Node<String> p = new Node<String>("a value");
    l.prepend(p);

    // head & tail both become newly prepended node
    Node<String> actualHead = l.getHead();
    assert actualHead == p : actualHead + " != " + p;
    Node<String> actualTail = l.getTail();
    assert actualTail == p : actualTail + " != " + p;

    // length increases from 0 to 1
    int actualLength = l.getLength();
    int expectedLength = 1;
    assert actualLength == expectedLength : actualLength + " != " + expectedLength;
  }

  // FIXME: needs edge cases tested:
  // 1. get index > length - 1
  // 2. get negative index
  public String descGetNodeAtIndex = "A node can be fetched by its index";
  public void testGetNodeAtIndex() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    Node<String> actual = l.get(1);
    assert actual == o : actual + " != " + o;
  }

  // FIXME: needs edge cases tested:
  // 1. replace head
  // 2. replace tail
  // 3. replace only remaining node
  public String descReplaceNodeAtIndex = "A node at a given index can be replaced";
  public void testReplaceNodeAtIndex() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    Node<String> p = new Node<String>("a value");

    Node<String> returnedNode = l.replace(p, 1);
    Node<String> nodeAtOne = l.get(1);

    assert nodeAtOne == p : nodeAtOne + " != " + p;
    assert returnedNode == o : returnedNode + " != " + o;
  }

  // FIXME: needs edge cases tested:
  // 1. delete head
  // 2. delete tail
  // 3. delete only remaining node
  public String descDeleteNodeAtIndex = "A node at a given index can be deleted";
  public void testDeleteNodeAtIndex() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value").addPrevSibling(n);
    Node<String> p = new Node<String>("a value").addPrevSibling(o);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, p);

    Node<String> returnedNode = l.delete(1);
    Node<String> nodeAtOne = l.get(1);

    assert nodeAtOne == p : nodeAtOne + " != " + p;
    assert returnedNode == o : returnedNode + " != " + o;
  }

  public String descContainsNode = "A list can tell you if it contains a given node";
  public void testContainsNode() {
    Node<String> n = new Node<String>("a value");
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n);

    assert l.contains(n);

    Node<String> o = new Node<String>("a value");
    assert !l.contains(o);
  }

  public String descCanBeIterated = "A list is iterable";
  public void testCanBeIterated() {
    Node<String> n = new Node<String>("n");
    Node<String> o = new Node<String>("o").addPrevSibling(n);
    Node<String> p = new Node<String>("p").addPrevSibling(o);
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, p);

    Iterator<String> iter = l.iterator();

    assert iter.next() == "n";
    assert iter.next() == "o";
    assert iter.next() == "p";
  }

  public String descConcatTwoLists = "Two lists can be concatenated";
  public void testConcatTwoLists() {
    Node<String> n = new Node<String>("n");
    Node<String> o = new Node<String>("o").addPrevSibling(n);
    Node<String> p = new Node<String>("p").addPrevSibling(o);
    DoublyLinkedList<String> l1 = new DoublyLinkedList<String>(n, p);

    Node<String> q = new Node<String>("q");
    Node<String> r = new Node<String>("r").addPrevSibling(q);
    Node<String> s = new Node<String>("s").addPrevSibling(r);
    DoublyLinkedList<String> l2 = new DoublyLinkedList<String>(q, s);

    l1.concat(l2);

    Iterator<String> iter = l1.iterator();

    String actual = iter.next();
    String expected = "n";
    assert actual.compareTo(expected) == 0 : actual + " != " + expected;
    actual = iter.next();
    expected = "o";
    assert actual.compareTo(expected) == 0 : actual + " != " + expected;
    actual = iter.next();
    expected = "p";
    assert actual.compareTo(expected) == 0 : actual + " != " + expected;
    actual = iter.next();
    expected = "q";
    assert actual.compareTo(expected) == 0 : actual + " != " + expected;
    actual = iter.next();
    expected = "r";
    assert actual.compareTo(expected) == 0 : actual + " != " + expected;
    actual = iter.next();
    expected = "s";
    assert actual.compareTo(expected) == 0 : actual + " != " + expected;
  }
}
