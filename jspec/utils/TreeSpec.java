package jspec.utils;

import jspec.lib.Group;

import java.util.Iterator;

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
  // public String descTraverse = "A tree can be traversed";
  // public void testTraverse() {
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

  //   /* Preorder traversal should return Traversed list:
  //    * 1 -> 2 -> 3 -> 5 -> 4
  //    */
  // }

  public String descReduce = "A tree can be reduced";
  public void testReduce() {
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

    int result = tree.reduce(
      (accumulator, current, depth) -> accumulator + current.getValue()
      , 0);
    int expected = 15;

    assert result == expected
      : result + " != " + expected;
  }

  public String descReduceCbKnowsDepth = "Reduce correctly gives a node's depth to callback";
  public void testReduceCbKnowsDepth() {
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

    /* reduce tree to the depth of the deepest leaf
     *
     *       1     // depth => 0
     *      /|\
     *     2 3 4   // depth => 1
     *       |
     *       5     // depth => 2
     */
    int result = tree.reduce(
      (accumulator, current, depth) ->
        accumulator < depth ? depth : accumulator
      , 0);
    int expected = 2;

    assert result == expected
      : result + " != " + expected;
  }

  public String descMapBuildsANewTree = "Map returns a new Tree with modified elements in place of the old ones in the original Tree";
  public void testMapBuildsANewTree() {
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

    /* Add one to every node & return new tree
     *
     *       2
     *      /|\
     *     3 4 5
     *       |
     *       6
     */
    Tree<Integer> result = tree.map(
      (node, depth) -> node.getValue() + 1);

    Iterator<Integer> iter = result.iterator();

    int actual = iter.next();
    assert actual == 2 : "first node should be 2, not " + actual;
    actual = iter.next();
    assert actual == 3 : "second node should be 3, not " + actual;
    actual = iter.next();
    assert actual == 4 : "third node should be 4, not " + actual;
    actual = iter.next();
    assert actual == 6 : "fourth node should be 6, not " + actual;
    actual = iter.next();
    assert actual == 5 : "fifth node should be 5, not " + actual;
  }
}

class TreeFactory {
  public static Tree<String> create() {
    Node<String> node = new Node<String>("root");
    return new Tree<String>(node);
  }
}
