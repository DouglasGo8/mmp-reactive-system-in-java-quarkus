package com.oreilly.reactive.systems.in.java.chapter08.munity.integration.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Quote {
  private final String company;
  private final double value;
}
