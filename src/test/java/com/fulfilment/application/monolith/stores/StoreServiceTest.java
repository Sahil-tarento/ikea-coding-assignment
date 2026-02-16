package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class StoreServiceTest {

    @Inject
    StoreService storeService;

    @Test
    public void testCrud() {
        // Create
        Store store = new Store();
        store.name = "Service Store";
        store.quantityProductsInStock = 10;
        storeService.create(store);
        Assertions.assertNotNull(store.id);

        // Update
        Store updateData = new Store();
        updateData.name = "Updated Service Store";
        updateData.quantityProductsInStock = 20;

        Store updated = storeService.update(store.id, updateData);
        Assertions.assertEquals("Updated Service Store", updated.name);
        Assertions.assertEquals(20, updated.quantityProductsInStock);

        // Update Not Found
        Assertions.assertNull(storeService.update(9999L, updateData));

        // Patch
        Store patchData = new Store();
        patchData.name = "Patched Name";
        // quantity 0, should not update

        Store patched = storeService.patch(store.id, patchData);
        Assertions.assertEquals("Patched Name", patched.name);
        Assertions.assertEquals(20, patched.quantityProductsInStock);

        // Patch Not Found
        Assertions.assertNull(storeService.patch(9999L, patchData));

        // Delete
        Assertions.assertTrue(storeService.delete(store.id));

        // Delete Not Found
        Assertions.assertFalse(storeService.delete(store.id)); // Already deleted
    }
}
