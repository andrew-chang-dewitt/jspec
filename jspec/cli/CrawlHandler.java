package jspec.cli;

import java.nio.file.Path;

public interface CrawlHandler {
  public void handle(Path file);
}
