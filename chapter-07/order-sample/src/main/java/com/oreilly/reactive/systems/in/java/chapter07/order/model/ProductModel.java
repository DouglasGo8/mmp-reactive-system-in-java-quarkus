package com.oreilly.reactive.systems.in.java.chapter07.order.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductModel {
  private final String name;

  public static String capitalizeAllFirstLetter(String name) {
    char[] array = name.toCharArray();
    array[0] = Character.toUpperCase(array[0]);

    for (int i = 1; i < array.length; i++) {
      if (Character.isWhitespace(array[i - 1])) {
        array[i] = Character.toUpperCase(array[i]);
      }
    }

    return new String(array);
  }
}
