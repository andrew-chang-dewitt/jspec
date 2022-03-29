package jspec.utils;

public class Node<T> {
  private T value;
  private Node<T> nextSibling;
  private Node<T> prevSibling;

  public Node(T value) {
    this.value = value;
  }

  public T getValue() {
    return this.value;
  }

  public Node<T> addNextSibling(Node<T> node) {
    this.nextSibling = node;

    if (node.getPrevSibling() != this) {
      node.addPrevSibling(this);
    }

    return this;
  }

  public Node<T> getNextSibling() {
    return this.nextSibling;
  }

  public Node<T> addPrevSibling(Node<T> node) {
    this.prevSibling = node;

    if (node.getNextSibling() != this) {
      node.addNextSibling(this);
    }

    return this;
  }

  public Node<T> getPrevSibling() {
    return this.prevSibling;
  }
}
