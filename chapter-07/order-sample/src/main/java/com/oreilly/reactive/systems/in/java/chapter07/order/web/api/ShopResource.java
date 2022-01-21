package com.oreilly.reactive.systems.in.java.chapter07.order.web.api;


import com.oreilly.reactive.systems.in.java.chapter07.order.model.Order;
import com.oreilly.reactive.systems.in.java.chapter07.order.model.Product;
import com.oreilly.reactive.systems.in.java.chapter07.order.model.ProductModel;
import com.oreilly.reactive.systems.in.java.chapter07.order.model.UserProfile;
import com.oreilly.reactive.systems.in.java.chapter07.order.web.service.OrderService;
import com.oreilly.reactive.systems.in.java.chapter07.order.web.service.ProductService;
import com.oreilly.reactive.systems.in.java.chapter07.order.web.service.UserService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

import javax.enterprise.event.Observes;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author dougdb
 */
@Path("/shop")
@AllArgsConstructor
public class ShopResource {

  private final UserService users;
  private final OrderService orders;
  private final ProductService products;


  public void init(@Observes StartupEvent ev) {
    var o1 = new Order();
    var p1 = (Product) Product.find("name", "Pen").firstResult().await().indefinitely();
    var p2 = (Product) Product.find("name", "Hat").firstResult().await().indefinitely();
    o1.products = List.of(p1, p2);
    o1.userId = UserProfile.findByName("Bob").await().indefinitely().id;
    Panache.withTransaction(() -> Order.persist(o1)).await().indefinitely();
  }


  @GET
  @Path("/user/{name}")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> getUser(@PathParam("name") String name) {
    return users.getUserByName(name).onItem()
            .transform(u -> String.format("Hey %s", u.name))
            .onFailure().recoverWithItem("anonymous");
  }

  @GET
  @Path("/users")
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<String> users() {
    return users.getAllUsers().onItem().transform(u -> u.name);
  }

  @GET
  @Path("/products")
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<ProductModel> products() {
    return products.getAllProducts()
            .onItem().transform(p -> ProductModel.capitalizeAllFirstLetter(p.name))
            .onItem().transform(ProductModel::new);
  }

  @GET
  @Path("/orders/{user}")
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<Order> getOrdersForUsers(@PathParam("user") String userName) {
    return users.getUserByName(userName)
            .invoke(e -> System.out.println(e.name)) // Log
            .onItem().transformToMulti(orders::getOrderForUser);
  }

  @GET
  @Path("/orders")
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<Order> getOrderPerUser() {
    return users.getAllUsers().onItem().transformToMultiAndConcatenate(orders::getOrderForUser);
  }

  @GET
  @Path("/recommendations")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<Product> getRecommendations() {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(ThreadLocalRandom.current().nextInt(5)))
            .onOverflow().drop()
            .onItem().transformToUniAndConcatenate(x -> products.getRecommendedProduct());
    //.invoke(p -> System.out.println(" --> " + p.name));
  }

  @GET
  @Path("/random-recommendation")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> getRecommendation() {
    var uni1 = users.getRandomUser().invoke(s -> System.out.println(s.name));
    var uni2 = products.getRecommendedProduct();
    //
    return Uni.combine().all().unis(uni1, uni2).asTuple()
            .onItem().transform(t -> "Hello" + t.getItem1().name + ", we recommend you ");
  }

  @GET
  @Path("/random-recommendations")
  @Produces(MediaType.TEXT_PLAIN)
  public Multi<String> getRandomRecommendations() {
    var u = Multi.createFrom().ticks().every(Duration.ofSeconds(1)).onOverflow().drop()
            .onItem().transformToUniAndConcatenate(x -> users.getRandomUser());
    var p = Multi.createFrom().ticks().every(Duration.ofSeconds(1)).onOverflow().drop()
            .onItem().transformToUniAndConcatenate(x -> products.getRecommendedProduct());

    return Multi.createBy().combining().streams(u, p).asTuple()
            .onItem().transform(tuple -> "Hello " + tuple.getItem1().name + ", we recommend you "
                    + tuple.getItem2().name);
  }

  @POST
  @Path("/users/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<Long> createUser(@QueryParam("name") String name) {
    return users.createUser(name)
            .onItem().invoke(l -> System.out.println("New user created: " + name + ", id: " + l))
            .onFailure().invoke(t -> System.out.println("Cannot create the user " + name + ": " + t.getMessage()));
  }

}
