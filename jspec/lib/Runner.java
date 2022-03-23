package jspec.lib;

import java.util.Arrays;
import java.util.ArrayList;

public class Runner {
  private ArrayList<Group> groups;
  private ArrayList<Result> results;

  public Runner(Group ...groups) {
    this.groups = new ArrayList<Group>(Arrays.asList(groups));
  }

  public Runner addGroup(Group group) {
    this.groups.add(group);

    return this;
  }

  public ArrayList<Group> getGroups() {
    return this.groups;
  }

  public ArrayList<Result> run() {
    this.results = new ArrayList<Result>();
    groups.forEach(group -> this.results.addAll(group.visit()));

    return this.results;
  }
}
