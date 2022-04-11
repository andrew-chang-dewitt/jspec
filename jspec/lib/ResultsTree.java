package jspec.lib;

import jspec.utils.Node;
import jspec.utils.tree.Tree;

public class ResultsTree extends Tree<Result> {
  ResultsTree(Node<Result> resultNode) {
    super(resultNode);
  }

  ResultsTree(Result result) {
    super(new Node<Result>(result));
  }
}
