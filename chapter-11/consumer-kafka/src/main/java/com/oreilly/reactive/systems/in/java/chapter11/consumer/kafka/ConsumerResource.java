package com.oreilly.reactive.systems.in.java.chapter11.consumer.kafka;


import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Channel;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Singleton
@Path("/stream")
public class ConsumerResource {


  @Inject
  @Channel(value = "processed")
  Multi<String> messages;


  @GET
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<String> stream() {
    return messages;
  }

}
