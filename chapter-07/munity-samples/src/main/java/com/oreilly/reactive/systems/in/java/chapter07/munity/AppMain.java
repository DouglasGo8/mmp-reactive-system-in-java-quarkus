package com.oreilly.reactive.systems.in.java.chapter07.munity;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.stream.Collectors;

public class AppMain {


  @Test
  public void testCreationWithSubscription() {

    Uni.createFrom().emitter(e -> {
      e.complete("hello");
    }).subscribe().with(
            item -> System.out.println("Received: " + item),
            fail -> System.out.println("D'oh!" + fail)
    );

    Multi.createFrom().emitter(e -> {
      e.emit("Hello").emit("World").complete();
    }).subscribe().with(
            item -> System.out.println("Received: " + item),
            failure -> System.out.println("D'oh! " + failure),
            () -> System.out.println("Done!"));

  }

  @Test
  public void testMultiApi() {

    var multi = Multi.createFrom().items("a", "b", "c", "d");

    multi.select().where(s -> s.length()>3)
            .onItem().transform(String::toUpperCase)
            .onFailure().recoverWithCompletion()
            .onCompletion().continueWith("!")
            .subscribe().with(item -> System.out.println("Received: " + item));

  }

  @Test
  public void testMultiCollect() {
    var multi = Multi.createFrom().items("a", "b", "c", "d");
    var itemAsList = multi.collect().asList();
    Uni<Map<String, String>> itemsAsMap = multi.collect().asMap(this::getKeyForItem);
    Uni<Long> count = multi.collect().with(Collectors.counting());

    itemAsList.subscribe().with(e ->System.out.println("Received: " + e));
    itemsAsMap.subscribe().with(e -> System.out.println(e.entrySet()));
    count.subscribe().with(System.out::println);


  }

  @Test
  public void testMultiCombine() {}

  private  String getKeyForItem(String item) {
    return item;
  }
}
