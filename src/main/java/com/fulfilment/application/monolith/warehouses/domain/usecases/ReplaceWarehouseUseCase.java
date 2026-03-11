package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import java.time.LocalDateTime;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void replace(String oldBusinessUnitCode, Warehouse newWarehouse) {
    Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(oldBusinessUnitCode);
    if (oldWarehouse == null) {
        throw new IllegalArgumentException("Warehouse to replace not found");
    }

    warehouseValidator.validateWarehouseReplacement(oldWarehouse, newWarehouse);

    // Execute Replacement
    // Archive Old
    oldWarehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(oldWarehouse);
    
    // Create New
    if (newWarehouse.createdAt == null) {
        newWarehouse.createdAt = LocalDateTime.now();
    }
    warehouseStore.create(newWarehouse);
  }
}
