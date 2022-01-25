package com.oreilly.reactive.systems.in.java.chapter08.munity.integration;

import com.oreilly.reactive.systems.in.java.chapter08.munity.integration.model.Book;
import com.oreilly.reactive.systems.in.java.chapter08.munity.integration.model.Quote;
import com.oreilly.reactive.systems.in.java.chapter08.munity.integration.service.BookService;
import com.oreilly.reactive.systems.in.java.chapter08.munity.integration.service.MarketService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.tuples.Tuple2;
import io.vertx.core.file.OpenOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.core.file.AsyncFile;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.Duration;

@Path("/")
public class StreamResource {

  @Inject
  Vertx vertx;
  @Inject
  BookService service;
  @Inject
  MarketService market;

  @GET
  @Path("/book")
  @Produces(MediaType.TEXT_PLAIN)
  public Multi<String> book() {
    var ticks = Multi.createFrom().ticks().every(Duration.ofSeconds(1));
    var book = this.vertx.fileSystem().open("war-and-peace.txt", new OpenOptions().setRead(true))
            .onItem().transformToMulti(AsyncFile::toMulti) // forces read file in async Mode
            .onItem().transform(b -> b.toString("UTF-8"));

    return Multi.createBy().combining().streams(ticks, book).asTuple()
            .onItem().transform(Tuple2::getItem2);
  }

  @GET
  @Path("/books")
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<Book> books() {
    var ticks = Multi.createFrom().ticks().every(Duration.ofSeconds(1));
    var books = service.getBooks();

    return Multi.createBy().combining().streams(ticks, books).asTuple()
            .onItem().transform(Tuple2::getItem2);
  }

  @GET
  @Path("/market")
  @Produces(MediaType.SERVER_SENT_EVENTS)
  public Multi<Quote> market() {
    return this.market.getEventStream();
  }
}
