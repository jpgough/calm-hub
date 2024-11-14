package org.finos.calm.store.mongo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import jakarta.enterprise.context.ApplicationScoped;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.finos.calm.domain.Pattern;
import org.finos.calm.domain.PatternVersionExistsException;
import org.finos.calm.store.PatternStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class MongoPatternStore implements PatternStore {
    private final MongoCollection<Document> patternCollection;

    public MongoPatternStore(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("calmSchemas");
        this.patternCollection = database.getCollection("patterns");
    }

    @Override
    public List<Integer> getPatternsForNamespace(String namespace) {
        //FIXME Namespace Not Found Exception
        Document groupDocument = patternCollection.find(Filters.eq("namespace", namespace)).first();
        List<Document> patterns = groupDocument.getList("patterns", Document.class);
        List<Integer> patternIds = new ArrayList<>();

        for (Document pattern : patterns) {
            patternIds.add(pattern.getInteger("patternId"));
        }

        return patternIds;
    }

    @Override
    public Pattern createPatternForNamespace(Pattern pattern) {
        //FIXME Counters/Identifier for patterns
        //FIXME Namespace Not Found Exception
        Document patternDocument = new Document("patternId", 1234).append("versions", new Document("1.0.0", Document.parse(pattern.getPatternJson())));
        patternCollection.updateOne(
                Filters.eq("namespace", pattern.getNamespace()),
                Updates.push("patterns", patternDocument),
                new UpdateOptions().upsert(true));

        Pattern persistedPattern = new Pattern.PatternBuilder()
                .setId(1234)
                .setVersion("1.0.0")
                .build();

        return persistedPattern;
    }

    @Override
    public List<String> getPatternVersions(Pattern pattern) {
        Bson filter = new Document("namespace", pattern.getNamespace());
        Bson projection = Projections.fields(Projections.include("patterns"));

        Document result = patternCollection.find(filter).projection(projection).first();

        if (result == null) {
            return new ArrayList<>();
        }

        List<Document> patterns = (List<Document>) result.get("patterns");
        for (Document patternDoc : patterns) {
            if (pattern.getId() == patternDoc.getInteger("patternId")) {
                // Extract the versions map from the matching pattern
                Document versions = (Document) patternDoc.get("versions");
                Set<String> versionKeys = versions.keySet();

                //Convert from Mongo representation
                List<String> resourceVersions = new ArrayList<>();
                for (String versionKey : versionKeys) {
                    resourceVersions.add(versionKey.replace('-', '.'));
                }
                return resourceVersions;  // Return the list of version keys
            }
        }

        return new ArrayList<>();
    }

    @Override
    public String getPatternForVersion(Pattern pattern) {
        //FIXME Refactor into single method
        Bson filter = new Document("namespace", pattern.getNamespace());
        Bson projection = Projections.fields(Projections.include("patterns"));

        Document result = patternCollection.find(filter).projection(projection).first();

        if (result == null) {
            return null;
        }

        List<Document> patterns = (List<Document>) result.get("patterns");
        for (Document patternDoc : patterns) {
            if (pattern.getId() == patternDoc.getInteger("patternId")) {
                // Retrieve the versions map from the matching pattern
                Document versions = (Document) patternDoc.get("versions");

                // Return the pattern JSON blob for the specified version
                Document versionDoc = (Document) versions.get(pattern.getMongoVersion());
                return versionDoc != null ? versionDoc.toJson() : null;
            }
        }
        return null;
    }

    @Override
    public Pattern createPatternForVersion(Pattern pattern) throws PatternVersionExistsException {
        if(versionExists(pattern)) {
            throw new PatternVersionExistsException();
        }

        writePatternToMongo(pattern);

        return pattern;
    }

    private void writePatternToMongo(Pattern pattern) {
        Document patternDocument = Document.parse(pattern.getPatternJson());
        Document filter = new Document("namespace", pattern.getNamespace())
                .append("patterns.patternId", pattern.getId());
        Document update = new Document("$set",
                new Document("patterns.$.versions." + pattern.getMongoVersion(), patternDocument)
        );

        patternCollection.updateOne(filter, update, new UpdateOptions().upsert(true));
    }

    @Override
    public Pattern updatePatternForVersion(Pattern pattern) {
        writePatternToMongo(pattern);
        return pattern;
    }

    private boolean versionExists(Pattern pattern) {
        Document filter = new Document("namespace", pattern.getNamespace()).append("patterns.patternId", pattern.getId());
        Bson projection = Projections.fields(Projections.include("patterns.versions." + pattern.getMongoVersion()));
        Document result = patternCollection.find(filter).projection(projection).first();

        if (result != null) {
            List<Document> patterns = (List<Document>) result.get("patterns");
            for (Document patternDoc : patterns) {
                Document versions = (Document) patternDoc.get("versions");
                if (versions != null && versions.containsKey(pattern.getMongoVersion())) {
                    return true;  // The version already exists
                }
            }
        }
        return false;
    }
}
