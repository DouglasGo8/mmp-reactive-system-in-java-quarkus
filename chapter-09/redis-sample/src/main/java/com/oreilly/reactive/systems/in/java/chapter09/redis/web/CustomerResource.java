package com.oreilly.reactive.systems.in.java.chapter09.redis.web;


import com.oreilly.reactive.systems.in.java.chapter09.redis.model.Customer;
import com.oreilly.reactive.systems.in.java.chapter09.redis.services.CustomerService;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.jboss.resteasy.reactive.RestPath;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.concurrent.atomic.AtomicLong;

@ApplicationScoped
@Path("/customer")
public class CustomerResource {
  private final AtomicLong customerId = new AtomicLong(1);
  @Inject
  CustomerService service;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Multi<Customer> allCustomers() {
    return this.service.allCustomers();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Customer> getCustomer(@RestPath Long id) {
    return service.getCustomer(id).onItem().ifNull()
            .failWith(new WebApplicationException("Failed to find customer", Response.Status.NOT_FOUND));
  }


  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> createCustomer(Customer customer) {

    //System.out.println(customer.getName());


    if (null != customer.getId() || customer.getName().length() == 0) {
      throw new WebApplicationException("Invalid customer set on request", 422);
    }


    customer.setId(customerId.getAndIncrement());


    // return null;

    return this.service.createCustomer(customer)
            .onItem().transform(cust -> Response.ok(cust).status(Response.Status.CREATED).build())
            .onFailure().recoverWithItem(Response.serverError().build());

  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> updateCustomer(@RestPath Long id, Customer customer) {
    if (customer.getId() == null || (customer.getName() == null || customer.getName().length() == 0)) {
      throw new WebApplicationException("Invalid customer set on request", 422);
    }

    return this.service.updateCustomer(customer)
            .onItem().ifNotNull().transform(success -> Response.ok(customer).build())
            .onFailure().recoverWithItem(Response.ok().status(Response.Status.NOT_FOUND).build());
  }

  @DELETE
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Uni<Response> deleteCustomer(@RestPath Long id) {
    return this.service.deleteCustomer(id)
            .onItem().transform(i -> Response.ok().status(Response.Status.NO_CONTENT).build())
            .onFailure().recoverWithItem(Response.ok().status(Response.Status.NOT_FOUND).build());
  }

}
