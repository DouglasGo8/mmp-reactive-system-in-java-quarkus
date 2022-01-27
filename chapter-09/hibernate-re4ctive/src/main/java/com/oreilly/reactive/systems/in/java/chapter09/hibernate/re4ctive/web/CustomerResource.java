package com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.web;

import com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.model.Customer;
import com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.model.Order;
import com.oreilly.reactive.systems.in.java.chapter09.hibernate.re4ctive.service.OrderService;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.panache.common.Sort;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("customer")
@ApplicationScoped
public class CustomerResource {

  @Inject
  OrderService orderService;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<Customer> findAll() {
    return Customer.streamAll(Sort.by("name"));
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> getCustomerById(@RestPath Long id) {
    final Uni<Customer> customerUni = Customer.<Customer>findById(id)
            .onItem().ifNull()
            .failWith(new WebApplicationException("Failed to find customer", Response.Status.NOT_FOUND));
    final Uni<List<Order>> customerOrdersUni = orderService.getOrderForCustomer(id);
    //
    return Uni.combine().all().unis(customerUni, customerOrdersUni).combinedWith((c, o) -> {
      c.orders = o;
      return c;
    }).onItem().transform(c -> Response.ok(c).build());
  }

  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> createCustomer(@Valid Customer customer) {
    if (null != customer.id) {
      throw new WebApplicationException("Invalid customer set on request", 422);
    }
    return Panache.withTransaction(customer::persist)
            .replaceWith(Response.ok(customer).status(Response.Status.CREATED).build());
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> updateCustomer(@RestPath Long id, @Valid Customer customer) {
    if (null == customer.id) {
      throw new WebApplicationException("Invalid customer set on request", 422);
    }
    //
    return Panache.withTransaction(() -> Customer.<Customer>findById(id).onItem().ifNotNull()
                    // transform method can be used in the place of invoke
                    .invoke(e -> e.name = customer.name))
            .onItem().ifNotNull()
            .transform(e -> Response.ok(e).build()).onItem().ifNull()
            .continueWith(Response.ok().status(Response.Status.NOT_FOUND).build());
  }

  @DELETE
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> deleteCustomer(@RestPath Long id) {
    return Panache.withTransaction(() -> Customer.deleteById(id)).map(d -> d ?
            Response.ok().status(Response.Status.NO_CONTENT).build() :
            Response.ok().status(Response.Status.NOT_FOUND).build());
  }


}
