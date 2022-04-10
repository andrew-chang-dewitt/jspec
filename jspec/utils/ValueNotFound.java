 package jspec.utils;

 public class ValueNotFound extends Exception {
      public ValueNotFound(Object value, Object collection) {
         super(" Value " + value + " not found in collection " + collection);
     }
 }
