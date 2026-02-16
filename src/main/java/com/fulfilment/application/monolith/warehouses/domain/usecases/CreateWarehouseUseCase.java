package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    // Business Unit Code Verification
    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
        throw new IllegalArgumentException("Business Unit Code already exists");
    }

    // Location Validation
    var location = locationResolver.resolveByIdentifier(warehouse.location);
    if (location == null) {
        throw new IllegalArgumentException("Invalid location");
    }

    // Warehouse Creation Feasibility (Max number of warehouses)
    long warehousesInLocation = warehouseStore.getAll().stream()
            .filter(w -> w.location.equals(warehouse.location))
            .filter(w -> w.archivedAt == null)
            .count();
    
    if (warehousesInLocation >= location.maxNumberOfWarehouses) {
        throw new IllegalArgumentException("Maximum number of warehouses reached for this location");
    }

    // Capacity and Stock Validation
    if (warehouse.stock > warehouse.capacity) {
        throw new IllegalArgumentException("Stock cannot exceed capacity");
    }

    // Location Max Capacity Validation
    int currentTotalCapacity = warehouseStore.getAll().stream()
            .filter(w -> w.location.equals(warehouse.location))
            .filter(w -> w.archivedAt == null)
            .mapToInt(w -> w.capacity)
            .sum();
    
    if (currentTotalCapacity + warehouse.capacity > location.maxCapacity) {
        throw new IllegalArgumentException("Location max capacity exceeded");
    }

    // if all went well, create the warehouse
    warehouseStore.create(warehouse);
  }
}
