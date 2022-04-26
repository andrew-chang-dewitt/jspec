/*
 * Simplest example use
 *
 * Create a class & a corresponding *Spec class that
 * extends jspec's Group, then initialize it and add
 * it to a Runner for evaluation in the main() method.
 */

package jspec.example;

import jspec.lib.Group;
import jspec.lib.Runner;

public class MathSpec extends Group {
  // you can give a more human-readable description to the test Group
  public String desc = "class: Math";

  public static void main(String[] args) {
    // main should initialize a runner, then add this Spec class to it
    new Runner(new MathSpec())
      // call run to evalutate the tests in this this Spec class
      .run(false)
      // then get the results
      .resultStrings()
      // and print them
      .forEach((node, i) -> System.out.println(node.getValue()));
  }

  // you can give a more human-readable description to the test method
  public String descSumTwo = "Can sum two numbers";
  public void testSumTwo() {
    int actual = Math.sum(1,2);
    assert actual == 3 : "Summing 1 & 2 should return 3, instead got " + actual;
  }

  // public String descSumThree = "Can sum three numbers";
  // public void testSumThree() {
  //   int actual = Math.sum(1,2,3);
  //   assert actual == 6 : "Summing 1, 2, & 3 should return 6, instead got " + actual;
  // }
}
