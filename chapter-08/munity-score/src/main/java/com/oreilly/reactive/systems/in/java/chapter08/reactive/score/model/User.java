package com.oreilly.reactive.systems.in.java.chapter08.reactive.score.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class User {
  private final String name;

  @JsonCreator
  public User(@JsonProperty("name") String name) {
    this.name = name;
  }
}
