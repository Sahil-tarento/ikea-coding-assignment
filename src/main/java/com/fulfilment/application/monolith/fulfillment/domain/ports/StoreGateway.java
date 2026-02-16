package com.fulfilment.application.monolith.fulfillment.domain.ports;

public interface StoreGateway {
    boolean exists(Long id);
}
