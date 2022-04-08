package jspec.utils;

public interface ReduceConsumer<T, U> {
  public U accept(U accumulator, Node<T> current, int n);
}
