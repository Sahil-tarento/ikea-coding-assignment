package com.fulfilment.application.monolith.fulfillment.domain.usecases;

import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.domain.ports.FulfillmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductGateway;
import com.fulfilment.application.monolith.fulfillment.domain.ports.StoreGateway;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class CreateFulfillmentUseCase {

    private final FulfillmentStore fulfillmentStore;
    private final WarehouseStore warehouseStore;
    private final ProductGateway productGateway;
    private final StoreGateway storeGateway;

    @Inject
    public CreateFulfillmentUseCase(FulfillmentStore fulfillmentStore, WarehouseStore warehouseStore,
            ProductGateway productGateway, StoreGateway storeGateway) {
        this.fulfillmentStore = fulfillmentStore;
        this.warehouseStore = warehouseStore;
        this.productGateway = productGateway;
        this.storeGateway = storeGateway;
    }

    public void create(Fulfillment fulfillment) {
        // Validate Existence
        if (!productGateway.exists(fulfillment.productId)) {
            throw new EntityNotFoundException("Product not found");
        }
        if (!storeGateway.exists(fulfillment.storeId)) {
            throw new EntityNotFoundException("Store not found");
        }
        if (warehouseStore.findByBusinessUnitCode(fulfillment.warehouseBusinessUnitCode) == null) {
            throw new EntityNotFoundException("Warehouse not found");
        }

        // Validate Uniqueness
        if (fulfillmentStore.exists(fulfillment.storeId, fulfillment.productId,
                fulfillment.warehouseBusinessUnitCode)) {
            throw new EntityExistsException("Fulfillment already exists");
        }

        // Constraint 1: Each Product can be fulfilled by a maximum of 2 different
        // Warehouses per Store
        long productFulfillmentCount = fulfillmentStore.countByStoreAndProduct(fulfillment.storeId,
                fulfillment.productId);
        if (productFulfillmentCount >= 2) {
            throw new IllegalArgumentException("Product is already fulfilled by 2 warehouses for this store");
        }

        // Constraint 2: Each Store can be fulfilled by a maximum of 3 different
        // Warehouses
        List<Fulfillment> storeFulfillments = fulfillmentStore.findByStoreId(fulfillment.storeId);
        List<String> uniqueWarehousesForStore = storeFulfillments.stream()
                .map(f -> f.warehouseBusinessUnitCode)
                .distinct()
                .collect(Collectors.toList());

        if (!uniqueWarehousesForStore.contains(fulfillment.warehouseBusinessUnitCode)
                && uniqueWarehousesForStore.size() >= 3) {
            throw new IllegalArgumentException("Store is already fulfilled by 3 different warehouses");
        }

        // Constraint 3: Each Warehouse can store maximally 5 types of Products
        List<Fulfillment> warehouseFulfillments = fulfillmentStore
                .findByWarehouseBusinessUnitCode(fulfillment.warehouseBusinessUnitCode);
        List<Long> uniqueProductsInWarehouse = warehouseFulfillments.stream()
                .map(f -> f.productId)
                .distinct()
                .collect(Collectors.toList());

        if (!uniqueProductsInWarehouse.contains(fulfillment.productId) && uniqueProductsInWarehouse.size() >= 5) {
            throw new IllegalArgumentException("Warehouse already stores 5 types of products");
        }

        fulfillmentStore.create(fulfillment);
    }
}
