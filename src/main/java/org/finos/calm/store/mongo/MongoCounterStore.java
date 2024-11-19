package org.finos.calm.store.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.ReturnDocument;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;

@ApplicationScoped
public class MongoCounterStore {

    private final MongoCollection<Document> counterCollection;
    private final String PATTERN_COUNTER = "patternStoreCounter";

    public MongoCounterStore(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("calmSchemas");
        this.counterCollection = database.getCollection("counters");
    }

    public int getNextSequenceValue() {
        Document filter = new Document("_id", PATTERN_COUNTER);
        Document update = new Document("$inc", new Document("sequence_value", 1));
        FindOneAndUpdateOptions options = new FindOneAndUpdateOptions().returnDocument(ReturnDocument.AFTER).upsert(true);

        Document result = counterCollection.findOneAndUpdate(filter, update, options);

        return result.getInteger("sequence_value");
    }

}
