package jspec.lib;

import java.util.ArrayList;

public class VisitResults {
  private ArrayList<Group> children;
  private ArrayList<Result> results;

  VisitResults(ArrayList<Result> results, ArrayList<Group> children) {
    this.children = children;
    this.results = results;
  }

  public ArrayList<Group> getChildren() {
    return this.children;
  }

  public ArrayList<Result> getResults() {
    return this.results;
  }
}
