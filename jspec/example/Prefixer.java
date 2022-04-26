package jspec.example;

public class Prefixer {
  String str;

  public Prefixer(String str) {
    this.str = str;
  }

  public String indent(int numSpaces) {
    return " ".repeat(numSpaces) + this.str;
  }

  public String bullet(char character) {
    return character + " " + this.str;
  }
}
