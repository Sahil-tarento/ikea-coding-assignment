package com.fulfilment.application.monolith.warehouses.domain.usecases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
class ReplaceWarehouseUseCaseTest {

    @Inject
    ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @InjectMock
    WarehouseStore warehouseStore;

    @InjectMock
    WarehouseValidator warehouseValidator;

    @Test
    public void testReplaceSuccessSameLocation() {
        String oldBuCode = "BU-OLD";
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = oldBuCode;
        oldW.stock = 50;
        oldW.location = "LOC-1";

        Warehouse newW = new Warehouse();
        newW.businessUnitCode = "BU-NEW";
        newW.stock = 50; 
        newW.capacity = 100; 
        newW.location = "LOC-1";

        Mockito.when(warehouseStore.findByBusinessUnitCode(oldBuCode)).thenReturn(oldW);
        Mockito.doNothing().when(warehouseValidator).validateWarehouseReplacement(oldW, newW);

        Assertions.assertDoesNotThrow(() -> replaceWarehouseUseCase.replace(oldBuCode, newW));

        Mockito.verify(warehouseStore).update(oldW); // Archived
        Mockito.verify(warehouseStore).create(newW); // Created
    }

    @Test
    public void testReplaceFailNotFound() {
        Mockito.when(warehouseStore.findByBusinessUnitCode("MISSING")).thenReturn(null);
        Warehouse w = new Warehouse();
        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("MISSING", w));
    }

    @Test
    public void testReplaceFailValidation() {
        Warehouse oldW = new Warehouse();
        oldW.businessUnitCode = "OLD";

        Warehouse newW = new Warehouse();

        Mockito.when(warehouseStore.findByBusinessUnitCode("OLD")).thenReturn(oldW);
        Mockito.doThrow(new IllegalArgumentException("Validation failed")).when(warehouseValidator).validateWarehouseReplacement(oldW, newW);

        Assertions.assertThrows(IllegalArgumentException.class, () -> replaceWarehouseUseCase.replace("OLD", newW));
    }
}
