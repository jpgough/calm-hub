package org.finos.calm.store.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.finos.calm.store.NamespaceStore;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MongoNamespaceStore implements NamespaceStore {

    private final MongoCollection<Document> namespaceCollection;

    public MongoNamespaceStore(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("calmSchemas");
        this.namespaceCollection = database.getCollection("namespaces");
    }

    @Override
    public List<String> getNamespaces() {
        List<String> versions = new ArrayList<>();
        for (Document doc : namespaceCollection.find()) {
            versions.add(doc.getString("namespace"));
        }
        return versions;
    }
}
