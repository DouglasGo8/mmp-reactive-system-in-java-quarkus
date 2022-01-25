package com.oreilly.reactive.systems.in.java.chapter08.munity.integration.service;

import com.oreilly.reactive.systems.in.java.chapter08.munity.integration.model.Quote;
import io.smallrye.mutiny.Multi;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@NoArgsConstructor
@ApplicationScoped
public class MarketService {

  public Multi<Quote> getEventStream() {
    return Multi.createFrom().ticks().every(Duration.ofSeconds(1)).onItem().transform(x -> getRandomQuote());
  }

  private Quote getRandomQuote() {
    var i = ThreadLocalRandom.current().nextInt(3);
    String company = "MacroHard";
    if (i == 0) {
      company = "Divinator";
    } else if (i == 1) {
      company = "Black Coat";
    }
    double value = ThreadLocalRandom.current().nextInt(200) * ThreadLocalRandom.current().nextDouble();

    return new Quote(company, value);
  }
}
