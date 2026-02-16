package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.ports.*;
import com.warehouse.api.WarehouseResource;
import com.warehouse.api.beans.Warehouse;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RequestScoped
public class WarehouseResourceImpl implements WarehouseResource {

  @Inject
  private WarehouseRepository warehouseRepository;
  @Inject
  private CreateWarehouseOperation createWarehouseOperation;
  @Inject
  private ArchiveWarehouseOperation archiveWarehouseOperation;
  @Inject
  private ReplaceWarehouseOperation replaceWarehouseOperation;

  @Override
  public List<Warehouse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream()
        .filter(w -> w.archivedAt == null)
        .map(this::toWarehouseResponse).toList();
  }

  @Override
  public Warehouse createANewWarehouseUnit(@NotNull Warehouse data) {
    try {
      var domainWarehouse = toDomain(data);
      createWarehouseOperation.create(domainWarehouse);
      return toWarehouseResponse(domainWarehouse);
    } catch (IllegalArgumentException e) {
      throw new jakarta.ws.rs.WebApplicationException(e.getMessage(), 400);
    }
  }

  @Override
  public Warehouse getAWarehouseUnitByID(String id) {
    var domainWarehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (domainWarehouse == null) {
      throw new jakarta.ws.rs.WebApplicationException("Warehouse not found", 404);
    }
    return toWarehouseResponse(domainWarehouse);
  }

  @Override
  public void archiveAWarehouseUnitByID(String id) {
    try {
      archiveWarehouseOperation.archive(id);
    } catch (IllegalArgumentException e) {
      throw new jakarta.ws.rs.WebApplicationException(e.getMessage(), 404);
    }
  }

  @Inject
  private LocationResolver locationResolver;

  @Override
  public List<com.warehouse.api.beans.Location> listAllAvailableWarehouseLocations() {
    return locationResolver.getAllLocations().stream().map(this::toLocationResponse).toList();
  }

  private com.warehouse.api.beans.Location toLocationResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Location location) {
    var response = new com.warehouse.api.beans.Location();
    response.setIdentification(location.identification);
    response.setMaxNumberOfWarehouses(location.maxNumberOfWarehouses);
    response.setMaxCapacity(location.maxCapacity);
    return response;
  }

  @Override
  public Warehouse replaceTheCurrentActiveWarehouse(
      String businessUnitCode, @NotNull Warehouse data) {
    try {
      var domainWarehouse = toDomain(data);
      replaceWarehouseOperation.replace(businessUnitCode, domainWarehouse);
      // Return the updated or new warehouse.
      // If buCode changed, we should probably return the one corresponding to
      // data.getBusinessUnitCode()
      // which is what we saved.
      return toWarehouseResponse(warehouseRepository.findByBusinessUnitCode(data.getBusinessUnitCode()));
    } catch (IllegalArgumentException e) {
      throw new jakarta.ws.rs.WebApplicationException(e.getMessage(), 400);
    }
  }

  private Warehouse toWarehouseResponse(
      com.fulfilment.application.monolith.warehouses.domain.models.Warehouse warehouse) {
    var response = new Warehouse();
    response.setBusinessUnitCode(warehouse.businessUnitCode);
    response.setLocation(warehouse.location);
    response.setCapacity(warehouse.capacity);
    response.setStock(warehouse.stock);

    return response;
  }

  private com.fulfilment.application.monolith.warehouses.domain.models.Warehouse toDomain(Warehouse data) {
    var w = new com.fulfilment.application.monolith.warehouses.domain.models.Warehouse();
    w.businessUnitCode = data.getBusinessUnitCode();
    w.location = data.getLocation();
    w.capacity = data.getCapacity();
    w.stock = data.getStock();
    if (w.createdAt == null)
      w.createdAt = java.time.LocalDateTime.now();
    return w;
  }
}
