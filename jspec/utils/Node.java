package jspec.utils;

public class Node<T> {
  private T value;
  private Node<T> nextSibling;
  private Node<T> prevSibling;
  private Node<T> parent;
  private Node<T> headChild;
  private Node<T> tailChild;

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

  public Node<T> addParent(Node<T> parent) {
    this.parent = parent;

    return this;
  }

  public Node<T> getParent() {
    return this.parent;
  }

  public Node<T> addHeadChild(Node<T> headChild) {
    this.headChild = headChild;

    return this;
  }

  public Node<T> getHeadChild() {
    return this.headChild;
  }

  public Node<T> addTailChild(Node<T> tailChild) {
    this.tailChild = tailChild;

    return this;
  }

  public Node<T> getTailChild() {
    return this.tailChild;
  }
}
