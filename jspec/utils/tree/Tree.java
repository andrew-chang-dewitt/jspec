package jspec.utils.tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import jspec.utils.FindPredicate;
import jspec.utils.ForEachConsumer;
import jspec.utils.MapConsumer;
import jspec.utils.Node;
import jspec.utils.ReduceConsumer;
import jspec.utils.ValueNotFound;
import jspec.utils.list.DoublyLinkedList;

public class Tree<T> implements Iterable<T> {
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

  public <U> U reduce(ReduceConsumer<T, U> action, U initialValue) {
    // return PreorderTraverser.reduce(this, action, initialValue);
    return this.reducer(action, initialValue, 0);
  }

  <U> U reducer(ReduceConsumer<T, U> action, U accumulator, int depth) {
    U updated = action.accept(accumulator, this.root, depth);

    return this.getChildren().reduce(
      (a, x, i) -> new Tree<T>(x).reducer(action, a, depth + 1)
      , updated);
  }

  public void forEach(ForEachConsumer<T> action) {
    this.reduce((a, current, depth) -> {
      action.accept(current, depth);

      return null;
    }, null);
  }

  public <U> Tree<U> map(MapConsumer<T, U> action) {
    class Tracker<V> {
      Stack<Node<V>> parentNodes;
      Node<V> previousNode;
      int previousDepth;
      Tree<V> tree;

      Tracker() {
        this.parentNodes = new Stack<Node<V>>();
        this.previousNode = null;
        this.previousDepth = 0;
        this.tree = null;
      }
    }

    Tracker<U> reduced = this.reduce(
      (accum, currt, depth) -> {
        Node<U> newNode = new Node<U>(action.accept(currt, depth));

        if (accum.previousNode == null) {
          accum.parentNodes.push(newNode);
          accum.previousNode = newNode;
          accum.tree = new Tree<U>(newNode);
        } else if (depth > accum.previousDepth) {
          accum.previousDepth = depth;
          accum.parentNodes.push(accum.previousNode);
          new Tree<U>(accum.previousNode)
            .appendChild(newNode);
        } else if (depth == accum.previousDepth) {
          new Tree<U>(accum.parentNodes.peek())
            .appendChild(newNode);
        } else if (depth < accum.previousDepth) {
          for (int i = 0; i < ( accum.previousDepth - depth ); i++) {
            accum.parentNodes.pop();
          }
          new Tree<U>(accum.parentNodes.peek())
            .appendChild(newNode);
        }

        return accum;
      }, new Tracker<U>());

    return reduced.tree;
  }

  public Node<T> find(T value) throws ValueNotFound {
     Node<T> result = this.reduce(
      (found, current, d) -> {
        if (found != null) return found;
        T currentValue = current.getValue();

        return currentValue == value ? current : found;
      }, null);

     if (result == null) throw new ValueNotFound(value, this);
     return result;
  }

  public Node<T> find(FindPredicate<T> predicate) throws ValueNotFound {
     Node<T> result = this.reduce(
      (found, current, d) -> {
        if (found != null) return found;

        return predicate.check(current) ? current : found;
      }, null);

     if (result == null) throw new ValueNotFound(predicate, this);
     return result;
  }

  public boolean contains(T value) {
    try {
      this.find(value);
      return true;
    } catch (ValueNotFound exc) {
      return false;
    }
  }

  public boolean contains(FindPredicate<T> predicate) {
    try {
      this.find(predicate);
      return true;
    } catch (ValueNotFound exc) {
      return false;
    }
  }

  public Iterator<T> iterator() {
    return this
      .reduce(
        (list, node, d) -> {
          list.add(node.getValue());
          return list;
        }, new ArrayList<T>())
      .iterator();
  }
}
