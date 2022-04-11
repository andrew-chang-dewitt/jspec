package jspec.utils.list;

import java.util.Iterator;
import jspec.utils.Node;
import jspec.utils.ForEachConsumer;
import jspec.utils.MapConsumer;
import jspec.utils.ReduceConsumer;

public class DoublyLinkedList<T> implements Iterable<T> {
  Node<T> head;
  Node<T> tail;

  public DoublyLinkedList() {
    this.head = null;
    this.tail = null;
  }

  public DoublyLinkedList(Node<T> node) {
    this.head = node;
    this.tail = node;
  }

  public DoublyLinkedList(Node<T> head, Node<T> tail) {
    this.head = head;
    this.tail = tail;
  }

  public static <T> DoublyLinkedList<T> fromArray(T[] arr) {
    DoublyLinkedList<T> newList = new DoublyLinkedList<T>();

    for (T t: arr) {
      newList.append(t);
    }

    return newList;
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

  public DoublyLinkedList<T> append(T value) {
    return this.append(new Node<T>(value));
  }

  public DoublyLinkedList<T> append(Node<T> node) {
    if (this.tail != null) {
      this.tail.addNextSibling(node);
    } else {
      this.head = node;
    }

    this.tail = node;

    return this;
  }

  public DoublyLinkedList<T> prepend(T value) {
    return this.prepend(new Node<T>(value));
  }

  public DoublyLinkedList<T> prepend(Node<T> node) {
    if (this.head != null) {
      this.head.addPrevSibling(node);
    } else {
      this.tail = node;
    }

    this.head = node;

    return this;
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

  public <U> U reduce(ReduceConsumer<T, U> action, U initialValue) {
    return this.reducer(action, initialValue, this.head, 0);
  }

  private <U> U reducer(
    ReduceConsumer<T, U> action,
    U currentValue,
    Node<T> currentNode,
    int currentIndex
  ) {
    if (currentNode == null) return currentValue;

    return reducer(
      action,
      action.accept(currentValue, currentNode, currentIndex),
      currentNode.getNextSibling(),
      ++currentIndex);
  }

  public <U> DoublyLinkedList<U> map(MapConsumer<T, U> action) {
    return this.reduce(
      (list, node, idx) ->
        list.append(action.accept(node, idx)),
      new DoublyLinkedList<U>());
  }

  public void forEach(ForEachConsumer<T> action, boolean reverse) {
    int index = 0;
    Node<T> current;

    if (reverse) {
      current = this.tail;
    } else {
      current = this.head;
    }

    while (current != null) {
      action.accept(current, index);

      index++;
      if (reverse) {
        current = current.getPrevSibling();
      } else {
        current = current.getNextSibling();
      }
    }
  }

  public void forEach(ForEachConsumer<T> action) {
    this.forEach(action, false);
  }

  public boolean contains(Node<T> node) {
    return this.reduce((acc, current, idx) -> {
      return acc || current == node;
    }, false);
  }

  public boolean contains(T value) {
    return this.reduce((acc, current, idx) -> {
      return acc || current.getValue() == value;
    }, false);
  }

  public Iterator<T> iterator() {
    return new DLLIterator<T>(this);
  }
}

class DLLIterator<T> implements Iterator<T> {
  Node<T> currentNode;

  DLLIterator(DoublyLinkedList<T> list) {
    this.currentNode = list.getHead();
  }

  public T next() {
    T result = currentNode.getValue();
    this.currentNode = this.currentNode.getNextSibling();

    return result;
  }

  public boolean hasNext() {
    return !(this.currentNode.getNextSibling() == null);
  }
}
