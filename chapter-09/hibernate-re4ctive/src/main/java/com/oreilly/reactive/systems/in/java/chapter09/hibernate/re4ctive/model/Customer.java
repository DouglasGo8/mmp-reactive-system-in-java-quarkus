package com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends PanacheEntity {
  @Column(nullable = false)
  @NotBlank(message = "Customer name can not be blank")
  @Length(min = 3, message = "Customer names must be at least three characters")
  public String name;

  @Transient
  public List<Order> orders;

}
