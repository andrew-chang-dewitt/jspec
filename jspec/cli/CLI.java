package jspec.cli;

import jspec.lib.Runner;

import jspec.lib.GroupSpec;
import jspec.lib.ResultSpec;
import jspec.lib.RunnerSpec;
import jspec.utils.NodeSpec;
import jspec.utils.tree.TreeSpec;
import jspec.utils.list.DoublyLinkedListSpec;

public class CLI {
  Runner runner;

  public static void main(String[] args) {
    new CLI().runner
      .addGroup(new GroupSpec())
      .addGroup(new ResultSpec())
      .addGroup(new RunnerSpec())
      .addGroup(new NodeSpec())
      .addGroup(new TreeSpec())
      .addGroup(new DoublyLinkedListSpec())
      .run()
      .resultStrings()
      .forEach((n, i) -> System.out.println(n.getValue()));
  }

  CLI() {
    this.runner = new Runner();
  }
}
