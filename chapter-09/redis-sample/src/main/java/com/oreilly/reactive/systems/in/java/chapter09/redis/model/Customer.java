package com.oreilly.reactive.systems.in.java.chapter09.redis.model;

import io.vertx.mutiny.redis.client.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
  private Long id;
  private String name;

  public static Customer constructCustomer(long id, Response response) {
    return new Customer(id, response.get("name").toString());
  }
}
