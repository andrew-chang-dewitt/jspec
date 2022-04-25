package jspec.utils.list;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.lang.IndexOutOfBoundsException;

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

  public Node<T> get(int index)
    throws
      NoSuchElementException,
      IndexOutOfBoundsException
  {
    if (index < 0) throw new IndexOutOfBoundsException();

    Node<T> result = this.reduce((a, x, i) -> {
      if (i == index) return x;
      else return a;
    }, null);

    if (result == null) throw new NoSuchElementException();

    return result;
  }

  public Node<T> delete(int index) {
    return this.reduce((deleted, node, idx) -> {
      // if the node is at the given index, then delete it
      if (idx == index) {
        Node<T> prev = node.getPrevSibling();
        Node<T> next = node.getNextSibling();

        if (prev != null) {
          if (next != null) {
            // the previous node's next should now be the next node
            prev.addNextSibling(next);
            // & vice-versa
            next.addPrevSibling(prev);
          } else {
            // node was the tail
            // so the previous node's next should now be null
            prev.removeNextSibling();
            // and the previous node should be the tail
            this.tail = prev;
          }
        } else {
          // node was the head, next should now be the head
          this.head = next;
        }

        deleted = node;
      }

      return deleted;
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

  public DoublyLinkedList<T> concat(DoublyLinkedList<T> other) {
    this.tail.addNextSibling(other.head);
    this.tail = other.tail;

    return this;
  }
}

class DLLIterator<T> implements Iterator<T> {
  Node<T> nextNode;

  DLLIterator(DoublyLinkedList<T> list) {
    this.nextNode = list.getHead();
  }

  public T next() {
    if (this.hasNext()) {
      Node<T> current = this.nextNode;
      this.nextNode = current.getNextSibling();

      return current.getValue();
    } else {
      throw new NoSuchElementException();
    }
  }

  public boolean hasNext() {
    return !(this.nextNode == null);
  }
}
