package org.finos.calm.store.mongo;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.json.JsonParseException;
import org.finos.calm.domain.NamespaceNotFoundException;
import org.finos.calm.domain.Pattern;
import org.finos.calm.domain.PatternNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@QuarkusTest
public class TestMongoPatternStoreShould {

    @InjectMock
    MongoClient mongoClient;

    @InjectMock
    MongoCounterStore counterStore;

    @InjectMock
    MongoNamespaceStore namespaceStore;

    private MongoDatabase mongoDatabase;
    private MongoCollection<Document> patternCollection;

    private MongoPatternStore mongoPatternStore;

    private final String validJson = "{\"test\":\"test\"}";

    @BeforeEach
    void setup() {
        mongoDatabase = Mockito.mock(MongoDatabase.class);
        patternCollection = Mockito.mock(MongoCollection.class);

        when(mongoClient.getDatabase("calmSchemas")).thenReturn(mongoDatabase);
        when(mongoDatabase.getCollection("patterns")).thenReturn(patternCollection);
        mongoPatternStore = new MongoPatternStore(mongoClient, counterStore, namespaceStore);
    }

    @Test
    void get_pattern_for_namespace_that_doesnt_exist_throws_exception() {
        when(namespaceStore.namespaceExists(anyString())).thenReturn(false);
        String namespace = "does-not-exist";

        assertThrows(NamespaceNotFoundException.class,
                () -> mongoPatternStore.getPatternsForNamespace(namespace));

        verify(namespaceStore).namespaceExists(namespace);
    }

    @Test
    void get_pattern_for_namespace_returns_empty_list_when_none_exist() throws NamespaceNotFoundException {
        FindIterable<Document> findIterable = Mockito.mock(FindIterable.class);
        when(namespaceStore.namespaceExists(anyString())).thenReturn(true);
        when(patternCollection.find(eq(Filters.eq("namespace", "finos"))))
                .thenReturn(findIterable);
        Document documentMock = Mockito.mock(Document.class);
        when(findIterable.first()).thenReturn(documentMock);
        when(documentMock.getList("patterns", Document.class))
                .thenReturn(new ArrayList<>());

        assertThat(mongoPatternStore.getPatternsForNamespace("finos"), is(empty()));
        verify(namespaceStore).namespaceExists("finos");
    }

    @Test
    void get_pattern_for_namespace_returns_values() throws NamespaceNotFoundException {
        FindIterable<Document> findIterable = Mockito.mock(FindIterable.class);
        when(namespaceStore.namespaceExists(anyString())).thenReturn(true);
        when(patternCollection.find(eq(Filters.eq("namespace", "finos"))))
                .thenReturn(findIterable);
        Document documentMock = Mockito.mock(Document.class);
        when(findIterable.first()).thenReturn(documentMock);

        Document doc1 = new Document("patternId", 1001);
        Document doc2 = new Document("patternId", 1002);

        when(documentMock.getList("patterns", Document.class))
                .thenReturn(Arrays.asList(doc1, doc2));

        List<Integer> patternIds = mongoPatternStore.getPatternsForNamespace("finos");

        assertThat(patternIds, is(Arrays.asList(1001, 1002)));
        verify(namespaceStore).namespaceExists("finos");
    }

    @Test
    void return_a_namespace_exception_when_namespace_does_not_exist() {
        when(namespaceStore.namespaceExists(anyString())).thenReturn(false);
        String namespace = "does-not-exist";
        Pattern pattern = new Pattern.PatternBuilder().setNamespace(namespace).build();

        assertThrows(NamespaceNotFoundException.class,
                () -> mongoPatternStore.createPatternForNamespace(pattern));

        verify(namespaceStore).namespaceExists(namespace);
    }

    @Test
    void return_a_json_parse_exception_when_an_invalid_json_object_is_presented() {
        when(namespaceStore.namespaceExists(anyString())).thenReturn(true);
        when(counterStore.getNextSequenceValue()).thenReturn(42);
        Pattern pattern = new Pattern.PatternBuilder().setNamespace("finos")
                .setPattern("Invalid JSON")
                .build();

        assertThrows(JsonParseException.class,
                () -> mongoPatternStore.createPatternForNamespace(pattern));
    }

    @Test
    void return_created_pattern_when_parameters_are_valid() throws NamespaceNotFoundException {
        String validNamespace = "finos";
        int sequenceNumber = 42;
        when(namespaceStore.namespaceExists(anyString())).thenReturn(true);
        when(counterStore.getNextSequenceValue()).thenReturn(sequenceNumber);
        Pattern patternToCreate = new Pattern.PatternBuilder().setPattern(validJson)
                .setNamespace(validNamespace)
                .build();

        Pattern pattern = mongoPatternStore.createPatternForNamespace(patternToCreate);

        Pattern expectedPattern = new Pattern.PatternBuilder().setPattern(validJson)
                .setNamespace(validNamespace)
                .setVersion("1.0.0")
                .setId(sequenceNumber)
                .build();

        assertThat(pattern, is(expectedPattern));
        Document expectedDoc = new Document("patternId", pattern.getId()).append("versions",
                new Document("1.0.0", Document.parse(pattern.getPatternJson())));

        verify(patternCollection).updateOne(
                eq(Filters.eq("namespace", validNamespace)),
                eq(Updates.push("patterns", expectedDoc)),
                any(UpdateOptions.class));
    }

    @Test
    void get_pattern_version_for_invalid_namespace_throws_exception() {
        when(namespaceStore.namespaceExists(anyString())).thenReturn(false);
        Pattern pattern = new Pattern.PatternBuilder().setNamespace("does-not-exist").build();

        assertThrows(NamespaceNotFoundException.class,
                () -> mongoPatternStore.getPatternVersions(pattern));

        verify(namespaceStore).namespaceExists(pattern.getNamespace());
    }

    @Test
    void get_pattern_version_for_invalid_pattern_throws_exception() {
        FindIterable<Document> findIterable = Mockito.mock(FindIterable.class);
        when(namespaceStore.namespaceExists(anyString())).thenReturn(true);
        //Return the same find iterable as the projection unboxes, then return null
        when(patternCollection.find(any(Bson.class)))
                .thenReturn(findIterable);
        when(findIterable.projection(any(Bson.class))).thenReturn(findIterable);
        when(findIterable.first()).thenReturn(null);

        Pattern pattern = new Pattern.PatternBuilder().setNamespace("finos").build();

        assertThrows(PatternNotFoundException.class,
                () -> mongoPatternStore.getPatternVersions(pattern));

        verify(patternCollection).find(new Document("namespace", pattern.getNamespace()));
        verify(findIterable).projection(Projections.fields(Projections.include("patterns")));
    }

    @Test
    void get_pattern_versions_for_valid_pattern_returns_list_of_versions() throws PatternNotFoundException, NamespaceNotFoundException {
        FindIterable<Document> findIterable = Mockito.mock(FindIterable.class);
        when(namespaceStore.namespaceExists(anyString())).thenReturn(true);
        when(patternCollection.find(any(Bson.class)))
                .thenReturn(findIterable);
        when(findIterable.projection(any(Bson.class))).thenReturn(findIterable);

        //Set up a patterns document with 2 patterns in (one with a valid version)
        Map<String, Document> versionMap = new HashMap<>();
        versionMap.put("1-0-0", Document.parse(validJson));
        Document targetStoredPattern = new Document("patternId", 42)
                .append("versions", new Document(versionMap));

        Document paddingPattern = new Document("patternId", 0);

        Document mainDocument = new Document("namespace", "finos")
                .append("patterns", Arrays.asList(paddingPattern, targetStoredPattern));
        when(findIterable.first()).thenReturn(mainDocument);

        Pattern pattern = new Pattern.PatternBuilder().setNamespace("finos").setId(42).build();
        List<String> patternVersions = mongoPatternStore.getPatternVersions(pattern);

        assertThat(patternVersions, is(Arrays.asList("1.0.0")));
    }

    //getPatternForVersion - namespace doesn't exist
    //getPatternForVersion - pattern doesn't exist
    //getPatternForVersion - version doesn't exist
    //getPatternForVersion - return the pattern

    //createPatternForVersion - version exists

    //create(Update)PatternForVersion - namespace doesn't exist
    //create(Update)PatternForVersion - pattern doesn't exist
    //create(Update)PatternForVersion - pattern is updated
}
