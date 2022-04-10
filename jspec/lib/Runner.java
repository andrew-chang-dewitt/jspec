package jspec.lib;

import jspec.utils.Node;
import jspec.utils.list.DoublyLinkedList;

public class Runner {
  private String indent = "  ";

  private DoublyLinkedList<Group> groups;
  private ResultsTree results;

  public Runner() {
    this.groups = new DoublyLinkedList<Group>();
  }

  public Runner(Group ...groups) {
    Node<Group> head = new Node<Group>(groups[0]);
    Node<Group> tail = new Node<Group>(groups[groups.length - 1]);
    this.groups = new DoublyLinkedList<Group>(head, tail);
  }

  public Runner addGroup(Group group) {
    this.groups.append(group);

    return this;
  }

  public DoublyLinkedList<Group> getGroups() {
    return this.groups;
  }

  public ResultsTree getResults() {
    return this.results;
  }

  public Runner run() {
    // start a tree w/ just the root node to add Results to
    Result root = new Result("");
    this.results = new ResultsTree(root);

    // build results tree up from each group in Runner
    this.groups.forEach(
      (grp, idx) -> this.buildResults(this.results, grp.getValue()));

    return this;
  }

  private ResultsTree buildResults(
    ResultsTree tree,
    Group group
  ) {
    // Add this Group as a Result child to the tree
    Node<Result> currNode = new Node<Result>(new Result(group.getClass().getName()));
    tree.appendChild(currNode);
    // visit Group to execute tests & discover children
    VisitResults visited = group.visit();
    // make new tree w/ child as root
    ResultsTree childTree = new ResultsTree(currNode);
    // add each result as child to tree
    visited.getResults().forEach((res, idx) -> childTree.appendChild(res));
    // recur on current group's list of children
    visited.getChildren().forEach(
      (grp, idx) -> this.buildResults(childTree, grp.getValue()));

    return tree;
  }

  public DoublyLinkedList<String> resultStrings() {
    DoublyLinkedList<String> statuses = this.results.reduce(
      (list, node, depth) ->
        list.append(node
          .getValue()
          .statusString(this.indent.repeat(depth))), 
      new DoublyLinkedList<String>());

    return statuses;
  }
}

// class ResultsStringParts {
//   DoublyLinkedList<String> statuses;
//   DoublyLinkedList<String> failures;
// 
//   ResultsStringParts() {
//     this.statuses = new DoublyLinkedList<String>();
//     this.failures = new DoublyLinkedList<String>();
//   }
// 
//   DoublyLinkedList<String> compose() {
//     return this.statuses.concat(this.failures);
//   }
// }
