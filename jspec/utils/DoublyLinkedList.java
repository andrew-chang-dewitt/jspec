package jspec.utils;

public class DoublyLinkedList<T> {
  Node<T> head;
  Node<T> tail;

  public DoublyLinkedList(Node<T> node) {
    this.head = node;
    this.tail = node;
  }

  public DoublyLinkedList(Node<T> head, Node<T> tail) {
    this.head = head;
    this.tail = tail;
  }

  public Node<T> getHead() {
    return this.head;
  }

  public Node<T> getTail() {
    return this.tail;
  }

  public int getLength() {
    return this.reduce((a, x, i) -> {
      return (int) a + 1;
    }, 0);
  }

  public <U> U reduce(ReduceCallback<T, U> cb, U initialValue) {
    return this.reducer(cb, initialValue, this.head, 0);
  }

  private <U> U reducer(ReduceCallback<T, U> cb, U currentValue, Node<T> currentNode, int currentIndex) {
    if (currentNode == null) return currentValue;
    
    return reducer(
      cb, 
      cb.cb(currentValue, currentNode, currentIndex), 
      currentNode.getNextSibling(), 
      currentIndex++);
  }

  public void forEach(ForEachCallback<T> cb, boolean reverse) {
    int index = 0;
    Node<T> current;

    if (reverse) {
      current = this.tail;
    } else {
      current = this.head;
    }

    while (current != null) {
      cb.cb(current, index);

      index++;
      if (reverse) {
        current = current.getPrevSibling();
      } else {
        current = current.getNextSibling();
      }
    }
  }

  public void forEach(ForEachCallback<T> cb) {
    this.forEach(cb, false);
  }

  public void append(Node<T> node) {
    this.tail.addNextSibling(node);
    this.tail = node;
  }

  public void prepend(Node<T> node) {
    this.head.addPrevSibling(node);
    this.head = node;
  }
}

interface ReduceCallback<T, U> {
  public U cb(U accumulator, Node<T> x, int index);
}

interface ForEachCallback<T> {
  public void cb(Node<T> x, int index);
}
