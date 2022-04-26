/*
 * An example showing nested test Groups
 *
 */

package jspec.example;

import jspec.lib.Group;
import jspec.lib.Runner;

public class PrefixerSpec extends Group {
  public String desc = "class: Prefixer";

  public static void main(String[] args) {
    new Runner(new PrefixerSpec())
      .run(false)
      .resultStrings()
      .forEach((node, i) -> System.out.println(node.getValue()));
  }

  // logic specific to a subset of tests can be encapsulated by
  // group the tests in an inner class that extends Group
  public class Indent extends Group {
    public String desc = "method: indent(int n)";

    Prefixer prefixer;

    // setup code for shared state for all methods on this sub-Group
    public void before() {
      this.prefixer = new Prefixer("test");
    }

    public String descTwoSpaces = "Can indent by two spaces";
    public void testTwoSpaces() {
      String actual = this.prefixer.indent(2);
      assert actual.startsWith("  t")
        : "Should start with two spaces, instead got " + actual;
    }
  }

  public class Bullet extends Group {
    public String desc = "method: bullet(char c)";

    Prefixer prefixer;

    public void before() {
      this.prefixer = new Prefixer("test");
    }

    public String descGiven = "Can prefix with given character as bullet point.";
    public void testGiven() {
      String actual = this.prefixer.bullet('-');
      assert actual.startsWith("- t")
        : "Should start with '- ', instead got " + actual;
    }
  }
}
