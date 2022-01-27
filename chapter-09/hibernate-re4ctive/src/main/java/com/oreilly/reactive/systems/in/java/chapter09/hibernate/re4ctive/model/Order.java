package com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import io.vertx.mutiny.sqlclient.Row;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;


@Entity
@Table(name = "orders")
@NoArgsConstructor
public class Order extends PanacheEntity {

  @Column(nullable = false)
  public Long customerId;
  public String description;
  public BigDecimal total;

  public static Order from(Row row) {
    final Order order = new Order();
    order.id = row.getLong("id");
    order.customerId = row.getLong("customerid");
    order.description = row.getString("description");
    order.total = row.getBigDecimal("total");
    //
    return order;
  }
}
