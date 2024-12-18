package org.finos.calm.integration;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.common.QuarkusTestResource;
import org.bson.Document;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.hasItem;

@QuarkusTest
@QuarkusTestResource(EndToEndResource.class)
public class MongoNamespaceIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(MongoNamespaceIntegrationTest.class);

    @BeforeEach
    public void setupNamespaces() {
        String mongoUri = ConfigProvider.getConfig().getValue("quarkus.mongodb.connection-string", String.class);

        // Safeguard: Fail fast if URI is not set
        if (mongoUri == null || mongoUri.isBlank()) {
            logger.error("MongoDB URI is not set. Check the EndToEndResource configuration.");
            throw new IllegalStateException("MongoDB URI is not set. Check the EndToEndResource configuration.");
        }

        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase("calmSchemas");

            // Ensure the 'namespaces' collection exists
            if (!database.listCollectionNames().into(new ArrayList<>()).contains("namespaces")) {
                database.createCollection("namespaces");
            }

            // Insert multiple documents into 'namespaces'
            database.getCollection("namespaces").insertMany(Arrays.asList(
                    new Document("namespace", "finos")
            ));
        }
    }

    @Test
    void performs_end_to_end_confirmation_of_namespaces() {
        given()
                .when().get("/calm/namespaces")
                .then()
                .statusCode(200)
                .body("values", hasItem("finos"));
    }
}
