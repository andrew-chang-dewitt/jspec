package jspec.cli;

import java.nio.file.Path;

public interface CrawlExcHandler {
  public void handle(Path file, Exception exc);
}
