package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LegacyStoreManagerGatewayTest {

    @Inject
    LegacyStoreManagerGateway legacyStoreManagerGateway;

    @Test
    public void testCreateStore() {
        Store store = new Store();
        store.name = "TestLegacyCreate";
        store.quantityProductsInStock = 100;

        legacyStoreManagerGateway.createStoreOnLegacySystem(store);
        // It's void and swallows exceptions, so mostly checking coverage here.
    }

    @Test
    public void testUpdateStore() {
        Store store = new Store();
        store.name = "TestLegacyUpdate";
        store.quantityProductsInStock = 50;

        legacyStoreManagerGateway.updateStoreOnLegacySystem(store);
    }
}
