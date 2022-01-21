package com.oreilly.reactive.systems.in.java.chapter07.order.web.service;

import com.oreilly.reactive.systems.in.java.chapter07.order.model.Product;
import com.oreilly.reactive.systems.in.java.chapter07.order.model.UserProfile;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class UserService {


  public Uni<UserProfile> getUserByName(String name) {
    return UserProfile.findByName(name);
  }

  public Multi<UserProfile> getAllUsers() {
    return UserProfile.streamAll();
  }

  public Uni<UserProfile> getRandomUser() {
    return UserProfile.count()
            .onItem().transform(l -> ThreadLocalRandom.current().nextInt(l.intValue()))
            .onItem().transformToUni(idx -> Product.findAll().page(idx, 1).firstResult());
  }

  public Uni<Long> createUser(String name) {
    var userProfile = new UserProfile();
    userProfile.name = name;
    return Panache.withTransaction(() -> userProfile.persist().onItem().transform(u -> ((UserProfile) u).id));
  }
}
