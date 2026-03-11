package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateFulfillmentUseCase {

    private final FulfillmentStore fulfillmentStore;
    private final FulfillmentValidator fulfillmentValidator;

    @Inject
    public CreateFulfillmentUseCase(FulfillmentStore fulfillmentStore, FulfillmentValidator fulfillmentValidator) {
        this.fulfillmentStore = fulfillmentStore;
        this.fulfillmentValidator = fulfillmentValidator;
    }

    public void create(Fulfillment fulfillment) {
        // Perform business constraint validations via FulfillmentValidator
        fulfillmentValidator.validateNewFulfillment(fulfillment);

        fulfillmentStore.create(fulfillment);
    }
}
