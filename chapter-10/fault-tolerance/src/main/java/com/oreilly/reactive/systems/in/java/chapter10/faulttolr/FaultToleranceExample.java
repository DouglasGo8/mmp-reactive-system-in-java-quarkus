package com.oreilly.reactive.systems.in.java.chapter10.faulttolr;

import io.smallrye.mutiny.Multi;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;

@ApplicationScoped
public class FaultToleranceExample {


  @Outgoing(value = "ticks")
  public Multi<Long> ticks() {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1)).onOverflow().drop();
  }

  @Incoming(value = "ticks")
  @Outgoing(value = "hello")
  @Retry(maxRetries = 10, delay = 1, delayUnit = ChronoUnit.SECONDS)
  public String hello(long tick) {
    this.maybeFaulty();
    return "Hello - " + tick;
  }

  @Incoming("hello")
  public void print(String msg) {
    System.out.println(msg);
  }


  void maybeFaulty() {
    if (ThreadLocalRandom.current().nextInt(10) > 7) {
      throw new RuntimeException("boom");
    }
  }
}
