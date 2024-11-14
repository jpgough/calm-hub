package org.finos.calm.store;

import org.finos.calm.domain.*;

import java.util.List;

public interface PatternStore {
    List<Integer> getPatternsForNamespace(String namespace) throws NamespaceNotFoundException;
    Pattern createPatternForNamespace(Pattern pattern) throws NamespaceNotFoundException;
    List<String> getPatternVersions(Pattern pattern) throws NamespaceNotFoundException, PatternNotFoundException;
    String getPatternForVersion(Pattern pattern) throws NamespaceNotFoundException, PatternNotFoundException, PatternVersionNotFoundException;
    Pattern createPatternForVersion(Pattern pattern) throws NamespaceNotFoundException, PatternNotFoundException, PatternVersionExistsException;
    Pattern updatePatternForVersion(Pattern pattern) throws NamespaceNotFoundException, PatternNotFoundException;
}
