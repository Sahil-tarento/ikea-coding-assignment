package com.fulfilment.application.monolith.fulfillment;

import static io.restassured.RestAssured.given;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.fulfillment.domain.models.Fulfillment;

import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class FulfillmentResourceTest {

        @InjectMock
        WarehouseStore warehouseStore;

        @BeforeEach
        public void setup() {
                // Mock WarehouseStore to return a warehouse for validations
                Mockito.when(warehouseStore.findByBusinessUnitCode("MWH.001")).thenReturn(new Warehouse());

                // Ensure entities exist (assuming test DB is used and reseeding happen or we
                // rely entirely on mocks if resources are mocked)
                // Since we are testing a Resource that uses Panache entities directly, we rely
                // on H2 in-memory DB or similar test profile.
                // However, we just injected mocks for WarehouseStore. Product and Store are
                // active record entities.
        }

        @Test
        public void testCreateFulfillmentSuccess() {
                // Create Store
                com.fulfilment.application.monolith.stores.Store store = new com.fulfilment.application.monolith.stores.Store();
                store.name = "Fulfillment Store";
                store.quantityProductsInStock = 10;
                Integer storeId = given().contentType("application/json").body(store).when().post("/store").then()
                                .statusCode(201).extract().path("id");

                // Create Product
                com.fulfilment.application.monolith.products.Product product = new com.fulfilment.application.monolith.products.Product();
                product.name = "Fulfillment Product";
                product.stock = 10;
                product.price = java.math.BigDecimal.ONE;
                product.description = "Desc";
                Integer productId = given().contentType("application/json").body(product).when().post("/product").then()
                                .statusCode(201).extract().path("id");

                Fulfillment f = new Fulfillment();
                f.storeId = storeId.longValue();
                f.productId = productId.longValue();
                f.warehouseBusinessUnitCode = "MWH.001";

                given()
                                .contentType("application/json")
                                .body(f)
                                .when().post("/fulfillment")
                                .then()
                                .statusCode(201);
        }

        @Test
        public void testCreateFulfillmentFailNotFound() {
                Fulfillment f = new Fulfillment();
                f.storeId = 99999L;
                f.productId = 1L;
                f.warehouseBusinessUnitCode = "MWH.001";

                given()
                                .contentType("application/json")
                                .body(f)
                                .when().post("/fulfillment")
                                .then()
                                .statusCode(404);
        }

        @Test
        public void testMaxProductsPerWarehouseConstraint() {
                String whCode = "MWH.CONSTRAINT";
                Mockito.when(warehouseStore.findByBusinessUnitCode(whCode)).thenReturn(new Warehouse());
                // Create a Store
                com.fulfilment.application.monolith.stores.Store store = new com.fulfilment.application.monolith.stores.Store();
                store.name = "Constraint Store";
                store.quantityProductsInStock = 100;
                Integer storeId = given().contentType("application/json").body(store).when().post("/store").then()
                                .statusCode(201).extract().path("id");

                // Fulfill 5 different products
                for (int i = 0; i < 5; i++) {
                        com.fulfilment.application.monolith.products.Product p = new com.fulfilment.application.monolith.products.Product();
                        p.name = "Prod-" + i;
                        p.stock = 10;
                        p.price = java.math.BigDecimal.ONE;
                        Integer pId = given().contentType("application/json").body(p).when().post("/product").then()
                                        .statusCode(201)
                                        .extract().path("id");

                        Fulfillment f = new Fulfillment();
                        f.storeId = storeId.longValue();
                        f.productId = pId.longValue();
                        f.warehouseBusinessUnitCode = whCode;

                        given().contentType("application/json").body(f).when().post("/fulfillment").then()
                                        .statusCode(201);
                }

                // Try 6th product
                com.fulfilment.application.monolith.products.Product p6 = new com.fulfilment.application.monolith.products.Product();
                p6.name = "Prod-6";
                p6.stock = 10;
                p6.price = java.math.BigDecimal.ONE;
                Integer pId6 = given().contentType("application/json").body(p6).when().post("/product").then()
                                .statusCode(201)
                                .extract().path("id");

                Fulfillment f6 = new Fulfillment();
                f6.storeId = storeId.longValue();
                f6.productId = pId6.longValue();
                f6.warehouseBusinessUnitCode = whCode;

                given().contentType("application/json").body(f6).when().post("/fulfillment").then().statusCode(400);
        }

        @Test
        public void testMaxWarehousesPerProductConstraint() {
                // Constraint: Max 2 warehouses per product per store
                // Create store and product
                com.fulfilment.application.monolith.stores.Store store = new com.fulfilment.application.monolith.stores.Store();
                store.name = "WH Limit Store";
                Integer storeId = given().contentType("application/json").body(store).when().post("/store").then()
                                .statusCode(201).extract().path("id");

                com.fulfilment.application.monolith.products.Product product = new com.fulfilment.application.monolith.products.Product();
                product.name = "WH Limit Prod";
                product.price = java.math.BigDecimal.ONE;
                Integer pId = given().contentType("application/json").body(product).when().post("/product").then()
                                .statusCode(201).extract().path("id");

                // Mock 3 warehouses
                Mockito.when(warehouseStore.findByBusinessUnitCode("WH1")).thenReturn(new Warehouse());
                Mockito.when(warehouseStore.findByBusinessUnitCode("WH2")).thenReturn(new Warehouse());
                Mockito.when(warehouseStore.findByBusinessUnitCode("WH3")).thenReturn(new Warehouse());

                // 1st WH
                Fulfillment f1 = new Fulfillment();
                f1.storeId = storeId.longValue();
                f1.productId = pId.longValue();
                f1.warehouseBusinessUnitCode = "WH1";
                given().contentType("application/json").body(f1).when().post("/fulfillment").then().statusCode(201);

                // 2nd WH
                Fulfillment f2 = new Fulfillment();
                f2.storeId = storeId.longValue();
                f2.productId = pId.longValue();
                f2.warehouseBusinessUnitCode = "WH2";
                given().contentType("application/json").body(f2).when().post("/fulfillment").then().statusCode(201);

                // 3rd WH - Should Fail
                Fulfillment f3 = new Fulfillment();
                f3.storeId = storeId.longValue();
                f3.productId = pId.longValue();
                f3.warehouseBusinessUnitCode = "WH3";
                given().contentType("application/json").body(f3).when().post("/fulfillment").then().statusCode(400);
        }

        @Test
        public void testMaxWarehousesPerStoreConstraint() {
                // Constraint: Max 3 unique warehouses per store
                com.fulfilment.application.monolith.stores.Store store = new com.fulfilment.application.monolith.stores.Store();
                store.name = "Store WH Limit";
                Integer storeId = given().contentType("application/json").body(store).when().post("/store").then()
                                .statusCode(201).extract().path("id");

                // Mock 4 warehouses
                Mockito.when(warehouseStore.findByBusinessUnitCode("SW1")).thenReturn(new Warehouse());
                Mockito.when(warehouseStore.findByBusinessUnitCode("SW2")).thenReturn(new Warehouse());
                Mockito.when(warehouseStore.findByBusinessUnitCode("SW3")).thenReturn(new Warehouse());
                Mockito.when(warehouseStore.findByBusinessUnitCode("SW4")).thenReturn(new Warehouse());

                // Fulfillment 1: Prod1 -> SW1
                com.fulfilment.application.monolith.products.Product p1 = new com.fulfilment.application.monolith.products.Product();
                p1.name = "P1";
                p1.price = java.math.BigDecimal.ONE;
                Integer pId1 = given().contentType("application/json").body(p1).when().post("/product").then()
                                .statusCode(201)
                                .extract().path("id");

                Fulfillment f1 = new Fulfillment();
                f1.storeId = storeId.longValue();
                f1.productId = pId1.longValue();
                f1.warehouseBusinessUnitCode = "SW1";
                given().contentType("application/json").body(f1).when().post("/fulfillment").then().statusCode(201);

                // Fulfillment 2: Prod2 -> SW2
                com.fulfilment.application.monolith.products.Product p2 = new com.fulfilment.application.monolith.products.Product();
                p2.name = "P2";
                p2.price = java.math.BigDecimal.ONE;
                Integer pId2 = given().contentType("application/json").body(p2).when().post("/product").then()
                                .statusCode(201)
                                .extract().path("id");

                Fulfillment f2 = new Fulfillment();
                f2.storeId = storeId.longValue();
                f2.productId = pId2.longValue();
                f2.warehouseBusinessUnitCode = "SW2";
                given().contentType("application/json").body(f2).when().post("/fulfillment").then().statusCode(201);

                // Fulfillment 3: Prod3 -> SW3
                com.fulfilment.application.monolith.products.Product p3 = new com.fulfilment.application.monolith.products.Product();
                p3.name = "P3";
                p3.price = java.math.BigDecimal.ONE;
                Integer pId3 = given().contentType("application/json").body(p3).when().post("/product").then()
                                .statusCode(201)
                                .extract().path("id");

                Fulfillment f3 = new Fulfillment();
                f3.storeId = storeId.longValue();
                f3.productId = pId3.longValue();
                f3.warehouseBusinessUnitCode = "SW3";
                given().contentType("application/json").body(f3).when().post("/fulfillment").then().statusCode(201);

                // Fulfillment 4: Prod4 -> SW4 (Should Fail, 4th unique warehouse)
                com.fulfilment.application.monolith.products.Product p4 = new com.fulfilment.application.monolith.products.Product();
                p4.name = "P4";
                p4.price = java.math.BigDecimal.ONE;
                Integer pId4 = given().contentType("application/json").body(p4).when().post("/product").then()
                                .statusCode(201)
                                .extract().path("id");

                Fulfillment f4 = new Fulfillment();
                f4.storeId = storeId.longValue();
                f4.productId = pId4.longValue();
                f4.warehouseBusinessUnitCode = "SW4";
                given().contentType("application/json").body(f4).when().post("/fulfillment").then().statusCode(400);
        }

        @Test
        public void testDeleteFulfillment() {
                // Create dependencies
                com.fulfilment.application.monolith.stores.Store store = new com.fulfilment.application.monolith.stores.Store();
                store.name = "Fulf Delete Store";
                Integer storeId = given().contentType("application/json").body(store).when().post("/store").then()
                                .statusCode(201).extract().path("id");

                com.fulfilment.application.monolith.products.Product product = new com.fulfilment.application.monolith.products.Product();
                product.name = "Delete Product";
                product.price = java.math.BigDecimal.ONE;
                Integer pId = given().contentType("application/json").body(product).when().post("/product").then()
                                .statusCode(201).extract().path("id");

                // Create Fulfillment
                Fulfillment f = new Fulfillment();
                f.storeId = storeId.longValue();
                f.productId = pId.longValue();
                f.warehouseBusinessUnitCode = "MWH.001"; // Mocked in setup

                Integer id = given().contentType("application/json").body(f).when().post("/fulfillment").then()
                                .statusCode(201)
                                .extract().path("id");

                // Delete
                given().when().delete("/fulfillment/" + id).then().statusCode(204);

                // Delete again -> 404
                given().when().delete("/fulfillment/" + id).then().statusCode(404);
        }

        @Test
        public void testGetAll() {
                given().when().get("/fulfillment").then().statusCode(200);
        }
}
