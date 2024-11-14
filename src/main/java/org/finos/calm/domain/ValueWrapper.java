package org.finos.calm.domain;

import java.util.List;

public class ValueWrapper<T> {
    private List<T> values;

    public ValueWrapper(List<T> values) {
        this.values = values;
    }

    public List<T> getValues() {
        return values;
    }
}
