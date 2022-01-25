package com.oreilly.reactive.systems.in.java.chapter08.munity.integration.model;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class Book {
  private final long id;
  private final String title;
  private final List<String> authors;
}
