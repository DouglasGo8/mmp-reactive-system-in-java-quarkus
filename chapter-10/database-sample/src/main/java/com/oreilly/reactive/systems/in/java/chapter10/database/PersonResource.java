package com.oreilly.reactive.systems.in.java.chapter10.database;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Path("/")
public class PersonResource {

  @Channel(value = "upload")
  Emitter<Person> emitter;


  /*
   * Work in SmallRye Mode
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> upload(Person person) {
    System.out.println("emitting " + person.name + " / " + emitter.isCancelled());
    return Uni.createFrom().completionStage(() -> {
              var future = new CompletableFuture<Void>();
              //
              var message = Message.of(person, () -> { // Ack Pipe
                System.out.println("Ack " + person.name);
                future.complete(null);
                return CompletableFuture.completedFuture(null);
              }, f -> { // Nack Pipe
                System.out.println("Nack " + person.name + " " + f.getMessage());
                future.completeExceptionally(f);
                return CompletableFuture.completedFuture(null);
              });
              //
              emitter.send(message);
              //
              return future;
            }).replaceWith(Response.accepted().build())
            .onFailure().recoverWithItem(t -> Response.status(Response.Status.BAD_REQUEST)
                    .encoding(t.getMessage()).build());

  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<List<Person>> getAll() {
    return Person.listAll();
  }


  @POST
  @Path("/post")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> post(Person person) {
    return Panache.withTransaction(person::persistAndFlush)
            .replaceWith(Response.accepted().build())
            .onFailure().recoverWithItem(t -> Response.status(Response.Status.BAD_REQUEST).entity(t.getMessage()).build());
  }
}
