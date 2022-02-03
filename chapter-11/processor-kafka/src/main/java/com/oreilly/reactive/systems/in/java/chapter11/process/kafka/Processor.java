package com.oreilly.reactive.systems.in.java.chapter11.process.kafka;

import lombok.SneakyThrows;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

import javax.inject.Singleton;
import java.net.InetAddress;

@Singleton
public class Processor {

  @SneakyThrows
  @Incoming(value = "ticks")
  @Outgoing(value = "processed")
  public String process(String payload) {
    return " :: Message Consumed by Reactive Messaging Kafka :: ["
            .concat(payload)
            .concat("] :: ")
            .concat(InetAddress.getLocalHost().getHostName());
  }

}
