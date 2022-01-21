package com.oreilly.reactive.systems.in.java.chapter07.munity;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.json.JsonObject;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.HttpResponse;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AppTest {


  @Test
  public void testCreationWithSubscription() {

    Uni.createFrom().emitter(e -> e.complete("hello")).subscribe()
            .with(
                    item -> System.out.println("Received: " + item),
                    fail -> System.out.println("D'oh!" + fail)
            );

    Multi.createFrom().emitter(e -> e.emit("Hello").emit("World").complete()).subscribe()
            .with(
                    item -> System.out.println("Received: " + item),
                    failure -> System.out.println("D'oh! " + failure),
                    () -> System.out.println("Done!"));

  }

  @Test
  public void testMultiApi() {

    var multi = Multi.createFrom().items("a", "b", "c", "d");

    multi.select().where(s -> s.length() > 3)
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

    itemAsList.subscribe().with(e -> System.out.println("Received: " + e));
    itemsAsMap.subscribe().with(e -> System.out.println(e.entrySet()));
    count.subscribe().with(System.out::println);

  }

  @Test
  public void testMultiCombine() {
    var multi1 = Multi.createFrom().items("a", "b", "c");
    var multi2 = Multi.createFrom().items("d", "e", "f");
    //
    var combined = Multi.createBy().combining().streams(multi1, multi2)
            .asTuple().onItem().transform(tuple -> {
              String itemFromTheFirstStream = tuple.getItem1();
              String itemFromTheSecondStream = tuple.getItem2();

              return combine(itemFromTheFirstStream, itemFromTheSecondStream);
            }).subscribe().with(System.out::println);

  }

  @Test
  @SneakyThrows
  public void testMultiMerge() {
    var multi1 = Multi.createFrom().items("a", "b", "c")
            .onItem().call(() -> Uni.createFrom().voidItem().onItem().delayIt().by(Duration.ofMillis(
                    ThreadLocalRandom.current().nextInt(100)
            )));
    //
    var multi2 = Multi.createFrom().items("d", "e", "f")
            .onItem().call(() ->
                    Uni.createFrom().voidItem().onItem()
                            .delayIt().by(Duration.ofMillis(ThreadLocalRandom.current().nextInt(100)
                            )));

    //

    var concatenated = Multi.createBy().concatenating().streams(multi1, multi2);
    var merged = Multi.createBy().merging().streams(multi1, multi2);

    concatenated.subscribe().with(item -> System.out.println("(concatenation) >> " + item));
    merged.subscribe().with(item -> System.out.println("(merge) >> " + item));

    Thread.sleep(5000);
  }

  @Test
  public void testMultiObserve() {
    var multi = Multi.createFrom().items("a", "b", "c", "d");

    multi.onSubscription().invoke(s -> System.out.println("Subscribed"))
            .onCancellation().invoke(() -> System.out.println("Cancelled"))
            .onItem().invoke(s -> System.out.println("Item: " + s))
            .onFailure().invoke(f -> System.out.println("Failure: " + f))
            .onCompletion().invoke(() -> System.out.println("Completed!"))
            .subscribe().with(i -> System.out.println("Received.." + i));
  }

  @Test
  public void testMultiTransform() {
    //
    var multi = Multi.createFrom().items("a", "b", "c", "d");

    class MyBusinessException extends Throwable {
      public MyBusinessException(Throwable f) {
      }
    }
    multi.onItem().transform(String::toUpperCase)
            .onFailure().transform(MyBusinessException::new)
            .subscribe().with(item -> System.out.println(">> " + item));
  }

  @Test
  @SneakyThrows
  public void testMultiTransformAsync() {

    final var latch = new CountDownLatch(2); // forces latch::countDown to be invoked twice
    final var multi = Multi.createFrom().items("Douglas", "Ketty", "Bingola");

    // Represents an external invocation service
    final Function<String, Uni<String>> service = name -> Uni.createFrom().item(name)
            //.invoke(e -> System.out.println("Received..." + e))
            .emitOn(Infrastructure.getDefaultExecutor())
            .onItem()
            // Forces some delay
            .call(() -> Uni.createFrom().voidItem().onItem().delayIt()
                    .by(Duration.ofMillis(ThreadLocalRandom.current().nextInt(100)))
            );
    //
    multi.onItem().transformToUniAndConcatenate(service::apply)
            .subscribe().with
                    (
                            s -> System.out.println("(concatenation) Received: " + s),
                            Throwable::printStackTrace,
                            latch::countDown // first invoke
                    );
    //
    multi.onItem().transformToUniAndMerge(service::apply)
            .subscribe()
            .with(
                    s -> System.out.println("(merge) Received: " + s),
                    Throwable::printStackTrace,
                    latch::countDown // second invoke
            );
    latch.await();
    // Thread.sleep(5000);
  }


  @Test
  @SneakyThrows
  public void testUni() {

    final var vertx = Vertx.vertx();
    final var latch = new CountDownLatch(1);
    final var webClient = WebClient.create(vertx);
    final var uni = webClient.getAbs("https://httpbin.org/json").send();
    //
    uni.onItem().transform(HttpResponse::bodyAsJsonObject)
            .onFailure().recoverWithItem(new JsonObject().put("message", "failure"))
            .onTermination().invoke(latch::countDown)
            .subscribe()
            .with(json -> System.out.println("Got json document: " + json));
    //
    latch.await();

  }


  @Test
  public void testUniCombine() {

    final Function<String, Uni<String>> service1 = args -> Uni.createFrom().item(args);
    final Function<String, Uni<String>> service2 = args -> Uni.createFrom().item(args);
    final BiFunction<String, String, String> combine = (args, args1) -> args + " - " + args1;

    var uni1 = service1.apply("hello");
    var uni2 = service2.apply("world");

    Uni.combine().all().unis(uni1, uni2).asTuple()
            .onItem()
            // transform op always switch the context to Sync mode invocation
            .transform(tuple -> {
              var responseFromFirstService = tuple.getItem1();
              var responseFromSecondService = tuple.getItem2();
              return combine.apply(responseFromFirstService, responseFromSecondService);
            }).subscribe()
            .with(System.out::println);

  }

  private String getKeyForItem(String item) {
    return item;
  }

  @Test
  @SneakyThrows
  public void testUniFailure() {
    var uni = Uni.createFrom().failure(new IOException("boom"));

    Function<Void, Uni<? extends String>> callFallbackService = args -> Uni.createFrom()
            // Inject some artificial delay
            .item("hello").onItem().delayIt().by(Duration.ofMillis(10));

    uni.onFailure().recoverWithItem("hello!")
            .subscribe().with(item -> System.out.println("Recovering with item >> " + item));

    uni.onFailure().recoverWithUni(callFallbackService.apply(null))
            .subscribe().with(item -> System.out.println("Recovering with uni >> " + item));

    // Retry
    uni.onFailure().retry().withBackOff(Duration.ofSeconds(1), Duration.ofSeconds(3)).atMost(3)
            .subscribe().with(
                    item -> System.out.println("Recovering with retry >> " + item),
                    failure -> System.out.println("Still unsuccessful " + failure));

    Thread.sleep(10000);

  }

  @Test
  public void testUniTransformAsync() {

    var uni = Uni.createFrom().item("mutiny!");
    Function<String, Multi<String>> getAMulti = args -> Multi.createFrom().items(args, args);
    Function<String, Uni<String>> callMyRemoteService = args -> Uni.createFrom().item(args.toUpperCase());
    //
    uni.onItem().transformToUni(callMyRemoteService::apply).subscribe().with(s -> System.out.println("Received: " + s));
    uni.onItem().transformToMulti(getAMulti).subscribe()
            .with(
                    s -> System.out.println("Received item: " + s),
                    () -> System.out.println("Done!"));
  }

  private String combine(String itemFromTheFirstStream, String itemFromTheSecondStream) {
    return itemFromTheFirstStream + " - " + itemFromTheSecondStream;
  }
}
