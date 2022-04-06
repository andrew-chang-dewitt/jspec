package jspec.utils;

public class Tree<T> {
  Node<T> root;

  public Tree(Node<T> root) {
    this.root = root;
  }

  public Node<T> getRoot() {
    return this.root;
  }

  public Tree<T> appendChild(Node<T> node) {
    Node<T> tail = this.root.getTailChild();

    if (tail != null) {
      tail.addNextSibling(node);
    } else {
      this.root.addHeadChild(node);
    }

    this.root.addTailChild(node);
    node.addParent(this.root);

    return this;
  }

  public Tree<T> prependChild(Node<T> node) {
    Node<T> head = this.root.getHeadChild();

    if (head != null) {
      head.addPrevSibling(node);
    } else {
      this.root.addTailChild(node);
    }

    this.root.addHeadChild(node);
    node.addParent(this.root);

    return this;
  }

  public DoublyLinkedList<T> getChildren() {
    return new DoublyLinkedList<T>(
      this.root.getHeadChild(),
      this.root.getTailChild());
  }

  public Traversed<T> preorder() {
    return this.traverser(0, this.root, new Traversed<T>());
  }

  private Traversed<T> traverser(
    int depth,
    Node<T> currentNode, 
    Traversed<T> traversal
  ) {
    traversal.append(currentNode.getValue(), depth);

    new Tree<T>(currentNode)
      .getChildren()
      .forEach((child, idx) -> traverser(depth + 1, child, traversal));

    return traversal;
  }

  public <U> U reduce(TreeReduceCallback<T, U> cb, U initialValue) {
    return this.reducer(cb, initialValue, this.root, 0);
  }

  private <U> U reducer(TreeReduceCallback<T, U> cb, U currentValue, Node<T> currentNode, int depth) {
    if (currentNode == null) return currentValue;

    Node<T> headChild = currentNode.getHeadChild();
    boolean hasHeadChild = headChild != null;

    return reducer(
      cb,
      cb.cb(currentValue, currentNode, depth),
      hasHeadChild ? headChild : currentNode.getNextSibling(),
      hasHeadChild ? ++depth : depth
    );
  }
}

class TraversedData<T> {
  T value;
  int depth;

  public TraversedData(T value, int depth) {
    this.value = value;
    this.depth = depth;
  }

  public T getValue() {
    return this.value;
  }

  public int getDepth() {
    return this.depth;
  }
}

class Traversed<T> extends DoublyLinkedList<TraversedData<T>> {
  public void append(T value, int depth) {
    this.append(
      new Node<TraversedData<T>>(
        new TraversedData<T>(value, depth)));
  }
}

interface TreeReduceCallback<T, U> {
  public U cb(U accumulator, Node<T> current, int depth);
}

interface TreeForEachCallback<T> {
  public void cb(Node<T> current, int depth);
}
