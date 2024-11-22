package org.finos.calm.store;

import org.finos.calm.domain.*;

import java.util.List;

public interface ArchitectureStore {
    List<Integer> getArchitecturesForNamespace(String namespace) throws NamespaceNotFoundException;
    Architecture createArchitectureForNamespace(Architecture architecture) throws NamespaceNotFoundException;
    List<String> getArchitectureVersions(Architecture architecture) throws NamespaceNotFoundException, ArchitectureNotFoundException;
    String getArchitectureForVersion(Architecture any) throws NamespaceNotFoundException, ArchitectureNotFoundException, ArchitectureVersionNotFoundException;
    Architecture createArchitectureForVersion(Architecture architecture) throws NamespaceNotFoundException, ArchitectureNotFoundException, ArchitectureVersionExistsException;
    Architecture updateArchitectureForVersion(Architecture architecture) throws NamespaceNotFoundException, ArchitectureNotFoundException;
}
