package com.fulfilment.application.monolith.fulfillment.adapters.restapi;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.Map;

@QuarkusIntegrationTest
public class FulfillmentEndpointIT {

        @Test
        public void testFulfillmentLifecycle() {
                // Since this is a black-box test against the running application (dev mode or
                // jar),
                // we can rely on the data that comes pre-loaded if any, or create our own.
                // Assuming the app starts with seeded data or empty state.
                // We will create dependencies first via API.

                // 1. Create a Store
                Map<String, Object> store = Map.of(
                                "name", "IT Store",
                                "quantityProductsInStock", 100);
                Integer storeId = given()
                                .contentType("application/json")
                                .body(store)
                                .when().post("/store")
                                .then().statusCode(201)
                                .extract().path("id");

                // 2. Create a Product
                Map<String, Object> product = Map.of(
                                "name", "IT Product",
                                "price", 10.50,
                                "stock", 50,
                                "description", "Integration Test Product");
                Integer productId = given()
                                .contentType("application/json")
                                .body(product)
                                .when().post("/product")
                                .then().statusCode(201)
                                .extract().path("id");

                // 3. Create Warehouse (assuming Locations exist or standard logic applies)
                // We'll use one of the existing locations from import.sql if available, or just
                // try MWH.001 which is usually seeded
                String whCode = "MWH.001";

                // 4. Create Fulfillment
                Map<String, Object> fulfillment = Map.of(
                                "storeId", storeId,
                                "productId", productId,
                                "warehouseBusinessUnitCode", whCode);

                Integer fulfillmentId = given()
                                .contentType("application/json")
                                .body(fulfillment)
                                .when().post("/fulfillment")
                                .then().statusCode(201)
                                .extract().path("id");

                // 5. Verify it exists
                given()
                                .when().get("/fulfillment")
                                .then().statusCode(200)
                                .body("find { it.id == " + fulfillmentId + " }.warehouseBusinessUnitCode",
                                                equalTo(whCode));

                // 6. Delete
                given()
                                .when().delete("/fulfillment/" + fulfillmentId)
                                .then().statusCode(204);

                // 7. Verify deletion
                given()
                                .when().delete("/fulfillment/" + fulfillmentId)
                                .then().statusCode(404);
        }
}
