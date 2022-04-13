package jspec.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

public class ArgParser {
  private File outFile;
  private PathMatcher pattern;
  private boolean verbose;

  ArgParser() {
    this.outFile = null;
    this.verbose = false;
    this.setPattern("**/*Spec.java");
  }

  public File getOutFile() {
    return this.outFile;
  }

  public PathMatcher getPattern() {
    return this.pattern;
  }

  public boolean getVerbose() {
    return this.verbose;
  }

  private void setOutFile(String value) throws InvalidArgumentError {
    // create file object from value
    File file = new File(value);

    try {
      // try to create file, if it exists, will return false
      if (!file.createNewFile()) {
        // if file exists, check if it can be written to
        if (!file.canWrite()) {
          // if not, throw error
          throw new InvalidArgumentError("outFile", value, "Unable to write to existing file at given path.");
        }
      }
    } catch (IOException exc) {
      // if there was an error creating the file, throw new error
      try {
        // use initCause to link new error w/ original error for better stack trace
        throw new InvalidArgumentError("outFile", value, "Unable to create new file to save output to at given path.").initCause(exc);
      } catch (Throwable e) {
        // linking causes can throw error sometimes, if this one does, throw just
        // the invalid argument error w/out a cause
        throw new InvalidArgumentError("outFile", value, "Unable to create new file to save output to at given path.");
      }
    }

    this.outFile = file;
  }

  private void setPattern(String value) {
    this.pattern = FileSystems.getDefault().getPathMatcher("glob:" + value);
  }

  private void setVerbose(String value) throws InvalidArgumentError {
    switch (value) {
      case "true":
        this.verbose = true;
        break;
      case "false":
        this.verbose = false;
      default:
        throw new InvalidArgumentError("verbose", value);
    }
  }

  public ArgParser arg(String arg) throws InvalidArgumentError {
    String[] split = arg.split("=");

    String flag = split[0];
    String value;

    if (split.length == 2) value = split[1];
    else value = "true";

    if (flag.startsWith("--")) {
      // strip "--" & pass to routeFlag
      this.routeFlag(flag.substring(2), value);
      return this;
    }
    else if (flag.startsWith("-")) {
      // strip "-" & pass to routeFlag
      this.routeFlag(flag.substring(1), value);
      return this;
    }
    else throw new InvalidArgumentError(flag);
  }

  private void routeFlag(String flag, String value) throws InvalidArgumentError {
    switch (flag) {
      case "o":
      case "out":
      case "outFile":
        this.setOutFile(value);
        break;
      case "g":
      case "f":
      case "p":
      case "glob":
      case "file":
      case "fileName":
      case "pattern":
        this.setPattern(value);
        break;
      case "v":
      case "verbose":
        this.setVerbose(value);
        break;
      default:
        throw new InvalidArgumentError(flag);
    }
  }
}
