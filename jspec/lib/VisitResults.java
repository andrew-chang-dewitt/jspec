package jspec.lib;

import jspec.utils.list.DoublyLinkedList;

public class VisitResults {
  private DoublyLinkedList<Group> children;
  private DoublyLinkedList<Result> results;

  VisitResults(DoublyLinkedList<Result> results, DoublyLinkedList<Group> children) {
    this.children = children;
    this.results = results;
  }

  public DoublyLinkedList<Group> getChildren() {
    return this.children;
  }

  public DoublyLinkedList<Result> getResults() {
    return this.results;
  }
}
