package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

@QuarkusTest
public class WarehouseRepositoryTest {

    @Inject
    WarehouseRepository warehouseRepository;

    @Test
    @TestTransaction
    public void testCrud() {
        // Create
        Warehouse w = new Warehouse();
        w.businessUnitCode = "REPO-TEST-1";
        w.location = "LOC-1";
        w.capacity = 100;
        w.stock = 10;
        w.createdAt = LocalDateTime.now();

        warehouseRepository.create(w);

        // Read
        Warehouse retrieved = warehouseRepository.findByBusinessUnitCode("REPO-TEST-1");
        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals(100, retrieved.capacity);

        // Update
        retrieved.stock = 50;
        warehouseRepository.update(retrieved);

        Warehouse updated = warehouseRepository.findByBusinessUnitCode("REPO-TEST-1");
        Assertions.assertEquals(50, updated.stock);

        // List
        var list = warehouseRepository.getAll();
        Assertions.assertFalse(list.isEmpty());

        // Remove
        warehouseRepository.remove(updated);
        Assertions.assertNull(warehouseRepository.findByBusinessUnitCode("REPO-TEST-1"));
    }
}
