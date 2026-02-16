package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.*;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@QuarkusTest
public class WarehouseResourceImplTest {

    @InjectMock
    WarehouseRepository warehouseRepository;
    @InjectMock
    CreateWarehouseOperation createWarehouseOperation;
    @InjectMock
    ArchiveWarehouseOperation archiveWarehouseOperation;
    @InjectMock
    ReplaceWarehouseOperation replaceWarehouseOperation;
    @InjectMock
    LocationResolver locationResolver;

    @Test
    public void testListWarehouses() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        w.location = "LOC-1";
        w.capacity = 100;
        w.stock = 10;

        Mockito.when(warehouseRepository.getAll()).thenReturn(List.of(w));

        given()
                .when().get("/warehouse")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("[0].businessUnitCode", is("BU-1"));
    }

    @Test
    public void testCreateWarehouse() {
        com.warehouse.api.beans.Warehouse dto = new com.warehouse.api.beans.Warehouse();
        dto.setBusinessUnitCode("BU-NEW");
        dto.setLocation("LOC-1");
        dto.setCapacity(100);
        dto.setStock(10);

        given()
                .contentType("application/json")
                .body(dto)
                .when().post("/warehouse")
                .then()
                .statusCode(200)
                .body("businessUnitCode", is("BU-NEW"));

        Mockito.verify(createWarehouseOperation).create(Mockito.any());
    }

    @Test
    public void testListLocations() {
        Location loc = new Location("LOC-TEST", 1, 100);
        Mockito.when(locationResolver.getAllLocations()).thenReturn(List.of(loc));

        given()
                .when().get("/warehouse/locations")
                .then()
                .statusCode(200)
                .body("[0].identification", is("LOC-TEST"));
    }

    @Test
    public void testArchiveWarehouse() {
        given()
                .when().delete("/warehouse/BU-1")
                .then()
                .statusCode(204);

        Mockito.verify(archiveWarehouseOperation).archive("BU-1");
    }

    @Test
    public void testReplaceWarehouse() {
        String oldBuCode = "BU-OLD";
        com.warehouse.api.beans.Warehouse newDto = new com.warehouse.api.beans.Warehouse();
        newDto.setBusinessUnitCode("BU-NEW");
        newDto.setStock(50);

        Warehouse newWarehouse = new Warehouse();
        newWarehouse.businessUnitCode = "BU-NEW";
        Mockito.when(warehouseRepository.findByBusinessUnitCode("BU-NEW")).thenReturn(newWarehouse);

        given()
                .contentType("application/json")
                .body(newDto)
                .when().post("/warehouse/" + oldBuCode + "/replacement")
                .then()
                .statusCode(200);

        Mockito.verify(replaceWarehouseOperation).replace(Mockito.eq(oldBuCode), Mockito.any());
    }

    @Test
    public void testCreateWarehouseError() {
        com.warehouse.api.beans.Warehouse dto = new com.warehouse.api.beans.Warehouse();
        dto.setBusinessUnitCode("BU-FAIL");

        Mockito.doThrow(new IllegalArgumentException("Invalid")).when(createWarehouseOperation).create(Mockito.any());

        given()
                .contentType("application/json")
                .body(dto)
                .when().post("/warehouse")
                .then()
                .statusCode(400);
    }

    @Test
    public void testGetSingleWarehouse() {
        Warehouse w = new Warehouse();
        w.businessUnitCode = "BU-1";
        Mockito.when(warehouseRepository.findByBusinessUnitCode("BU-1")).thenReturn(w);

        given()
                .when().get("/warehouse/BU-1")
                .then()
                .statusCode(200)
                .body("businessUnitCode", is("BU-1"));

    }
}
