package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FulfillmentRepositoryAdapter implements FulfillmentStore {

    @Override
    public List<Fulfillment> getAll() {
        return FulfillmentEntity.listAll().stream()
                .map(e -> toDomain((FulfillmentEntity) e))
                .collect(Collectors.toList());
    }

    @Override
    public void create(Fulfillment fulfillment) {
        FulfillmentEntity entity = toEntity(fulfillment);
        entity.persist();
        fulfillment.id = entity.id;
    }

    @Override
    public boolean deleteById(Long id) {
        return FulfillmentEntity.deleteById(id);
    }

    @Override
    public boolean exists(Long storeId, Long productId, String warehouseBusinessUnitCode) {
        return FulfillmentEntity.count("storeId = ?1 and productId = ?2 and warehouseBusinessUnitCode = ?3",
                storeId, productId, warehouseBusinessUnitCode) > 0;
    }

    @Override
    public long countByStoreAndProduct(Long storeId, Long productId) {
        return FulfillmentEntity.count("storeId = ?1 and productId = ?2", storeId, productId);
    }

    @Override
    public List<Fulfillment> findByStoreId(Long storeId) {
        return FulfillmentEntity.list("storeId", storeId).stream()
                .map(e -> toDomain((FulfillmentEntity) e))
                .collect(Collectors.toList());
    }

    @Override
    public List<Fulfillment> findByWarehouseBusinessUnitCode(String warehouseBusinessUnitCode) {
        return FulfillmentEntity.list("warehouseBusinessUnitCode", warehouseBusinessUnitCode).stream()
                .map(e -> toDomain((FulfillmentEntity) e))
                .collect(Collectors.toList());
    }

    private Fulfillment toDomain(FulfillmentEntity entity) {
        Fulfillment fulfillment = new Fulfillment();
        fulfillment.id = entity.id;
        fulfillment.storeId = entity.storeId;
        fulfillment.productId = entity.productId;
        fulfillment.warehouseBusinessUnitCode = entity.warehouseBusinessUnitCode;
        return fulfillment;
    }

    private FulfillmentEntity toEntity(Fulfillment fulfillment) {
        FulfillmentEntity entity = new FulfillmentEntity();
        // ID is managed by DB usually, checking if we need to set it
        if (fulfillment.id != null) {
            entity.id = fulfillment.id;
        }
        entity.storeId = fulfillment.storeId;
        entity.productId = fulfillment.productId;
        entity.warehouseBusinessUnitCode = fulfillment.warehouseBusinessUnitCode;
        return entity;
    }
}
