package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StoreService {

    @Inject
    Event<StoreEvent> storeEvent;

    @Transactional
    public void create(Store store) {
        store.persist();
        storeEvent.fire(new StoreEvent(store, StoreEvent.Action.CREATE));
    }

    @Transactional
    public Store update(Long id, Store updatedStore) {
        Store entity = Store.findById(id);
        if (entity == null) {
            return null;
        }

        entity.name = updatedStore.name;
        entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
        storeEvent.fire(new StoreEvent(entity, StoreEvent.Action.UPDATE));
        return entity;
    }

    @Transactional
    public Store patch(Long id, Store updatedStore) {
        Store entity = Store.findById(id);
        if (entity == null) {
            return null;
        }

        if (updatedStore.name != null) {
            entity.name = updatedStore.name;
        }

        if (updatedStore.quantityProductsInStock != 0) {
            entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
        }
        storeEvent.fire(new StoreEvent(entity, StoreEvent.Action.UPDATE));
        return entity;
    }

    @Transactional
    public boolean delete(Long id) {
        Store entity = Store.findById(id);
        if (entity == null) {
            return false;
        }
        entity.delete();
        return true;
    }
}
