package org.finos.calm.store.mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.finos.calm.domain.NamespaceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@QuarkusTest
public class TestMongoPatternStoreShould {

    @InjectMock
    MongoClient mongoClient;

    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> patternCollection;

    private MongoPatternStore mongoPatternStore;

    @BeforeEach
    void setup() {
        mongoDatabase = Mockito.mock(MongoDatabase.class);
        patternCollection = Mockito.mock(MongoCollection.class);

        when(mongoClient.getDatabase("calmSchemas")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("patterns")).thenReturn(patternCollection);
        mongoPatternStore = new MongoPatternStore(mongoClient);
    }

    @Test
    void get_pattern_for_namespace_that_doesnt_exist_throws_exception() {
        FindIterable<Document> findIterable = Mockito.mock(FindIterable.class);
        when(patternCollection.find(eq(Filters.eq("namespace", "finos"))))
                .thenReturn(findIterable);
        when(findIterable.first()).thenReturn(null);

        assertThrows(NamespaceNotFoundException.class,
                () -> mongoPatternStore.getPatternsForNamespace("finos"));
    }

}
