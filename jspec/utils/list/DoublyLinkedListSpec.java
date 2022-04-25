package jspec.utils.list;

import jspec.lib.Group;
import jspec.lib.Runner;

import java.util.Iterator;
import java.util.NoSuchElementException;

import jspec.utils.Node;

public class DoublyLinkedListSpec extends Group {
  public String desc = "class: utils.list.DoublyLinkedList";

  public static void main(String[] args) {
    DoublyLinkedListSpec spec = new DoublyLinkedListSpec();
    new Runner(spec)
      .run(false)
      .resultStrings(true)
      .forEach((node, i) -> System.out.println(node.getValue()));
  }

  public String descCreatingWithOneNode =
    "Create a list with only one node sets that node as the head & tail";
  public void testCreatingWithOneNode () {
    Node<String> n = new Node<String>("a value");
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n);

    assert l.getHead() == n;
    assert l.getTail() == n;
  }

  public String descCreatingWithTwoNodes =
    "Creating a list with two nodes sets the first one as the head & the second one as the tail";
  public void testCreatingWithTwoNodes() {
    Node<String> n = new Node<String>("a value");
    Node<String> o = new Node<String>("a value");
    DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

    assert l.getHead() == n;
    assert l.getTail() == o;
  }

  public String descCreatingAsEmpty =
    "Creating a list as empty sets head & tail as null";
  public void testCreatingAsEmpty() {
    DoublyLinkedList<String> l = new DoublyLinkedList<String>();

    assert l.getHead() == null;
    assert l.getTail() == null;
  }

  public class ForEachAble extends Group {
    public String desc = "DoublyLinkedList.forEach()";

    public String descListIsIterable = "It executes given function once for each node";
    public void testListIsIterable() {
      Node<String> n = new Node<String>("a value");
      Node<String> o = new Node<String>("a value").addPrevSibling(n);
      Node<String> p = new Node<String>("a value").addPrevSibling(o);
      DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, p);

      l.forEach((x, i) -> {
        if (i == 0) assert x == n;
        else if (i == 1) assert x == o;
        else if (i == 2) assert x == p;
        else assert false : "list should only have indices 0, 1, & 2";
      });
    }

    public String descEmptyList = "It does nothing when called on an empty list";
    public void testEmptyList() {
      DoublyLinkedList<String> l = new DoublyLinkedList<String>();

      l.forEach((x, i) -> {
        assert false : "this shouldn't happen since the list should be empty";
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
  }

  public class Reducable extends Group {
    public String desc = "DoublyLinkedList.reduce()";

    public String descReduce = "Builds a new data structure using the given callback";
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

    public String descEmpty = "Returns unmodified initial value if list is empty";
    public void testEmpty() {
      DoublyLinkedList<String> l = new DoublyLinkedList<String>();

      String actual = l.reduce((str, node, inx) -> {
        return str + node.getValue();
      }, "");

      String expected = "";

      // reduce returns expected value
      assert actual.compareTo(expected) == 0 : actual + " != " + expected;
    }
  }

  public class Length extends Group {
    public String desc= "DoublyLinkedList.getLength()";

    public String descListLength = "Returns the length of the list";
    public void testListLength() {
      Node<String> n = new Node<String>("a value");
      Node<String> o = new Node<String>("a value").addPrevSibling(n);
      DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

      int actual = l.getLength();
      int expected = 2;

      assert actual == expected : actual + " != " + expected;
    }

    public String descEmpty = "Returns 0 for an empty list";
    public void testEmpty() {
      DoublyLinkedList<String> l = new DoublyLinkedList<String>();

      int actual = l.getLength();
      int expected = 0;

      assert actual == expected : actual + " != " + expected;
    }
  }

  public class Appendable extends Group {
    public String desc = "DoublyLinkedList.append()";

    public String descAppendNode = "Adds given node to end of the list";
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

    public String descAppendToEmpty = "Adds given node as head & tail if list is empty";
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

    public String descAppendValue =
      "Also adds a new node from a value only instead of requiring a node";
    public void testAppendValue() {
      Node<String> n = new Node<String>("a value");
      Node<String> o = new Node<String>("a value").addPrevSibling(n);
      DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

      String p = "a value";
      l.append(p);

      // tail becomes newly appended node
      String actual = l.getTail().getValue();
      assert actual.compareTo(p) == 0
        : actual + " != " + p;

      // length increases from 2 to 3
      int actualLength = l.getLength();
      int expectedLength = 3;
      assert actualLength == expectedLength : actualLength + " != " + expectedLength;
    }
  }

  public class Prependable extends Group {
    public String desc = "DoublyLinkedList.prepend()";

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

    public String descPrependValue =
      "Also adds a new node from a value only instead of requiring a node";
    public void testPrependValue() {
      Node<String> n = new Node<String>("a value");
      Node<String> o = new Node<String>("a value").addPrevSibling(n);
      DoublyLinkedList<String> l = new DoublyLinkedList<String>(n, o);

      String m = "a value";
      l.prepend(m);

      // head becomes newly prepended node
      String actual = l.getHead().getValue();
      assert actual.compareTo(m) == 0
        : actual + " != " + m;

      // length increases from 2 to 3
      int actualLength = l.getLength();
      int expectedLength = 3;
      assert actualLength == expectedLength : actualLength + " != " + expectedLength;
    }
  }

  public class Getter extends Group {
    public String desc = "DoublyLinkedList.get()";

    Node<String> n;
    Node<String> o;
    DoublyLinkedList<String> l;

    public void before() {
      this.n = new Node<String>("a value");
      this.o = new Node<String>("a value").addPrevSibling(n);
      this.l = new DoublyLinkedList<String>(n, o);

    }

    public String descGetNodeAtIndex = "Returns the node at the given index";
    public void testGetNodeAtIndex() {
      Node<String> actual = this.l.get(1);
      assert actual == this.o : actual + " != " + this.o;
    }

    public String descGetIndexGreaterThanLength =
      "Throws NoSuchElement if given index is greater than the index of the last node";
    public void testGetIndexGreaterThanLength() {
      try {
        Node<String> actual = this.l.get(2);
        assert false
          : "Got " + actual + ": " + actual.getValue()
            + ", expected NoSuchElementException";
      } catch (NoSuchElementException exc) {
        assert true;
      }
    }

    public String descNegativeIndex =
      "Throws IndexOutOfBoundsException if given negative index";
    public void testNegativeIndex() {
      try {
        Node<String> actual = this.l.get(-1);
        assert false
          : "Got " + actual + ": " + actual.getValue()
            + ", expected IndexOutOfBoundsException";
      } catch (IndexOutOfBoundsException exc) {
        assert true;
      }
    }
  }

  public class Replacer extends Group {
    public String desc = "DoublyLinkedList.replace()";

    Node<String> n;
    Node<String> o;
    Node<String> p;
    DoublyLinkedList<String> l;

    public void before() {
      this.n = new Node<String>("a value");
      this.o = new Node<String>("a value").addPrevSibling(n);
      this.p = new Node<String>("a value").addPrevSibling(o);
      this.l = new DoublyLinkedList<String>(n, p);
    }

    public String descReplaceNodeAtIndex =
      "Replaces the node at the given index with the given node";
    public void testReplaceNodeAtIndex() {
      Node<String> r = new Node<String>("a value");

      Node<String> returnedNode = this.l.replace(r, 1);
      Node<String> nodeAtOne = this.l.get(1);

      assert nodeAtOne == r
        : "Node at index 1 should equal " + r + " not " + nodeAtOne;
      assert returnedNode == this.o
        : "Returned node should equal " + this.o + " not " + returnedNode;
    }

    public String descReplaceHeadNode =
      "Works when replacing the head node";
    public void testReplaceHeadNode() {
      Node<String> r = new Node<String>("a value");

      Node<String> returnedNode = this.l.replace(r, 0);
      Node<String> replacedNode = this.l.get(0);

      assert replacedNode == r
        : "Node at index 0 should equal " + r + " not " + replacedNode;
      assert returnedNode == this.n
        : "Returned node should equal " + this.n + " not " + returnedNode;
    }

    public String descReplaceTailNode =
      "Works when replacing the tail node";
    public void testReplaceTailNode() {
      Node<String> r = new Node<String>("a value");

      Node<String> returnedNode = this.l.replace(r, 2);
      Node<String> replacedNode = this.l.get(2);

      assert replacedNode == r
        : "Node at index 2 should equal " + r + " not " + replacedNode;
      assert returnedNode == this.p
        : "Returned node should equal " + this.p + " not " + returnedNode;
    }
  }

  public class Delete extends Group {
    public String desc = "DoublyLinkedList.delete()";

    Node<String> n;
    Node<String> o;
    Node<String> p;
    DoublyLinkedList<String> l;

    public void beforeEach() {
      this.n = new Node<String>("a value");
      this.o = new Node<String>("a value").addPrevSibling(n);
      this.p = new Node<String>("a value").addPrevSibling(o);
      this.l = new DoublyLinkedList<String>(this.n, this.p);
    }

    public String descDeleteNodeAtIndex =
      "Removes the node at the given index from the list & returns it";
    public void testDeleteNodeAtIndex() {
      Node<String> returnedNode = this.l.delete(1);

      int length = this.l.getLength();
      assert length == 2 :
        "length should go from 3 to 2, instead length is " + length;

      Node<String> nodeAtOne = this.l.get(1);

      assert nodeAtOne == this.p : nodeAtOne + " != " + this.p;
      assert returnedNode == this.o : returnedNode + " != " + this.o;
    }

    public String descDeleteHead =
      "Works on the head node";
    public void testDeleteHead() {
      Node<String> returnedNode = this.l.delete(0);

      int length = this.l.getLength();
      assert length == 2 :
        "length should go from 3 to 2, instead length is " + length;

      Node<String> nodeAtZero = this.l.get(0);

      assert nodeAtZero == this.o : nodeAtZero + " != " + this.o;
      assert returnedNode == this.n : returnedNode + " != " + this.n;
    }

    public String descDeleteTail =
      "Works on the tail node";
    public void testDeleteTail() {
      Node<String> returnedNode = this.l.delete(2);

      int length = this.l.getLength();
      assert length == 2 :
        "length should go from 3 to 2, instead length is " + length;

      Node<String> nodeAtOne = this.l.get(1);

      assert nodeAtOne == this.o : nodeAtOne + " != " + this.o;
      assert returnedNode == this.p : returnedNode + " != " + this.p;
    }

    public String descDeleteOnly =
      "Works when deleting the only node";
    public void testDeleteOnly() {
      // set up list w/ only 1 node
      Node<String> node = new Node<String>("a value");
      DoublyLinkedList<String> oneNode = new DoublyLinkedList<String>(node);

      // delete that node
      Node<String> returnedNode = oneNode.delete(0);

      int length = oneNode.getLength();
      assert length == 0 :
        "length should go from 1 to 0, instead length is " + length;
      assert returnedNode == node :
        "expected delete to return " + node + ", instead got " + returnedNode;
    }
  }

  public class Contains extends Group {
    public String desc = "DoublyLinkedList.contains()";

    Node<String> n;
    DoublyLinkedList<String> l;

    public void before() {
      this.n = new Node<String>("a value");
      this.l = new DoublyLinkedList<String>(n);
    }

    public String descContainsNode = "Returns true if it contains a given node";
    public void testContainsNode() {
      assert this.l.contains(this.n);
    }

    public String descDoesntContain = "Returns false if it doesn't contain a given node";
    public void testDoesntContain() {
      Node<String> o = new Node<String>("a value");
      assert !this.l.contains(o);
    }
  }

  public class Iterable extends Group {
    public String desc = "A list is iterable";

    DoublyLinkedList<String> l;

    public void before() {
      Node<String> n = new Node<String>("n");
      Node<String> o = new Node<String>("o").addPrevSibling(n);
      Node<String> p = new Node<String>("p").addPrevSibling(o);
      this.l = new DoublyLinkedList<String>(n, p);
    }

    public String descNext = "Iterator.next() returns the next node's value";
    public void testNext() {
      Iterator<String> iter = this.l.iterator();

      String actual = iter.next();
      String expected = "n";
      assert actual.compareTo(expected) == 0
        : "expected actual to equal " + expected + ", got " + actual + " instead";

      actual = iter.next();
      expected = "o";
      assert actual.compareTo(expected) == 0
        : "expected actual to equal " + expected + ", got " + actual + " instead";

      actual = iter.next();
      expected = "p";
      assert actual.compareTo(expected) == 0
        : "expected actual to equal " + expected + ", got " + actual + " instead";
    }

    public String descHasNext = "Iterator.hasNext() tells if there's another value";
    public void testHasNext() {
      Iterator<String> iter = this.l.iterator();

      assert iter.hasNext() : "there's a first value";
      iter.next();
      assert iter.hasNext() : "there's a second value";
      iter.next();
      assert iter.hasNext() : "there's a third value";
      iter.next();
      assert !iter.hasNext() : "there's not a fourth value";
    }

    public String descNextThrows =
      "Iterator.next() throws NoSuchElement when there's no more elements";
    public void testNextThrows() {
      Iterator<String> iter = this.l.iterator();

      iter.next();
      iter.next();
      iter.next();

      try {
        iter.next();
        assert false : "next() should have thrown an exception";
      } catch (NoSuchElementException exc) {
        assert true;
      }
    }
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
