package com.oreilly.reactive.systems.in.java.chapter07.order.web.service;

import com.oreilly.reactive.systems.in.java.chapter07.order.model.Product;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
@AllArgsConstructor
public class ProductService {

  private final OrderService orders;

  public Multi<Product> getAllProducts() {
    return Product.streamAll();
  }

  public Uni<Product> getRecommendedProduct() {
    return Product.count()
            //.invoke(e -> System.out.println(e.intValue()))
            .onItem().transform(l -> ThreadLocalRandom.current().nextInt(l.intValue()))
            .onItem().transformToUni(idx -> Product.findAll().page(idx, 1).firstResult());
  }

}
