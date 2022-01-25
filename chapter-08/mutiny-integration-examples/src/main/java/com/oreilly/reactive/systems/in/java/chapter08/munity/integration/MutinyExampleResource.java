package com.oreilly.reactive.systems.in.java.chapter08.munity.integration;


import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.core.Vertx;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.nio.file.FileSystemException;

@Path("/")
public class MutinyExampleResource {
  @Inject
  Vertx vertx; // from Munity Core


  @GET
  @Path("/lorem")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> getLoremIpsum() {
    return this.vertx.fileSystem().readFile("lorem.txt")
            .onItem().transform(b -> b.toString("UTF-8"));
  }

  @GET
  @Path("/missing")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> getMissingFile() {
    return this.vertx.fileSystem().readFile("oops.txt")
            .onItem().transform(b -> b.toString("UTF-8"));
  }

  @GET
  @Path("/recover")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<String> getMissingFileAndRecover() {
    return this.vertx.fileSystem().readFile("oops.txt")
            .onItem().transform(b -> b.toString("UTF-8"))
            .onFailure().recoverWithItem("Missing File!!!");
  }

  @GET
  @Path("/notfound")
  @Produces(MediaType.TEXT_PLAIN)
  public Uni<Response> getNotFound() {
    return this.vertx.fileSystem().readFile("oops.txt")
            .onItem().transform(b -> b.toString("UTF-8"))
            .onItem().transform(body -> Response.ok(body).build())
            .onFailure().recoverWithItem(Response.status(Response.Status.NOT_FOUND).build());
  }

  @ServerExceptionMapper
  public Response mapFileSystemException(FileSystemException ex) {
    return Response.status(Response.Status.NOT_FOUND)
            .entity(ex.getMessage())
            .build();
  }

}
