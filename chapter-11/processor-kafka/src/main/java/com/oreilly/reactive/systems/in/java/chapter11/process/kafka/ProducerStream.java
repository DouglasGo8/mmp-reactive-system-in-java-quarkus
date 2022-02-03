package com.oreilly.reactive.systems.in.java.chapter11.process.kafka;


import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@ApplicationScoped
public class ProducerStream {

  @Outgoing(value = "generator")
  public Multi<String> producer() {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onOverflow().drop()
            .map(String::valueOf);
  }

}
