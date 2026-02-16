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
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(String oldBusinessUnitCode, Warehouse newWarehouse) {
    Warehouse oldWarehouse = warehouseStore.findByBusinessUnitCode(oldBusinessUnitCode);
    if (oldWarehouse == null) {
        throw new IllegalArgumentException("Warehouse to replace not found");
    }

    if (newWarehouse.stock == null || !newWarehouse.stock.equals(oldWarehouse.stock)) {
        throw new IllegalArgumentException("Stock of new warehouse must match stock of previous warehouse");
    }

    if (newWarehouse.capacity < oldWarehouse.stock) {
        throw new IllegalArgumentException("New warehouse capacity must accommodate stock from previous warehouse");
    }

    if (newWarehouse.capacity < newWarehouse.stock) {
         throw new IllegalArgumentException("Stock cannot exceed capacity");
    }
    
    // Location Validation
    var location = locationResolver.resolveByIdentifier(newWarehouse.location);
    if (location == null) {
        throw new IllegalArgumentException("Invalid location");
    }

    // Max Warehouses check
    long activeWarehousesInLocation = warehouseStore.getAll().stream()
            .filter(w -> w.location.equals(newWarehouse.location))
            .filter(w -> w.archivedAt == null)
            .filter(w -> !w.businessUnitCode.equals(oldBusinessUnitCode)) 
            .count();

    if (activeWarehousesInLocation >= location.maxNumberOfWarehouses) {
         throw new IllegalArgumentException("Maximum number of warehouses reached for this location");
    }

    // Max Capacity Validation
    int currentTotalCapacity = warehouseStore.getAll().stream()
            .filter(w -> w.location.equals(newWarehouse.location))
            .filter(w -> w.archivedAt == null)
            .filter(w -> !w.businessUnitCode.equals(oldBusinessUnitCode))
            .mapToInt(w -> w.capacity)
            .sum();

    if (currentTotalCapacity + newWarehouse.capacity > location.maxCapacity) {
        throw new IllegalArgumentException("Location max capacity exceeded");
    }

    // Execute Replacement
    if (oldBusinessUnitCode.equals(newWarehouse.businessUnitCode)) {
        // Just Update
        warehouseStore.update(newWarehouse);
    } else {
        // Archive Old
        oldWarehouse.archivedAt = LocalDateTime.now();
        warehouseStore.update(oldWarehouse);
        
        // Create New
        if (warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode) != null) {
             throw new IllegalArgumentException("New Business Unit Code already exists");
        }
        // Ensure new creation uses the creation timestamp if not provided, or keeps current if appropriate. 
        // Usually creation should have createdAt. UseCase caller (Resource) might set it, or we set it here.
        if (newWarehouse.createdAt == null) {
            newWarehouse.createdAt = LocalDateTime.now();
        }
        warehouseStore.create(newWarehouse);
    }
  }
}
