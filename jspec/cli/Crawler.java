package jspec.cli;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

import jspec.lib.Group;
import jspec.utils.list.DoublyLinkedList;

public class Crawler {
  private DoublyLinkedList<Group> groups;
  private PathMatcher pattern;

  public Crawler(String pattern) {
    this.pattern = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    this.groups = new DoublyLinkedList<Group>();
  }

  public Crawler crawl(File start) {
    // needs to do the following:
    // 1. walk the file tree starting at the given directory
    // 2. for each file, compare if matches pattern
    //   - if it does, compile the file, make sure the public
    //     class is a Group, and add it to groups
    //   - otherwise, do nothing with it
    }
}
