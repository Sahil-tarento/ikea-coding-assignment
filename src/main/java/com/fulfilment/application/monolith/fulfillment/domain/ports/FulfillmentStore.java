package com.fulfilment.application.monolith.fulfillment.domain.ports;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import java.util.List;

public interface FulfillmentStore {
    List<Fulfillment> getAll();

    void create(Fulfillment fulfillment);

    boolean deleteById(Long id);

    boolean exists(Long storeId, Long productId, String warehouseBusinessUnitCode);

    long countByStoreAndProduct(Long storeId, Long productId);

    List<Fulfillment> findByStoreId(Long storeId);

    List<Fulfillment> findByWarehouseBusinessUnitCode(String warehouseBusinessUnitCode);
}
