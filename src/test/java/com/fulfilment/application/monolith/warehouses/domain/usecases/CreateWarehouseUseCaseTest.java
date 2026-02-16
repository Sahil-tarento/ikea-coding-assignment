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

import java.util.Collections;
import java.util.List;

@QuarkusTest
public class CreateWarehouseUseCaseTest {

    @Inject
    CreateWarehouseUseCase createWarehouseUseCase;

    @InjectMock
    WarehouseStore warehouseStore;

    @InjectMock
    LocationResolver locationResolver;

    @Test
    public void testCreateSuccess() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        w.location = "LOC-1";
        w.capacity = 100;
        w.stock = 50;

        Location loc = new Location("LOC-1", 10, 1000);
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("LOC-1")).thenReturn(loc);
        Mockito.when(warehouseStore.getAll()).thenReturn(Collections.emptyList());

        Assertions.assertDoesNotThrow(() -> createWarehouseUseCase.create(w));
        Mockito.verify(warehouseStore).create(w);
    }

    @Test
    public void testCreateFailBuExists() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(new Warehouse());

        Assertions.assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(w));
    }

    @Test
    public void testCreateFailInvalidLocation() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        w.location = "INVALID";
        Mockito.when(warehouseStore.findByBusinessUnitCode("BU-1")).thenReturn(null);
        Mockito.when(locationResolver.resolveByIdentifier("INVALID")).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(w));
    }

    @Test
    public void testCreateFailMaxWarehouses() {
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

        Assertions.assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(w));
    }

    @Test
    public void testCreateFailCapacityExceeded() {
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

        Assertions.assertThrows(IllegalArgumentException.class, () -> createWarehouseUseCase.create(w));
    }
}
