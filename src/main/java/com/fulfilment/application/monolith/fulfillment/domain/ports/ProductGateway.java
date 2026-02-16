package com.fulfilment.application.monolith.fulfillment.domain.ports;

public interface ProductGateway {
    boolean exists(Long id);
}
