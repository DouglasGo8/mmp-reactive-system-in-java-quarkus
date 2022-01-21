package com.oreilly.reactive.systems.in.java.chapter07.order.web.service;

import lombok.AllArgsConstructor;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@AllArgsConstructor
public class OrderService {
  private final UserService users;
}
