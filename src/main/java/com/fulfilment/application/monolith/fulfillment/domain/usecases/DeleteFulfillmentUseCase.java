package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityNotFoundException;

@ApplicationScoped
public class DeleteFulfillmentUseCase {

    private final FulfillmentStore fulfillmentStore;

    @Inject
    public DeleteFulfillmentUseCase(FulfillmentStore fulfillmentStore) {
        this.fulfillmentStore = fulfillmentStore;
    }

    public void delete(Long id) {
        boolean deleted = fulfillmentStore.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException("Fulfillment with id of " + id + " does not exist.");
        }
    }
}
