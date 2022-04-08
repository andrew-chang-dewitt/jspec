package jspec.utils;

public interface MapConsumer<T, U> {
  public U accept(Node<T> current, int n);
}
