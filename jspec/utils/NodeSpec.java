package jspec.utils;

import jspec.lib.Group;

public class NodeSpec extends Group {
  final String desc = "utils.Node";

  public static void main(String[] args) {
    NodeSpec spec = new NodeSpec();

    spec
      .visit()
      .forEach(result -> System.out.println(
        result.getDescription() +
        " " +
        (result.didPass()
          ? "✅"
          : "❌")));
  }

  public String descStoresAValue = "A Node stores a given value";
  public void testStoresAValue() {
    Node<String> n = new Node<String>("a value");

    assert n.getValue().compareTo("a value") == 0 : "node's value should be a string stating 'a value'";
  }
  
  public String descKnowsItsNextSibling = "A Node knows its next sibling Node";
  public void testKnowsItsNextSibling() {
    Node<Integer> n = new Node<Integer>(1);
    Node<Integer> o = new Node<Integer>(2).addNextSibling(n);

    assert o.getNextSibling() == n;
  }

  public String descAddNextSiblingToAAddsPrevtoB = "Adding Node A as next sibling to B adds B as prev sibling to A";
  public void testAddNextSiblingToAAddsPrevtoB() {
    Node<Integer> n = new Node<Integer>(1);
    Node<Integer> o = new Node<Integer>(2).addNextSibling(n);

    assert n.getPrevSibling() == o;
  }

  public String descKnowsItsPrevSibling = "A Node knows its previous sibling Node";
  public void testKnowsItsPrevSibling() {
    Node<Integer> n = new Node<Integer>(1);
    Node<Integer> o = new Node<Integer>(2).addPrevSibling(n);

    assert o.getPrevSibling() == n;
  }

  public String descAddPrevSiblingToAAddsNexttoB = "Adding Node A as prev sibling to B adds B as next sibling to A";
  public void testAddPrevSiblingToAAddsNexttoB() {
    Node<Integer> n = new Node<Integer>(1);
    Node<Integer> o = new Node<Integer>(2).addPrevSibling(n);

    assert n.getNextSibling() == o;
  }

  public String descWorksWithThreePlus = "Same behaviour works with 3+ nodes";
  public void testWorksWithThreePlus() {
    Node<Integer> n = new Node<Integer>(1);
    Node<Integer> o = new Node<Integer>(2).addNextSibling(n);
    Node<Integer> p = new Node<Integer>(2).addNextSibling(o);

    assert p.getPrevSibling() == null;
    assert p.getNextSibling() == o;
    assert o.getPrevSibling() == p;
    assert o.getNextSibling() == n;
    assert n.getPrevSibling() == o;
    assert n.getNextSibling() == null;
  }
}
