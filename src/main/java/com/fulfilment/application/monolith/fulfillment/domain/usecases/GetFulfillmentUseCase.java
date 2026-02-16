package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class GetFulfillmentUseCase {

    private final FulfillmentStore fulfillmentStore;

    @Inject
    public GetFulfillmentUseCase(FulfillmentStore fulfillmentStore) {
        this.fulfillmentStore = fulfillmentStore;
    }

    public List<Fulfillment> getAll() {
        return fulfillmentStore.getAll();
    }
}
