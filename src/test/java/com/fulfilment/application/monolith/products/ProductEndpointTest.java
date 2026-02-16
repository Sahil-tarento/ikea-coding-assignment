package com.fulfilment.application.monolith.products;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class ProductEndpointTest {

  @Test
  public void testCrudProduct() {
    // 1. List - Checking initial state
    given().when().get("/product").then().statusCode(200);

    // 2. Create
    Product p = new Product();
    p.name = "NEW_PROD";
    p.stock = 10;
    p.description = "Desc";
    p.price = java.math.BigDecimal.TEN;

    Integer id = given()
        .contentType(ContentType.JSON)
        .body(p)
        .when().post("/product")
        .then()
        .statusCode(201)
        .extract().path("id");

    // 3. Read Single
    given().when().get("/product/" + id).then().statusCode(200).body("name", is("NEW_PROD"));

    // 4. Update
    p.name = "UPDATED_PROD";
    given()
        .contentType(ContentType.JSON)
        .body(p)
        .when().put("/product/" + id)
        .then()
        .statusCode(200)
        .body("name", is("UPDATED_PROD"));

    // 5. Delete
    given().when().delete("/product/" + id).then().statusCode(204);

    // 6. Read Missing
    given().when().get("/product/" + id).then().statusCode(404);
  }

  @Test
  public void testErrorCases() {
    // Create with ID set
    Product p = new Product();
    p.id = 1L;
    given().contentType(ContentType.JSON).body(p).when().post("/product").then().statusCode(422);

    // Update missing
    p.id = null;
    p.name = "NonExistent";
    given().contentType(ContentType.JSON).body(p).when().put("/product/99999").then().statusCode(404);

    // Update id mismatch? Not checked in code but resource checks invalid id set on
    // create.
    // Resource: "if (product.name == null) throw ... 422"
    Product invalid = new Product();
    given().contentType(ContentType.JSON).body(invalid).when().put("/product/1").then().statusCode(422);
  }
}
