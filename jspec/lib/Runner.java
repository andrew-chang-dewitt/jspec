package jspec.lib;

import jspec.utils.Node;
import jspec.utils.list.DoublyLinkedList;

public class Runner {
  private DoublyLinkedList<Group> groups;
  private ResultsTree results;

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

  // TODO: Test this
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
    Node<Result> currNode = new Node<Result>(new Result(group.desc));
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
}
