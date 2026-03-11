package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Collections;
import java.util.List;
public class WarehouseValidatorTest {

    private WarehouseStore warehouseStore;
    private LocationResolver locationResolver;
    private WarehouseValidator warehouseValidator;

    @BeforeEach
    public void setup() {
        warehouseStore = Mockito.mock(WarehouseStore.class);
        locationResolver = Mockito.mock(LocationResolver.class);
        warehouseValidator = new WarehouseValidator(warehouseStore, locationResolver);
    }

    @Test
    public void testValidateNewWarehouseCreation_Success() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        w.location = "LOC-1";
        w.capacity = 100;
        w.stock = 50;

        Location loc = new Location("LOC-1", 10, 1000);
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(Collections.emptyList());

        Assertions.assertDoesNotThrow(() -> warehouseValidator.validateNewWarehouseCreation(w));
    }

    @Test
    public void testValidateNewWarehouseCreation_FailsWhenBuExists() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(new Warehouse());

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateNewWarehouseCreation(w));
        Assertions.assertEquals("Business Unit Code already exists", ex.getMessage());
    }

    @Test
    public void testValidateNewWarehouseCreation_FailsWhenLocationInvalid() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        w.location = "INVALID";
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("INVALID")).thenReturn(null);

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateNewWarehouseCreation(w));
        Assertions.assertEquals("Invalid location", ex.getMessage());
    }

    @Test
    public void testValidateNewWarehouseCreation_FailsWhenStockExceedsCapacity() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        w.location = "LOC-1";
        w.capacity = 50;
        w.stock = 100; // Stock > Capacity

        Location loc = new Location("LOC-1", 10, 1000);
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(loc);

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateNewWarehouseCreation(w));
        Assertions.assertEquals("Stock cannot exceed capacity", ex.getMessage());
    }

    @Test
    public void testValidateNewWarehouseCreation_FailsWhenMaxWarehousesReached() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-NEW";
        w.location = "LOC-1";
        w.capacity = 100;
        w.stock = 50;

        Location loc = new Location("LOC-1", 1, 1000); // Max 1

        Warehouse existing = new Warehouse();
        existing.location = "LOC-1";
        existing.businessUnitCode = "BU-EXIST";

        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-NEW")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(existing));

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateNewWarehouseCreation(w));
        Assertions.assertEquals("Maximum number of warehouses reached for this location", ex.getMessage());
    }

    @Test
    public void testValidateWarehouseReplacement_Success() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "BU-OLD";
        oldW.location = "LOC-1";
        oldW.capacity = 100;
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "BU-NEW";
        newW.location = "LOC-2";
        newW.capacity = 150;
        newW.stock = 50; // Must match old stock

        Location loc = new Location("LOC-2", 10, 1000);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-2")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(Collections.emptyList());
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-NEW")).thenReturn(null);

        Assertions.assertDoesNotThrow(() -> warehouseValidator.validateWarehouseReplacement(oldW, newW));
    }

    @Test
    public void testValidateWarehouseReplacement_FailsWhenStockMismatches() {
        Warehouse oldW = new Warehouse();
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.stock = 60; // Mismatch

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateWarehouseReplacement(oldW, newW));
        Assertions.assertEquals("Stock of new warehouse must match stock of previous warehouse", ex.getMessage());
    }

    @Test
    public void testValidateWarehouseReplacement_FailsWhenCapacityTooSmall() {
        Warehouse oldW = new Warehouse();
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.stock = 50;
        newW.capacity = 40; // Too small for old stock

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateWarehouseReplacement(oldW, newW));
        Assertions.assertEquals("New warehouse capacity must accommodate stock from previous warehouse", ex.getMessage());
    }

    @Test
    public void testValidateNewWarehouseCreation_FailsWhenMaxCapacityExceeded() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-NEW";
        w.location = "LOC-1";
        w.capacity = 900;
        w.stock = 50;

        Location loc = new Location("LOC-1", 5, 1000); // Max cap 1000

        Warehouse existing = new Warehouse();
        existing.location = "LOC-1";
        existing.businessUnitCode = "BU-EXIST";
        existing.capacity = 200; // 200 + 900 = 1100 > 1000

        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-NEW")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(existing));

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateNewWarehouseCreation(w));
        Assertions.assertEquals("Location max capacity exceeded", ex.getMessage());
    }

    @Test
    public void testValidateWarehouseReplacement_FailsWhenNewBuExists() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "BU-OLD";
        oldW.location = "LOC-1";
        oldW.stock = 50;
        oldW.capacity = 100;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "BU-NEW"; // Different
        newW.location = "LOC-2";
        newW.stock = 50;
        newW.capacity = 100;

        Location loc = new Location("LOC-2", 10, 1000);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-2")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(Collections.emptyList());
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-NEW")).thenReturn(new Warehouse()); // Already exists

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateWarehouseReplacement(oldW, newW));
        Assertions.assertEquals("New Business Unit Code already exists", ex.getMessage());
    }

    @Test
    public void testValidateWarehouseReplacement_ExceedsMaxWarehouses() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "BU-OLD";
        oldW.location = "LOC-1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "BU-NEW";
        newW.location = "LOC-2";
        newW.stock = 50;
        newW.capacity = 100;

        Location loc = new Location("LOC-2", 1, 1000); // Max 1 warehouse

        Warehouse otherActive = new Warehouse();
        otherActive.businessUnitCode = "BU-OTHER";
        otherActive.location = "LOC-2";

        Mockito.when(locationResolver.resolveByIdentifier("LOC-2")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(otherActive));

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateWarehouseReplacement(oldW, newW));
        Assertions.assertEquals("Maximum number of warehouses reached for this location", ex.getMessage());
    }

    @Test
    public void testValidateWarehouseReplacement_ExceedsMaxCapacity() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "BU-OLD";
        oldW.location = "LOC-1";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "BU-NEW";
        newW.location = "LOC-2";
        newW.stock = 50;
        newW.capacity = 900;

        Location loc = new Location("LOC-2", 10, 1000); // 1000 max

        Warehouse otherActive = new Warehouse();
        otherActive.businessUnitCode = "BU-OTHER";
        otherActive.location = "LOC-2";
        otherActive.capacity = 200;

        Mockito.when(locationResolver.resolveByIdentifier("LOC-2")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(otherActive));

        IllegalArgumentException ex = Assertions.assertThrows(IllegalArgumentException.class, 
            () -> warehouseValidator.validateWarehouseReplacement(oldW, newW));
        Assertions.assertEquals("Location max capacity exceeded", ex.getMessage());
    }
}
