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
        String id = "1";
        Warehouse warehouse = new Warehouse();
        warehouse.id = id;
        warehouse.businessUnitCode = "BU-001";
        warehouse.archivedAt = null;

        Mockito.when(warehouseStore.findById(id)).thenReturn(warehouse);

        archiveWarehouseUseCase.archive(id);

        Assertions.assertNotNull(warehouse.archivedAt);
        Mockito.verify(warehouseStore).update(warehouse);
    }

    @Test
    public void testArchiveNotFound() {
        String id = "some-id";
        Mockito.when(warehouseStore.findById(id)).thenReturn(null);

        Assertions.assertThrows(IllegalArgumentException.class, () -> archiveWarehouseUseCase.archive(id));
    }
}
