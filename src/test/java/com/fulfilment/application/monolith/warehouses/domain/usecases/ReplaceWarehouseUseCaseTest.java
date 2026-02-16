package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

@QuarkusTest
public class ReplaceWarehouseUseCaseTest {

    @Inject
    ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @InjectMock
    WarehouseStore warehouseStore;

    @InjectMock
    LocationResolver locationResolver;

    @Test
    public void testReplaceSuccessSameLocation() {
        String oldBuCode = "BU-OLD";
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = oldBuCode;
        oldW.stock = 50;
        oldW.location = "LOC-1";

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "BU-NEW";
        newW.stock = 50; // Match
        newW.capacity = 100; // >= stock
        newW.location = "LOC-1";

        Location loc = new Location("LOC-1", 10, 1000);

        Mockito.when(warehouseStore.findByBusinessUnitCode(oldBuCode)).thenReturn(oldW);
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-NEW")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(oldW)); // Only old one exists

        Assertions.assertDoesNotThrow(() -> replaceWarehouseUseCase.replace(oldBuCode, newW));

        Mockito.verify(warehouseStore).update(oldW); // Archived
        Mockito.verify(warehouseStore).create(newW); // Created
    }

    @Test
    public void testReplaceFailStockMismatch() {
        String oldBuCode = "BU-OLD";
        Warehouse oldW = new Warehouse();
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.stock = 60; // Mismatch

        Mockito.when(warehouseStore.findByBusinessUnitCode(oldBuCode)).thenReturn(oldW);

        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace(oldBuCode, newW));
    }

    @Test
    public void testReplaceFailNotFound() {
        Mockito.when(warehouseStore.findByBusinessUnitCode("MISSING")).thenReturn(null);
        Warehouse w = new Warehouse();
        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("MISSING", w));
    }

    @Test
    public void testReplaceFailCapacityTooSmall() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "OLD";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.stock = 50;
        newW.capacity = 40; // Less than stock

        Mockito.when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldW);
        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("OLD", newW));
    }

    @Test
    public void testReplaceFailInvalidLocation() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "OLD";
        oldW.stock = 50;

        Warehouse newW = new Warehouse();
        newW.stock = 50;
        newW.capacity = 60;
        newW.location = "INVALID";

        Mockito.when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldW);
        Mockito.when(locationResolver.resolveByIdentifier("INVALID")).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("OLD", newW));
    }

    @Test
    public void testReplaceFailMaxWarehouses() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "OLD";
        oldW.location = "LOC";
        oldW.stock = 10;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "NEW";
        newW.location = "LOC";
        newW.stock = 10;
        newW.capacity = 20;

        Location loc = new Location("LOC", 1, 1000); // Max 1

        Mockito.when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldW);
        Mockito.when(locationResolver.resolveByIdentifier("LOC")).thenReturn(loc);

        // Mock existing warehouses: OLD is one. replace logic filters out OLD.
        // But if there is ANOTHER one, count will be 1 (filtered OLD) + others.
        // If max is 1, and we have OLD, filtering OLD gives 0. 0 < 1 is OK.
        // We need existing count >= max.
        // So we need OTHER warehouses.
        Warehouse other = new Warehouse();
        other.location = "LOC";
        other.businessUnitCode = "OTHER";

        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(oldW, other));

        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("OLD", newW));
    }

    @Test
    public void testReplaceFailMaxCapacity() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "OLD";
        oldW.location = "LOC";
        oldW.stock = 10;
        oldW.capacity = 10;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "NEW";
        newW.location = "LOC";
        newW.stock = 10;
        newW.capacity = 20;

        Location loc = new Location("LOC", 10, 25); // Max 25

        Mockito.when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldW);
        Mockito.when(locationResolver.resolveByIdentifier("LOC")).thenReturn(loc);

        // OLD (10) + OTHER (10). OLD is filtered out. So current = 10 (OTHER).
        // New (20) + Current (10) = 30 > 25. Fail.
        Warehouse other = new Warehouse();
        other.location = "LOC";
        other.businessUnitCode = "OTHER";
        other.capacity = 10;

        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(oldW, other));

        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("OLD", newW));
    }

    @Test
    public void testReplaceFailNewBuCodeExists() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "OLD";
        oldW.stock = 10;
        oldW.location = "LOC";

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "EXISTING";
        newW.stock = 10;
        newW.capacity = 20;
        newW.location = "LOC";

        Location loc = new Location("LOC", 10, 1000);

        Mockito.when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldW);
        Mockito.when(locationResolver.resolveByIdentifier("LOC")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(oldW));

        // New BU code exists
        Mockito.when(warehouseStore.findByBusinessUnitCode("EXISTING")).thenReturn(new Warehouse());

        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("OLD", newW));
    }

    @Test
    public void testReplaceSameBuCode() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "SAME";
        oldW.stock = 10;
        oldW.location = "LOC";
        oldW.capacity = 20;

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "SAME";
        newW.stock = 10;
        newW.capacity = 30; // Increasing capacity
        newW.location = "LOC";

        Location loc = new Location("LOC", 10, 1000);

        Mockito.when(warehouseStore.findByBusinessUnitCode("SAME")).thenReturn(oldW);
        Mockito.when(locationResolver.resolveByIdentifier("LOC")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(List.of(oldW));

        Assertions.assertDoesNotThrow(() -> replaceWarehouseUseCase.replace("SAME", newW));

        Mockito.verify(warehouseStore).update(newW);
        Mockito.verify(warehouseStore, Mockito.never()).create(Mockito.any());
    }
}
