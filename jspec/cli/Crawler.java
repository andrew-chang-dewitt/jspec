package jspec.cli;

import java.lang.Override;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

public class Crawler extends SimpleFileVisitor<Path> {
  private File start;
  private PathMatcher pattern;
  private CrawlHandler handler;
  private CrawlExcHandler excHandler;

  public Crawler(File start, PathMatcher pattern) {
    this.start = start;
    this.pattern = pattern;
  }

  public Crawler crawl (
    CrawlHandler handler,
    CrawlExcHandler excHandler
  ) throws IOException {
    this.handler = handler;
    this.excHandler = excHandler;
    Files.walkFileTree(this.start.toPath(), this);

    return this;
  }

  @Override
  public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
    if (this.pattern.matches(file)) {
      this.handler.handle(file);
    }
    return FileVisitResult.CONTINUE;
  }

  @Override
  public FileVisitResult visitFileFailed(Path file, IOException exc) {
    this.excHandler.handle(file, exc);
    return FileVisitResult.CONTINUE;
  }
}
