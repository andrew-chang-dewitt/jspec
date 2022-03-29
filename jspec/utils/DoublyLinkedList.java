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

  public Node<T> get(int index) {
    return this.reduce((a, x, i) -> {
      if (i == index) return x;
      else return a;
    }, null);
  }

  public Node<T> replace(Node<T> node, int index) {
    Node<T> replaced = this.get(index);
    Node<T> prev = replaced.getPrevSibling();
    Node<T> next = replaced.getNextSibling();

    if (prev == null) {
      this.head = node;
    } else {
      prev.addNextSibling(node);
      node.addPrevSibling(prev);
    }

    if (next == null){
      this.tail = node;
    } else {
      node.addNextSibling(next);
      next.addPrevSibling(node);
    }

    return replaced;
  }

  public Node<T> delete(int index) {
    Node<T> deleted = this.get(index);
    Node<T> prev = deleted.getPrevSibling();
    Node<T> next = deleted.getNextSibling();

    if (prev == null) {
      this.head = next;
    } else {
      prev.addNextSibling(next);
    }

    if (next == null){
      this.tail = prev;
    } else {
      next.addPrevSibling(prev);
    }

    return deleted;
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
      ++currentIndex);
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
