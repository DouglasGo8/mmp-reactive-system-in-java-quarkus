package com.oreilly.reactive.systems.in.java.chapter07.order.web.service;

import com.oreilly.reactive.systems.in.java.chapter07.order.model.UserProfile;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserService {



  public Uni<UserProfile> getUserByName(String name) {
    return UserProfile.findByName(name);
  }

  public Multi<UserProfile> getAllUsers() {
    return UserProfile.streamAll();
  }
}
