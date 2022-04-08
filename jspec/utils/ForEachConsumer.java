package jspec.utils;

public interface ForEachConsumer<T> {
  public void accept(Node<T> current, int depth);
}
