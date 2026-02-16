package com.fulfilment.application.monolith.stores;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.InjectMock;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
public class StoreResourceTest {

        @InjectMock
        LegacyStoreManagerGateway legacyStoreManagerGateway;

        @Test
        public void testCreateStore() {
                Store store = new Store();
                store.name = "Test Store";
                store.quantityProductsInStock = 100;

                given()
                                .contentType(ContentType.JSON)
                                .body(store)
                                .when().post("/store")
                                .then()
                                .statusCode(201)
                                .body("id", notNullValue())
                                .body("name", is("Test Store"));

                Mockito.verify(legacyStoreManagerGateway).createStoreOnLegacySystem(Mockito.any(Store.class));
        }

        @Test
        public void testGetStores() {
                given()
                                .when().get("/store")
                                .then()
                                .statusCode(200);
        }

        @Test
        public void testUpdateStore() {
                // First create
                Store store = new Store();
                store.name = "Update Store";
                store.quantityProductsInStock = 10;

                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body(store)
                                .when().post("/store")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                // Then update
                store.name = "Updated Name";

                given()
                                .contentType(ContentType.JSON)
                                .body(store)
                                .when().put("/store/" + id)
                                .then()
                                .statusCode(200)
                                .body("name", is("Updated Name"));

                Mockito.verify(legacyStoreManagerGateway).updateStoreOnLegacySystem(Mockito.any(Store.class));
        }

        @Test
        public void testGetSingleStore() {
                Store store = new Store();
                store.name = "Single Store";
                store.quantityProductsInStock = 10;
                Integer id = given().contentType(ContentType.JSON).body(store).when().post("/store").then()
                                .statusCode(201).extract().path("id");

                given().when().get("/store/" + id).then().statusCode(200).body("name", is("Single Store"));
        }

        @Test
        public void testCreateStoreError() {
                Store store = new Store();
                store.id = 999L;
                store.name = "Bad Store";
                given().contentType(ContentType.JSON).body(store).when().post("/store").then().statusCode(422);
        }

        @Test
        public void testUpdateStoreErrors() {
                // Not Found
                Store store = new Store();
                store.name = "Missing";
                given().contentType(ContentType.JSON).body(store).when().put("/store/9999").then().statusCode(404);

                // Name Null
                Store invalid = new Store();
                given().contentType(ContentType.JSON).body(invalid).when().put("/store/1").then().statusCode(422);
        }

        @Test
        public void testPatchStore() {
                Store store = new Store();
                store.name = "Patch Me";
                Integer id = given().contentType(ContentType.JSON).body(store).when().post("/store").then()
                                .statusCode(201).extract().path("id");

                Store patch = new Store();
                patch.name = "Patched";
                given().contentType(ContentType.JSON).body(patch).when().patch("/store/" + id).then().statusCode(200)
                                .body("name", is("Patched"));

                // Error cases
                given().contentType(ContentType.JSON).body(patch).when().patch("/store/9999").then().statusCode(404);
                Store invalid = new Store();
                given().contentType(ContentType.JSON).body(invalid).when().patch("/store/" + id).then().statusCode(422);
        }

        @Test
        public void testDeleteStoreError() {
                given().when().delete("/store/9999").then().statusCode(404);
        }

        @Test
        public void testDeleteStore() {
                Store store = new Store();
                store.name = "Delete Store";
                store.quantityProductsInStock = 10;

                Integer id = given()
                                .contentType(ContentType.JSON)
                                .body(store)
                                .when().post("/store")
                                .then()
                                .statusCode(201)
                                .extract().path("id");

                given()
                                .when().delete("/store/" + id)
                                .then()
                                .statusCode(204);

                // Try getting it again -> 404
                given()
                                .when().get("/store/" + id)
                                .then()
                                .statusCode(404);
        }
}
