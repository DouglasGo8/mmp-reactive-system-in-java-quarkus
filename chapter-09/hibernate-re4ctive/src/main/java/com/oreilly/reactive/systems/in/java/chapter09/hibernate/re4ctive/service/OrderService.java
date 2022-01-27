package com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.service;

import com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.model.Order;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.pgclient.PgPool;
import io.vertx.mutiny.sqlclient.Tuple;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@NoArgsConstructor
@ApplicationScoped
public class OrderService {

  @Inject
  PgPool pgPoolClient;

  public Uni<List<Order>> getOrderForCustomer(Long customerId) {
    final String sql = "SELECT id, customerid, description, total FROM orders WHERE customerid= $1";
    return this.pgPoolClient.preparedQuery(sql)
            .execute(Tuple.of(customerId)) // query parameter
            .onItem().transformToMulti(s -> Multi.createFrom().iterable(s))
            .onItem().transform(Order::from) // received a row
            .collect().asList();

  }
}
