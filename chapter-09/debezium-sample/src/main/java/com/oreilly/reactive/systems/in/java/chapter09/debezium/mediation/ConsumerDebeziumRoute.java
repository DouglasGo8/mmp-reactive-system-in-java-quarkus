package com.oreilly.reactive.systems.in.java.chapter09.debezium.mediation;

import lombok.NoArgsConstructor;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;


/**
 *
 */
@NoArgsConstructor
@ApplicationScoped
public class ConsumerDebeziumRoute extends RouteBuilder {
  @Override
  public void configure() {

    from("kafka:quarkus-db-server.public.customer?brokers=localhost:9092&seekTo=beginning")
            .log("Message received from Kafka : ${body}")
            .log("    on the topic ${headers[kafka.TOPIC]}")
            .log("    on the partition ${headers[kafka.PARTITION]}")
            .log("    with the offset ${headers[kafka.OFFSET]}")
            .log("    with the key ${headers[kafka.KEY]}");
  }
}
