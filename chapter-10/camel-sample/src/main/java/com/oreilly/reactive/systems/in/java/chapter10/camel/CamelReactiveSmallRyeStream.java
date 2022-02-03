package com.oreilly.reactive.systems.in.java.chapter10.camel;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class CamelReactiveSmallRyeStream {


  // @Inject
  // CamelReactiveStreamsService reactiveStreamsService;


  @Outgoing("channel1")
  public Multi<String> generate() {
    // Build an infinite stream of random prices
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .map(x -> ThreadLocalRandom.current().nextDouble())
            .map(p -> Double.toString(p));
    //.map(Message::of);
  }


}
