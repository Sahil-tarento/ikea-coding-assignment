package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class ArchiveWarehouseUseCaseTest {

    @Inject
    ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @InjectMock
    WarehouseStore warehouseStore;

    @Test
    public void testArchiveSuccess() {
        String buCode = "BU-001";
        Warehouse warehouse = new Warehouse();
        warehouse.businessUnitCode = buCode;
        warehouse.archivedAt = null;

        Mockito.when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(warehouse);

        archiveWarehouseUseCase.archive(buCode);

        Assertions.assertNotNull(warehouse.archivedAt);
        Mockito.verify(warehouseStore).update(warehouse);
    }

    @Test
    public void testArchiveNotFound() {
        String buCode = "BU-404";
        Mockito.when(warehouseStore.findByBusinessUnitCode(buCode)).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> archiveWarehouseUseCase.archive(buCode));
    }
}
