package com.oreilly.reactive.systems.in.java.chapter10.blocking;

import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Multi;
import lombok.SneakyThrows;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@ApplicationScoped
public class BlockingExample {

  @Outgoing(value = "ticks")
  public Multi<Long> ticks() {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onOverflow().drop();
  }

  @Blocking
  @SneakyThrows
  @Incoming(value = "ticks")
  @Outgoing(value = "hello")
  public String hello(long tick) {
    TimeUnit.SECONDS.sleep(1);
    return "Hello - " + tick;
  }

  @Incoming(value = "hello")
  public void print(String msg) {
    System.out.println(msg);
  }

}
