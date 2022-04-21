package jspec.lib;

import java.text.DecimalFormat;
import jspec.utils.Node;
import jspec.utils.list.DoublyLinkedList;

public class Runner {
  private String indent = "  ";

  private DoublyLinkedList<Group> groups;
  private ResultsTree results;
  private int totalTests;
  private int failedTests;

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

  public Runner run(boolean silent) {
    // start a tree w/ just the root node to add Results to
    Result root = new Result("");
    this.results = new ResultsTree(root);

    // Pad w/ empty line before
    if (!silent) System.out.println();
    // build results tree up from each group in Runner
    this.groups.forEach(
      (grp, idx) -> this.buildResults(this.results, grp.getValue(), silent));

    return this;
  }

  private ResultsTree buildResults(
    ResultsTree tree,
    Group group,
    boolean silent
  ) {
    // Add this Group as a Result child to the tree
    String groupDesc = null;

    try {
      groupDesc = (String)group.getClass().getField("desc").get(group);
    } catch (IllegalAccessException exc) {
      System.err.println("Warning: the `desc` field on a test Group must be public for it to be used by the test Runner.");
      // do nothing, leave desc as null
    } catch (NoSuchFieldException exc) {
      // do nothing, leave desc as null
    }

    String groupName = groupDesc != null
      ? groupDesc
      : group.getClass().getName();
    Node<Result> currNode = new Node<Result>(new Result(groupName));
    tree.appendChild(currNode);
    // visit Group to execute tests & discover children
    VisitResults visited = group.visit(silent);
    // make new tree w/ child as root
    ResultsTree childTree = new ResultsTree(currNode);
    // add each result as child to tree
    visited.getResults().forEach((res, idx) -> childTree.appendChild(res));
    // recur on current group's list of children
    visited.getChildren().forEach(
      (grp, idx) -> this.buildResults(childTree, grp.getValue(), silent));

    return tree;
  }

  public DoublyLinkedList<String> resultStrings() {
    return this.resultStrings(false);
  }

  public DoublyLinkedList<String> resultStrings(boolean concise) {
    ResultsStringParts res = this.results.reduce(
      (parts, node, depth) -> {
        Result result = node.getValue();

        // pad groups with an empty line before them
        if (!result.isTest()) {
          // as long as the output is verbose
          if (!concise) parts.statuses.append("");
        }
        // otherwise increment the test counter
        else ++this.totalTests;

        if (!concise) {
          // add the result's status string to the statuses list
          parts.statuses.append(
              result.statusString(this.indent.repeat(depth)));
        }

        try {
          if (!result.didPass()) {
            // if failure, add result's failure strings to failures list
            result
              .failureStrings()
              .forEach((fnode, i) -> parts.failures.append(fnode.getValue()));
            // and increment the failure counter
            ++this.failedTests;
          }
        } catch (NotATestResult exc) {
          // do nothing because the result is a group result & can't
          // have a failure to report
        }

        // return the updated list
        return parts;
      },
      // start with empty statuses & failures lists
      new ResultsStringParts());

    res.statistics = this.statisticsStrings();

    return res.compose();
  }

  DoublyLinkedList<String> statisticsStrings() {
    int passed = this.totalTests - failedTests;

    String stats = passed + "/" + this.totalTests + " tests passed";

    DecimalFormat df = new DecimalFormat("###.#%");
    String percPassed = df.format(
      Integer.valueOf(passed).floatValue()
      / Integer.valueOf(this.totalTests).floatValue());

    if (passed == this.totalTests)
      stats += "!";
    else
      stats += " (" + percPassed + ")";

    return new DoublyLinkedList<String>()
      .append("")
      .append("=".repeat(80))
      .append("")
      .append(stats)
      .append("");
  }
}

class ResultsStringParts {
  DoublyLinkedList<String> statuses;
  DoublyLinkedList<String> failures;
  DoublyLinkedList<String> statistics;

  ResultsStringParts() {
    this.statuses = new DoublyLinkedList<String>().append("");
    this.failures = new DoublyLinkedList<String>();
    this.statistics = new DoublyLinkedList<String>();
  }

  DoublyLinkedList<String> compose() {
    if (this.failures.getLength() > 0)
      return this.statuses.concat(this.failures).concat(this.statistics);

    return this.statuses.concat(this.statistics);
  }
}
