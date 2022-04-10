package jspec.utils;

public interface FindPredicate<T> {
  public boolean check(Node<T> node);
}
