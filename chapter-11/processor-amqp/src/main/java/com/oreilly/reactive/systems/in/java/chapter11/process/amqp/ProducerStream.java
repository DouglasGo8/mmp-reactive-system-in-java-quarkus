package com.oreilly.reactive.systems.in.java.chapter11.process.amqp;


import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@ApplicationScoped
public class ProducerStream {


  @Outgoing(value = "ticks")
  public Multi<Long> generator() {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onOverflow().drop();
  }



}
