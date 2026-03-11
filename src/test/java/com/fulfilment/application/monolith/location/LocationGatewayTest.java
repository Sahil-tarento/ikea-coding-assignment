package com.fulfilment.application.monolith.location;

import org.junit.jupiter.api.Test;
public class LocationGatewayTest {

  @Test
  public void testWhenResolveExistingLocationShouldReturn() {
    // given
    LocationGateway locationGateway = new LocationGateway();

    // when
    com.fulfilment.application.monolith.warehouses.domain.models.Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    // then
    org.junit.jupiter.api.Assertions.assertNotNull(location);
    org.junit.jupiter.api.Assertions.assertEquals(location.identification, "ZWOLLE-001");
  }

  @Test
  public void testResolveNonExistingLocationShouldReturnNull() {
    LocationGateway locationGateway = new LocationGateway();
    com.fulfilment.application.monolith.warehouses.domain.models.Location location = locationGateway.resolveByIdentifier("NON-EXISTING");
    org.junit.jupiter.api.Assertions.assertNull(location);
  }

  @Test
  public void testGetAllLocationsShouldReturnList() {
    LocationGateway locationGateway = new LocationGateway();
    java.util.List<com.fulfilment.application.monolith.warehouses.domain.models.Location> locations = locationGateway.getAllLocations();
    org.junit.jupiter.api.Assertions.assertNotNull(locations);
    org.junit.jupiter.api.Assertions.assertFalse(locations.isEmpty());
    org.junit.jupiter.api.Assertions.assertEquals(8, locations.size());
  }
}
