package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.usecases.CreateFulfillmentUseCase;
import com.fulfilment.application.monolith.fulfillment.domain.usecases.DeleteFulfillmentUseCase;
import com.fulfilment.application.monolith.fulfillment.domain.usecases.GetFulfillmentUseCase;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentResource {

    @Inject
    CreateFulfillmentUseCase createFulfillmentUseCase;

    @Inject
    GetFulfillmentUseCase getFulfillmentUseCase;

    @Inject
    DeleteFulfillmentUseCase deleteFulfillmentUseCase;

    @GET
    public List<Fulfillment> getAll() {
        return getFulfillmentUseCase.getAll();
    }

    @POST
    @Transactional
    public Response create(Fulfillment fulfillment) {
        try {
            createFulfillmentUseCase.create(fulfillment);
            return Response.status(201).entity(fulfillment).build();
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(e.getMessage(), 404);
        } catch (EntityExistsException e) {
            throw new WebApplicationException(e.getMessage(), 409);
        } catch (IllegalArgumentException e) {
            throw new WebApplicationException(e.getMessage(), 400);
        }
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        try {
            deleteFulfillmentUseCase.delete(id);
            return Response.status(204).build();
        } catch (EntityNotFoundException e) {
            throw new WebApplicationException(e.getMessage(), 404);
        }
    }
}
