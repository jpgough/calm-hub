package org.finos.calm.domain;

import java.util.Objects;

public class Pattern {
    private final String namespace;
    private final int id;
    private final String version;
    private final String pattern;

    private Pattern(PatternBuilder builder) {
        this.namespace = builder.namespace;
        this.id = builder.id;
        this.version = builder.version;
        this.pattern = builder.pattern;
    }

    public String getNamespace() {
        return namespace;
    }

    public int getId() {
        return id;
    }

    public String getDotVersion() {
        return version;
    }

    public String getMongoVersion() {
        return version.replace('.', '-');
    }

    public String getPatternJson() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pattern pattern1 = (Pattern) o;
        return id == pattern1.id && Objects.equals(namespace, pattern1.namespace) && Objects.equals(version, pattern1.version) && Objects.equals(pattern, pattern1.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, id, version, pattern);
    }

    @Override
    public String toString() {
        return "Pattern{" +
                "namespace='" + namespace + '\'' +
                ", id=" + id +
                ", version='" + version + '\'' +
                ", pattern='" + pattern + '\'' +
                '}';
    }

    public static class PatternBuilder {
        private String namespace;
        private int id;
        private String version;
        private String pattern;

        public PatternBuilder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public PatternBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public PatternBuilder setVersion(String version) {
            this.version = version;
            return this;
        }

        public PatternBuilder setPattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Pattern build() {
            return new Pattern(this);
        }
    }
}
