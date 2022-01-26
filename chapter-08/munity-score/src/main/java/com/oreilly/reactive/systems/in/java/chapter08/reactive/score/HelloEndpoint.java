package com.oreilly.reactive.systems.in.java.chapter08.reactive.score;

import com.oreilly.reactive.systems.in.java.chapter08.reactive.score.model.User;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * @author dougdb
 */
@Path("/scores")
public class HelloEndpoint {

  @GET
  @NonBlocking
  @Path("/simple")
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "hello";
  }

  @GET
  @Path("/simple-blocking")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloBlocking() {
    return "hello";
  }

  @GET
  @Path("/uni")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> helloUni() {
    return Uni.createFrom().item("hello");
  }

  @GET
  @Path("/json")
  @Produces(MediaType.APPLICATION_JSON)
  public User user() {
    return new User("dougdb");
  }

  @GET
  @Path("/json-uni")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<User> userUni() {
    return Uni.createFrom().item(new User("dougdb"));
  }

  @GET
  @Path("/json-blocking")
  @Produces(MediaType.APPLICATION_JSON)
  public User userBlocking() {
    return new User("dougdb");
  }

  @POST
  @NonBlocking
  @Path("/json")
  @Produces(MediaType.TEXT_PLAIN)
  @Consumes(MediaType.APPLICATION_JSON)
  public String post(User user) {
    System.out.println("User is " + user);
    return user.getName();
  }

  @GET
  @Path("/stream")
  @Produces(MediaType.TEXT_PLAIN)
  public Multi<String> stream() {
    return Multi.createFrom().items("a", "b", "c");
  }

  @GET
  @Path("/stream-json")
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<User> users() {
    return Multi.createFrom().items(new User("tinhosa"), new User("bingola"));
  }

  @GET
  @Path("/stream-sse")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<User> userSSE() {
    return Multi.createFrom().items(new User("tinhosa"), new User("bingola"));
  }

}
