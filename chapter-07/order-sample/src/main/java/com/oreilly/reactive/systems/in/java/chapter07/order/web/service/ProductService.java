package com.oreilly.reactive.systems.in.java.chapter07.order.web.service;

import com.oreilly.reactive.systems.in.java.chapter07.order.model.Product;
import io.smallrye.mutiny.Multi;
import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@AllArgsConstructor
public class ProductService {

  private final OrderService orders;

  public Multi<Product> getAllProducts() {
    return Product.streamAll();
  }

}
