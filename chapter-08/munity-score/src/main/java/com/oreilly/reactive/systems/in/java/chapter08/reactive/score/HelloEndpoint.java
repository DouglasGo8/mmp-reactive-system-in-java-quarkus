package com.oreilly.reactive.systems.in.java.chapter08.reactive.score;

import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
}
