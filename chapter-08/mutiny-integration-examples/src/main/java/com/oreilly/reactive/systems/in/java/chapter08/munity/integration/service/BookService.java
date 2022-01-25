package com.oreilly.reactive.systems.in.java.chapter08.munity.integration.service;

import com.oreilly.reactive.systems.in.java.chapter08.munity.integration.model.Book;
import io.smallrye.mutiny.Multi;
import lombok.NoArgsConstructor;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;


@NoArgsConstructor
@ApplicationScoped
public class BookService {

  private final List<Book> books = List.of(
          new Book(0, "Fundamentals of Software Architecture", List.of("Mark Richards", "Neal Ford")),
          new Book(1, "Domain-Driven Design", List.of("Eric Evans")),
          new Book(2, "Designing Distributed Systems", List.of("Brendan Burns")),
          new Book(3, "Building Evolutionary Architectures", List.of("Neal Ford", "Rebecca Parsons", "Patrick Kua")),
          new Book(4, "Principles of Concurrent and Distributed Programming", List.of("M. Ben-Ari")),
          new Book(5, "Distributed Systems Observability", List.of("Cindy Sridharan")),
          new Book(6, "Event Streams in Action", List.of("Alexander Dean", "Valentin Crettaz")),
          new Book(7, "Designing Data-Intensive Applications", List.of("Martin Kleppman")),
          new Book(8, "Building Microservices", List.of("Sam Newman")),
          new Book(9, "Kubernetes in Action", List.of("Marko Luksa")),
          new Book(10, "Kafka - the definitive guide", List.of("Gwenn Shapira", "Todd Palino", "Rajini Sivaram", "Krit Petty")),
          new Book(11, "Effective Java", List.of("Joshua Bloch")),
          new Book(12, "Building Event-Driven Microservices", List.of("Adam Bellemare"))
  );

  public Multi<Book> getBooks() {
    return Multi.createFrom().iterable(books);
  }

}
