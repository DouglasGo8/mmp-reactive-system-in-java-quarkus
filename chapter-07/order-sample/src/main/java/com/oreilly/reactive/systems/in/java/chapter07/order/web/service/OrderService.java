package com.oreilly.reactive.systems.in.java.chapter07.order.web.service;

import com.oreilly.reactive.systems.in.java.chapter07.order.model.Order;
import com.oreilly.reactive.systems.in.java.chapter07.order.model.UserProfile;
import io.smallrye.mutiny.Multi;
import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@AllArgsConstructor
public class OrderService {

  private final UserService users;


  public Multi<Order> getAllOrders() {
    return Order.streamAll();
  }


  public Multi<Order> getOrdersForUsername(String userName) {
    //
    return this.getAllOrders()
            .select()
            .when(o -> users.getUserByName(userName).onItem().transform(u -> u.name.equalsIgnoreCase(userName)));
  }

  public Multi<Order> getOrderForUser(UserProfile profile) {
    return Order.stream("userId", profile.id);
  }
}
