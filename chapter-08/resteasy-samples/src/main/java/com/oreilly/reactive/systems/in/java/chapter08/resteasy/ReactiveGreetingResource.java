package com.oreilly.reactive.systems.in.java.chapter08.resteasy;

import io.smallrye.common.annotation.NonBlocking;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello-rest-reactive")
public class ReactiveGreetingResource {

  @GET
  @NonBlocking
  @Produces(MediaType.TEXT_PLAIN)
  public String hello() {
    return "Hello RESTEasy Reactive from " + Thread.currentThread().getName();
  }

  @GET
  @Path("/blocking")
  @Produces(MediaType.TEXT_PLAIN)
  public String helloBlocking() {
    return "Hello RESTEasy Reactive from " + Thread.currentThread().getName();
  }
}
