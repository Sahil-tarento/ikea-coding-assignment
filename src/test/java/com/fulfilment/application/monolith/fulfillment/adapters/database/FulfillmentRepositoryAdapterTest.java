package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.List;

@QuarkusTest
public class FulfillmentRepositoryAdapterTest {

    @Inject
    FulfillmentRepositoryAdapter repositoryAdapter;

    @Test
    @Transactional
    public void testCreateAndGet() {
        Fulfillment f = new Fulfillment();
        f.storeId = 1L;
        f.productId = 1L;
        f.warehouseBusinessUnitCode = "W1";

        repositoryAdapter.create(f);
        Assertions.assertNotNull(f.id);

        List<Fulfillment> all = repositoryAdapter.getAll();
        Assertions.assertTrue(all.stream().anyMatch(item -> item.id.equals(f.id)));
    }

    @Test
    @Transactional
    public void testExists() {
        Fulfillment f = new Fulfillment();
        f.storeId = 2L;
        f.productId = 2L;
        f.warehouseBusinessUnitCode = "W2";
        repositoryAdapter.create(f);

        Assertions.assertTrue(repositoryAdapter.exists(2L, 2L, "W2"));
        Assertions.assertFalse(repositoryAdapter.exists(2L, 2L, "W99"));
    }

    @Test
    @Transactional
    public void testDelete() {
        Fulfillment f = new Fulfillment();
        f.storeId = 3L;
        f.productId = 3L;
        f.warehouseBusinessUnitCode = "W3";
        repositoryAdapter.create(f);

        boolean deleted = repositoryAdapter.deleteById(f.id);
        Assertions.assertTrue(deleted);
        Assertions.assertFalse(repositoryAdapter.exists(3L, 3L, "W3"));
    }
}
