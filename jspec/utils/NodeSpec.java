package jspec.utils;

import jspec.lib.Group;
import jspec.lib.Runner;

public class NodeSpec extends Group {
  public String desc = "class: utils.Node";

  public static void main(String[] args) {
    NodeSpec spec = new NodeSpec();
    new Runner(spec)
      .run(false)
      .resultStrings(false)
      .forEach((node, i) -> System.out.println(node.getValue()));
  }

  public String descStoresAValue = "A Node stores a given value";
  public void testStoresAValue() {
    Node<String> n = new Node<String>("a value");

    assert n.getValue().compareTo("a value") == 0 : "node's value should be a string stating 'a value'";
  }

  public class Siblings extends Group {
    public String desc = "Nodes have siblings";

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

    public class RemoveSiblings extends Group {
      public String desc = "A Node's Siblings can be removed.";

      public class RemoveNext extends Group {
        public String desc = "Removing the next Sibling";

        Node<Integer> n;
        Node<Integer> o;

        public void before() {
          // set up node o as next sibling of node n
          this.n = new Node<Integer>(1);
          this.o = new Node<Integer>(2).addNextSibling(n);

          Node<Integer> oNext = o.getNextSibling();
          Node<Integer> nPrev = n.getPrevSibling();

          assert oNext == n
            : "O's next should be n, not " + oNext;
          assert nPrev == o
            : "N's prev should be o, not " + nPrev;
        }

        public String descSetsNextToNull =
          "Removing the next sibling sets next back to null";
        public void testSetsNextToNull() {
          // remove o's next sibling
          o.removeNextSibling();

          Node<Integer> actual = o.getNextSibling();
          assert actual == null
            : "Node o's next sibling should be null, not " + actual;
        }

        public String descSetsNextsPrevToNull =
          "Also sets the Node that was next's previous Node to null";
        public void testSetsNextsPrevToNull() {
          Node<Integer> actual = n.getPrevSibling();
          assert actual == null
            : "Node n's prev sibling should be null, not " + actual;
        }
      }

      public class RemovePrev extends Group {
        public String desc = "Removing the previous Sibling";

        Node<Integer> n;
        Node<Integer> o;

        public void before() {
          // set up node o as next sibling of node n
          this.o = new Node<Integer>(1);
          this.n = new Node<Integer>(2).addPrevSibling(o);

          Node<Integer> oNext = o.getNextSibling();
          Node<Integer> nPrev = n.getPrevSibling();

          assert oNext == n
            : "O's next should be n, not " + oNext;
          assert nPrev == o
            : "N's prev should be o, not " + nPrev;
        }

        public String descSetsNextToNull =
          "Removing the next sibling sets next back to null";
        public void testSetsNextToNull() {
          // remove n's next sibling
          n.removePrevSibling();

          Node<Integer> actual = n.getPrevSibling();
          assert actual == null
            : "Node n's previous sibling should be null, not " + actual;
        }

        public String descSetsNextsPrevToNull =
          "Also sets the Node that was next's previous Node to null";
        public void testSetsNextsPrevToNull() {
          Node<Integer> actual = o.getNextSibling();
          assert actual == null
            : "Node o's next sibling should be null, not " + actual;
        }
      }
    }
  }

  public String descKnowsItsParent = "A Node knows its parent Node";
  public void testKnowsItsParent() {
    Node<Integer> n = new Node<Integer>(1);
    Node<Integer> o = new Node<Integer>(2).addParent(n);

    assert o.getParent() == n;
  }

  public class ChildNodes extends Group {
    public String desc = "A Node knows its child Nodes";

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
