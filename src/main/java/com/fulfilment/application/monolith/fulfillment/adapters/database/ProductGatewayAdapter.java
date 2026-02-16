package com.fulfilment.application.monolith.fulfillment.adapters.database;

import com.fulfilment.application.monolith.fulfillment.domain.ports.ProductGateway;
import com.fulfilment.application.monolith.products.Product;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ProductGatewayAdapter implements ProductGateway {
    @Override
    public boolean exists(Long id) {
        return Product.findById(id) != null;
    }
}
