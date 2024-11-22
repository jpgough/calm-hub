package org.finos.calm.store.mongo;

import jakarta.enterprise.context.ApplicationScoped;
import org.finos.calm.domain.*;
import org.finos.calm.store.ArchitectureStore;

import java.util.List;

@ApplicationScoped
public class MongoArchitectureStore implements ArchitectureStore {


    @Override
    public List<Integer> getArchitecturesForNamespace(String namespace) throws NamespaceNotFoundException {
        return List.of();
    }

    @Override
    public Architecture createArchitectureForNamespace(Architecture architecture) throws NamespaceNotFoundException {
        return null;
    }

    @Override
    public List<String> getArchitectureVersions(Architecture architecture) throws NamespaceNotFoundException, ArchitectureNotFoundException {
        return List.of();
    }

    @Override
    public String getArchitectureForVersion(Architecture any) throws NamespaceNotFoundException, ArchitectureNotFoundException, ArchitectureVersionNotFoundException {
        return "";
    }

    @Override
    public Architecture createArchitectureForVersion(Architecture architecture) throws NamespaceNotFoundException, ArchitectureNotFoundException, ArchitectureVersionExistsException {
        return null;
    }

    @Override
    public Architecture updateArchitectureForVersion(Architecture architecture) throws NamespaceNotFoundException, ArchitectureNotFoundException {
        return null;
    }
}
