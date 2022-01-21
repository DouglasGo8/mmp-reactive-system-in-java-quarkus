package com.oreilly.reactive.systems.in.java.chapter07.order.web.api;


import com.oreilly.reactive.systems.in.java.chapter07.order.model.ProductModel;
import com.oreilly.reactive.systems.in.java.chapter07.order.web.service.ProductService;
import com.oreilly.reactive.systems.in.java.chapter07.order.web.service.UserService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/shop")
@AllArgsConstructor
public class ShopResource {

  private final UserService users;
  private final ProductService products;


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
}
