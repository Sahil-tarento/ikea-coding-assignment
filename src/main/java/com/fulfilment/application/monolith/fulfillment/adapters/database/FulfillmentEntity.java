package com.fulfilment.application.monolith.fulfillment.adapters.database;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "Fulfillment", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "storeId", "productId", "warehouseBusinessUnitCode" })
})
public class FulfillmentEntity extends PanacheEntity {
    public Long storeId;
    public Long productId;
    public String warehouseBusinessUnitCode;
}
