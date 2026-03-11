package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class WarehouseValidator {

    private final WarehouseStore warehouseStore;
    private final LocationResolver locationResolver;

    @Inject
    public WarehouseValidator(WarehouseStore warehouseStore, LocationResolver locationResolver) {
        this.warehouseStore = warehouseStore;
        this.locationResolver = locationResolver;
    }

    public void validateNewWarehouseCreation(Warehouse warehouse) {
        // Business Unit Code Verification
        if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
            throw new IllegalArgumentException("Business Unit Code already exists");
        }

        Location location = resolveAndValidateLocation(warehouse.location);
        validateLocationMaxWarehouses(location, warehouse.location, null);
        validateStockVsCapacity(warehouse.stock, warehouse.capacity);
        validateLocationMaxCapacity(location, warehouse.location, warehouse.capacity, null);
    }

    public void validateWarehouseReplacement(Warehouse oldWarehouse, Warehouse newWarehouse) {
        if (newWarehouse.stock == null || !newWarehouse.stock.equals(oldWarehouse.stock)) {
            throw new IllegalArgumentException("Stock of new warehouse must match stock of previous warehouse");
        }

        if (newWarehouse.capacity < oldWarehouse.stock) {
            throw new IllegalArgumentException("New warehouse capacity must accommodate stock from previous warehouse");
        }

        validateStockVsCapacity(newWarehouse.stock, newWarehouse.capacity);

        Location location = resolveAndValidateLocation(newWarehouse.location);
        validateLocationMaxWarehouses(location, newWarehouse.location, oldWarehouse.businessUnitCode);
        validateLocationMaxCapacity(location, newWarehouse.location, newWarehouse.capacity, oldWarehouse.businessUnitCode);

        if (!oldWarehouse.businessUnitCode.equals(newWarehouse.businessUnitCode)) {
             if (warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode) != null) {
                 throw new IllegalArgumentException("New Business Unit Code already exists");
             }
        }
    }

    private Location resolveAndValidateLocation(String locationId) {
        Location location = locationResolver.resolveByIdentifier(locationId);
        if (location == null) {
            throw new IllegalArgumentException("Invalid location");
        }
        return location;
    }

    private void validateStockVsCapacity(Integer stock, Integer capacity) {
        if (stock > capacity) {
            throw new IllegalArgumentException("Stock cannot exceed capacity");
        }
    }

    private void validateLocationMaxWarehouses(Location location, String locationId, String excludedBusinessUnitCode) {
        long activeWarehousesInLocation = warehouseStore.getAll().stream()
                .filter(w -> w.location.equals(locationId))
                .filter(w -> w.archivedAt == null)
                .filter(w -> excludedBusinessUnitCode == null || !w.businessUnitCode.equals(excludedBusinessUnitCode))
                .count();

        if (activeWarehousesInLocation >= location.maxNumberOfWarehouses) {
            throw new IllegalArgumentException("Maximum number of warehouses reached for this location");
        }
    }

    private void validateLocationMaxCapacity(Location location, String locationId, int newCapacity, String excludedBusinessUnitCode) {
        int currentTotalCapacity = warehouseStore.getAll().stream()
                .filter(w -> w.location.equals(locationId))
                .filter(w -> w.archivedAt == null)
                .filter(w -> excludedBusinessUnitCode == null || !w.businessUnitCode.equals(excludedBusinessUnitCode))
                .mapToInt(w -> w.capacity)
                .sum();

        if (currentTotalCapacity + newCapacity > location.maxCapacity) {
            throw new IllegalArgumentException("Location max capacity exceeded");
        }
    }
}
