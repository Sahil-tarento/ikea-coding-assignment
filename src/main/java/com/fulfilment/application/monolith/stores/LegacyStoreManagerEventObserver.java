package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;

@ApplicationScoped
public class LegacyStoreManagerEventObserver {

    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    public void onStoreEvent(@Observes(during = TransactionPhase.AFTER_SUCCESS) StoreEvent event) {
        if (event.action == StoreEvent.Action.CREATE) {
            legacyStoreManagerGateway.createStoreOnLegacySystem(event.store);
        } else if (event.action == StoreEvent.Action.UPDATE) {
            legacyStoreManagerGateway.updateStoreOnLegacySystem(event.store);
        }
    }
}
