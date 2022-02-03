package com.oreilly.reactive.systems.in.java.chapter10.database;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class Person extends PanacheEntity {

  @Column(unique = true)
  public String name;

  public int age;
}
