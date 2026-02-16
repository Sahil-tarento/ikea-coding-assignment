package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreGateway;
import com.fulfilment.application.monolith.stores.Store;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StoreGatewayAdapter implements StoreGateway {
    @Override
    public boolean exists(Long id) {
        return Store.findById(id) != null;
    }
}
