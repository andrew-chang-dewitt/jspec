package jspec.cli;

public class InvalidArgumentError extends Exception {
  public InvalidArgumentError(String flag) {
    super("Invalid argument: " + flag);
  }

  public InvalidArgumentError(String flag, String value) {
    super("Invalid argument for " + flag + ": " + value);
  }

  public InvalidArgumentError(String flag, String value, String msg) {
    super("Invalid argument for " + flag + ": " + value + ". " + msg);
  }
}
