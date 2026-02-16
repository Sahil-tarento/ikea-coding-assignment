# Java Code Assignment

This is a short code assignment that explores various aspects of software development, including API implementation, documentation, persistence layer handling, and testing.

## About the assignment

You will find the tasks of this assignment on [CODE_ASSIGNMENT](CODE_ASSIGNMENT.md) file

## About the code base

This is based on https://github.com/quarkusio/quarkus-quickstarts

### Requirements

To compile and run this demo you will need:

- JDK 17+

In addition, you will need either a PostgreSQL database, or Docker to run one.

### Configuring JDK 17+

Make sure that `JAVA_HOME` environment variables has been set, and that a JDK 17+ `java` command is on the path.

## Building the demo

Execute the Maven build on the root of the project:

```sh
./mvnw package
```

## Running the demo

### Live coding with Quarkus

The Maven Quarkus plugin provides a development mode that supports
live coding. To try this out:

```sh
./mvnw quarkus:dev
```

In this mode you can make changes to the code and have the changes immediately applied, by just refreshing your browser.

    Hot reload works even when modifying your JPA entities.
    Try it! Even the database schema will be updated on the fly.

## (Optional) Run Quarkus in JVM mode

When you're done iterating in developer mode, you can run the application as a conventional jar file.

First compile it:

```sh
./mvnw package
```

Next we need to make sure you have a PostgreSQL instance running (Quarkus automatically starts one for dev and test mode). To set up a PostgreSQL database with Docker:

```sh
docker run -it --rm=true --name quarkus_test -e POSTGRES_USER=quarkus_test -e POSTGRES_PASSWORD=quarkus_test -e POSTGRES_DB=quarkus_test -p 15432:5432 postgres:13.3
```

Connection properties for the Agroal datasource are defined in the standard Quarkus configuration file,
`src/main/resources/application.properties`.

Then run it:

```sh
java -jar ./target/quarkus-app/quarkus-run.jar
```
    Have a look at how fast it boots.
    Or measure total native memory consumption...


## See the demo in your browser

Navigate to:

<http://localhost:8080/index.html>

Have fun, and join the team of contributors!

## Troubleshooting

Using **IntelliJ**, in case the generated code is not recognized and you have compilation failures, you may need to add `target/.../jaxrs` folder as "generated sources".

## Completed Tasks & Features

### 1. Warehouse Management (Mandatory)
- **Create**: Create new warehouses with validations for Business Unit Code, Location Capacity, and Stock.
- **Archive**: Archive existing warehouses (soft delete).
- **Replace**: Replace an active warehouse with a new one, ensuring stock continuity and capacity checks.
- **List**: View all active warehouses (archived ones are filtered out).
- **Location Support**: Dynamic Location dropdowns populated from the backend.

### 2. Store Management (Mandatory)
- Integrated `LegacyStoreManagerGateway` to trigger only after successful database transaction commit.

### 3. Fulfillment Logistics (Bonus Task)
- **UI Tab**: A new "Fulfillment" tab allows associating Stores, Products, and Warehouses.
- **Validations**: All business constraints regarding fulfillment limits (2 warehouses per product/store, 3 warehouses per store, 5 products per warehouse) are enforced.

## Accessing the Application

Once the application is running (via `java -jar ...` or `mvn quarkus:dev`):

- **Main UI**: [http://localhost:8080/](http://localhost:8080/)
- **Swagger UI (API Docs)**: [http://localhost:8080/q/swagger-ui](http://localhost:8080/q/swagger-ui)

## Testing & Code Coverage

To run the unit and integration tests:

```sh
./mvnw test
```

### Code Coverage (JaCoCo)

We have integrated the **JaCoCo** plugin to track code coverage.

**How to generate the report:**
Running `./mvnw test` automatically generates the coverage report.

**How to view the report:**
Open the following file in your browser:
`target/jacoco-report/index.html`
