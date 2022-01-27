package com.oreilly.reactive.systems.in.java.chapter09.redis.services;

import com.oreilly.reactive.systems.in.java.chapter09.redis.model.Customer;
import io.quarkus.redis.client.reactive.ReactiveRedisClient;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import io.vertx.mutiny.redis.client.Response;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.NoSuchElementException;

@Singleton
public class CustomerService {
  private static final String CUSTOMER_HASH_PREFIX = "cust:";
  @Inject
  ReactiveRedisClient reactiveRedisClient;


  public Multi<Customer> allCustomers() {
    return reactiveRedisClient.keys("*")
            .onItem().transformToMulti(r -> Multi.createFrom().iterable(r).map(Response::toString))
            .onItem().transformToUniAndMerge(k ->
                    reactiveRedisClient.hgetall(k).map(r ->
                            Customer.constructCustomer(Long.parseLong(k.substring(CUSTOMER_HASH_PREFIX.length())), r)));
  }


  public Uni<Customer> createCustomer(Customer customer) {
    return storeCustomer(customer);
  }

  public Uni<Customer> getCustomer(Long id) {
    return reactiveRedisClient.hgetall(CUSTOMER_HASH_PREFIX + id)
            .map(r -> r.size() > 0 ? Customer.constructCustomer(id, r) : null);
  }

  public Uni<Customer> updateCustomer(Customer customer) {
    return getCustomer(customer.getId())
            .onItem().transformToUni((cust) -> {
              if (cust == null) {
                return Uni.createFrom().failure(new NotFoundException());
              }
              cust.setName(customer.getName());
              return storeCustomer(cust);
            });
  }

  public Uni<Void> deleteCustomer(Long id) {
    return reactiveRedisClient.hdel(Arrays.asList(CUSTOMER_HASH_PREFIX + id, "name"))
            .map(resp -> resp.toInteger() == 1 ? true : null)
            .onItem().ifNull().failWith(new NotFoundException())
            .onItem().ifNotNull().transformToUni(r -> Uni.createFrom().nullItem());
  }

  private Uni<Customer> storeCustomer(Customer customer) {

    //System.out.println(customer.getId());

    return reactiveRedisClient.hmset(Arrays.asList(CUSTOMER_HASH_PREFIX + customer.getId(), "name", customer.getName()))
            .onItem().transform(Unchecked.function(resp -> {
              if (resp.toString().equals("OK")) {
                return customer;
              } else {
                throw new NoSuchElementException();
              }
            }));
  }

}
