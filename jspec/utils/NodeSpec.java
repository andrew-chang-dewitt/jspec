package jspec.utils;

import jspec.lib.Group;
import jspec.lib.Runner;

public class NodeSpec extends Group {
  final String desc = "utils.Node";

  public static void main(String[] args) {
    NodeSpec spec = new NodeSpec();
    new Runner(spec)
      .run(false)
      .resultStrings()
      .forEach((node, i) -> System.out.println(node.getValue()));
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

  public String descKnowsItsParent = "A Node knows its parent Node";
  public void testKnowsItsParent() {
    Node<Integer> n = new Node<Integer>(1);
    Node<Integer> o = new Node<Integer>(2).addParent(n);

    assert o.getParent() == n;
  }

  public class ChildNodes extends Group {
    final String desc = "A Node knows its child Nodes";

    public String descKnowsHead = "A Node has a head child";
    public void testKnowsHead() {
      Node<Integer> n = new Node<Integer>(1);
      Node<Integer> o = new Node<Integer>(2).addHeadChild(n);

      assert o.getHeadChild() == n;
    }

    public String descKnowsTail = "A Node has a tail child";
    public void testKnowsTail() {
      Node<Integer> n = new Node<Integer>(1);
      Node<Integer> o = new Node<Integer>(2).addTailChild(n);

      assert o.getTailChild() == n;
    }
  }
}
