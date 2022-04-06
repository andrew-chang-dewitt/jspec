package jspec.utils;

import jspec.lib.Group;

public class TreeSpec extends Group {
  final String desc = "utils.Node";

  public static void main(String[] args) {
    TreeSpec spec = new TreeSpec();

    spec
      .visit()
      .getResults()
      .forEach(result -> {
        System.out.print(result.getDescription());
        System.out.print(" ");

        if (result.didPass()) {
          System.out.print("✅\n");
        } else {
          System.out.print("❌\n");
          System.out.println();
          result.getFailureExc().getCause().printStackTrace();
          System.out.println();
        }
      });
  }

  public String descCreateTree = "Creating a tree adds given node as root";
  public void testCreateTree() {
    Node<Integer> node = new Node<Integer>(1);
    Tree<Integer> tree = new Tree<Integer>(node);

    assert tree.getRoot() == node;
  }

  public String descAppendChild = "Can append a child to a tree";
  public void testAppendChild() {
    Node<String> root = new Node<String>("root");
    Tree<String> tree = new Tree<String>(root);

    Node<String> aChild = new Node<String>("first child");
    tree.appendChild(aChild);

    Node<String> head = tree.getRoot().getHeadChild();
    Node<String> tail = tree.getRoot().getTailChild();

    assert head == aChild
      : head.getValue() + " != " + aChild.getValue();
    assert tail == aChild
      : tail.getValue() + " != " + aChild.getValue();
    assert root == aChild.getParent()
      : root.getValue() + " != " + aChild.getParent().getValue();

    Node<String> newChild = new Node<String>("second child");
    tree.appendChild(newChild);

    head = tree.getRoot().getHeadChild();
    tail = tree.getRoot().getTailChild();

    assert head == aChild
      : head.getValue() + " != " + aChild.getValue();
    assert tail == newChild
      : tail.getValue() + " != " + newChild.getValue();
    assert root == newChild.getParent()
      : root.getValue() + " != " + newChild.getParent().getValue();
  }

  public String descPrependChild = "Can prepend a child to a tree";
  public void testPrependChild() {
    Node<String> root = new Node<String>("root");
    Tree<String> tree = new Tree<String>(root);

    Node<String> aChild = new Node<String>("first child");
    tree.prependChild(aChild);

    Node<String> head = tree.getRoot().getHeadChild();
    Node<String> tail = tree.getRoot().getTailChild();

    assert head == aChild
      : head.getValue() + " != " + aChild.getValue();
    assert tail == aChild
      : tail.getValue() + " != " + aChild.getValue();
    assert root == aChild.getParent()
      : root.getValue() + " != " + aChild.getParent().getValue();

    Node<String> newChild = new Node<String>("second child");
    tree.prependChild(newChild);

    head = tree.getRoot().getHeadChild();
    tail = tree.getRoot().getTailChild();

    assert head == newChild
      : head.getValue() + " != " + aChild.getValue();
    assert tail == aChild
      : tail.getValue() + " != " + newChild.getValue();
    assert root == newChild.getParent()
      : root.getValue() + " != " + newChild.getParent().getValue();
  }

  public String descAllChildren = "Can get doubly linked list of all children";
  public void testAllChildren() {
    Tree<String> tree = TreeFactory.create();
    Node<String> one = new Node<String>("one");
    Node<String> two = new Node<String>("two");
    Node<String> three = new Node<String>("three");

    tree.appendChild(one);
    tree.appendChild(two);
    tree.appendChild(three);

    assert tree.getChildren() instanceof DoublyLinkedList<?>
      : tree.getChildren() + " should be a Doubly Linked List";
    assert tree.getChildren().getHead() == one;
    assert tree.getChildren().getTail() == three;
    assert tree.getChildren().contains(one);
    assert tree.getChildren().contains(two);
    assert tree.getChildren().contains(three);
  }

  // FIXME: this works as defined below, but is missing features:
  // 1. needs inversion of control
  // 2. needs to be aware of depth of current node during inversion 
  //    (in callback)
  // 3. ideally will be something that can be passed to a reduce 
  //    method and a forEach method on Tree => probably means traversal
  //    functionality needs split off into own interface/class?
  public String descTraverse = "A tree can be traversed";
  public void testTraverse() {
    /* Build a tree that looks like this:
     *
     *          1
     *         /|\
     *        2 3 4
     *          |
     *          5
     */
    Node<Integer> a = new Node<Integer>(1);
    Tree<Integer> tree = new Tree<Integer>(a);
    Node<Integer> b = new Node<Integer>(2);
    Node<Integer> c = new Node<Integer>(3);
    Node<Integer> d = new Node<Integer>(4);

    tree.appendChild(b);
    tree.appendChild(c);
    tree.appendChild(d);

    Tree<Integer> ctree = new Tree<Integer>(c);
    Node<Integer> e = new Node<Integer>(5);

    ctree.appendChild(e);

    /* Preorder traversal should return Traversed list:
     * 1 -> 2 -> 3 -> 5 -> 4
     */
    DoublyLinkedList<Node<> traversed = tree.preorder();

    assert traversed.get(0).getValue().getValue() == 1;
    assert traversed.get(0).getValue().getDepth() == 1;
    assert traversed.get(1).getValue().getValue() == 2;
    assert traversed.get(1).getValue().getDepth() == 2;
    assert traversed.get(2).getValue().getValue() == 3;
    assert traversed.get(2).getValue().getDepth() == 3;
    assert traversed.get(3).getValue().getValue() == 5;
    assert traversed.get(3).getValue().getDepth() == 5;
    assert traversed.get(4).getValue().getValue() == 4;
    assert traversed.get(4).getValue().getDepth() == 4;
  }

  // public String descReduce = "A tree can be reduced";
  // public void testReduce() {
  //   /* Build a tree that looks like this:
  //    *
  //    *          1
  //    *         /|\
  //    *        2 3 4
  //    *          |
  //    *          5
  //    */
  //   Node<Integer> a = new Node<Integer>(1);
  //   Tree<Integer> tree = new Tree<Integer>(a);
  //   Node<Integer> b = new Node<Integer>(2);
  //   Node<Integer> c = new Node<Integer>(3);
  //   Node<Integer> d = new Node<Integer>(4);

  //   tree.appendChild(b);
  //   tree.appendChild(c);
  //   tree.appendChild(d);

  //   Tree<Integer> ctree = new Tree<Integer>(c);
  //   Node<Integer> e = new Node<Integer>(5);

  //   ctree.appendChild(e);

  //   /* reduce uses a preorder traversal & 
  //    * a callback that sums the values of each node,
  //    * so it should do the following
  //    *   0       // initial value
  //    *   0 + 1   // add a
  //    *   1 + 2   // add b
  //    *   3 + 3   // add c
  //    *   6 + 5   // add e
  //    *   11 + 4  // add d
  //    *   15      // return total
  //    */
  //   int result = tree.reduce(
  //     (accumulator, current, depth) -> {
  //       System.out.println("current accumulator value " + accumulator);
  //       System.out.println("current node's value " + current.getValue());
  //       System.out.println("current depth " + depth);
  //       return accumulator + current.getValue();
  //     }
  //       , 0);
  //   int expected = 15;

  //   assert result == expected
  //     : result + " != " + expected;
  // }
}

class TreeFactory {
  public static Tree<String> create() {
    Node<String> node = new Node<String>("root");
    return new Tree<String>(node);
  }
}
