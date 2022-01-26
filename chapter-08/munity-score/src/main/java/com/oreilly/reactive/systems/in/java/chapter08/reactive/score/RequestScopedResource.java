package com.oreilly.reactive.systems.in.java.chapter08.reactive.score;

import io.smallrye.common.annotation.NonBlocking;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/request")
@RequestScoped
public class RequestScopedResource {

  int count = 0;

  @GET
  @NonBlocking
  @Produces(MediaType.TEXT_PLAIN)
  public String requestScope() {
    return this + "-" + count++;
  }
}
