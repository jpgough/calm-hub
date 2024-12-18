package org.finos.calm.integration;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.containers.MongoDBContainer;

import java.util.Map;

public class EndToEndResource implements QuarkusTestResourceLifecycleManager {

    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.3");

    @Override
    public Map<String, String> start() {
        mongoDBContainer.start();
        System.out.println("MongoDB TestContainer started at: " + mongoDBContainer.getReplicaSetUrl());
        return Map.of(
                "quarkus.mongodb.connection-string", mongoDBContainer.getReplicaSetUrl()
        );
    }

    @Override
    public void stop() {
        mongoDBContainer.stop();
    }
}

