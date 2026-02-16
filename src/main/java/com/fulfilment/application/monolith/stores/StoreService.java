package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class StoreService {

    @Transactional
    public void create(Store store) {
        store.persist();
    }

    @Transactional
    public Store update(Long id, Store updatedStore) {
        Store entity = Store.findById(id);
        if (entity == null) {
            return null;
        }

        entity.name = updatedStore.name;
        entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
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
