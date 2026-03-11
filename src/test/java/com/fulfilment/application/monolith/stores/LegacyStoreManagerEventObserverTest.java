package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
public class LegacyStoreManagerEventObserverTest {

    @Test
    public void testOnStoreEventCreate() {
        // Arrange
        LegacyStoreManagerGateway gateway = Mockito.mock(LegacyStoreManagerGateway.class);
        LegacyStoreManagerEventObserver observer = new LegacyStoreManagerEventObserver();
        observer.legacyStoreManagerGateway = gateway;

        Store store = new Store();
        StoreEvent event = new StoreEvent(store, StoreEvent.Action.CREATE);
        
        // Act
        observer.onStoreEvent(event);
        
        // Assert
        Mockito.verify(gateway).createStoreOnLegacySystem(store);
        Mockito.verifyNoMoreInteractions(gateway);
    }
    
    @Test
    public void testOnStoreEventUpdate() {
        // Arrange
        LegacyStoreManagerGateway gateway = Mockito.mock(LegacyStoreManagerGateway.class);
        LegacyStoreManagerEventObserver observer = new LegacyStoreManagerEventObserver();
        observer.legacyStoreManagerGateway = gateway;

        Store store = new Store();
        StoreEvent event = new StoreEvent(store, StoreEvent.Action.UPDATE);
        
        // Act
        observer.onStoreEvent(event);
        
        // Assert
        Mockito.verify(gateway).updateStoreOnLegacySystem(store);
        Mockito.verifyNoMoreInteractions(gateway);
    }
}
