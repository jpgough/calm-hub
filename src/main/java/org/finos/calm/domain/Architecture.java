package org.finos.calm.domain;

import java.util.Objects;

public class Architecture {
    private final String namespace;
    private final int id;
    private final String version;
    private final String architecture;

    private Architecture(ArchitectureBuilder builder) {
        this.namespace = builder.namespace;
        this.id = builder.id;
        this.version = builder.version;
        this.architecture = builder.architecture;
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

    public String getArchitectureJson() {
        return architecture;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Architecture that = (Architecture) o;
        return id == that.id && Objects.equals(namespace, that.namespace) && Objects.equals(version, that.version) && Objects.equals(architecture, that.architecture);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, id, version, architecture);
    }

    @Override
    public String toString() {
        return "Architecture{" +
                "namespace='" + namespace + '\'' +
                ", id=" + id +
                ", version='" + version + '\'' +
                ", architecture='" + architecture + '\'' +
                '}';
    }

    public static class ArchitectureBuilder {
        private String namespace;
        private int id;
        private String version;
        private String architecture;

        public ArchitectureBuilder setNamespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public ArchitectureBuilder setId(int id) {
            this.id = id;
            return this;
        }

        public ArchitectureBuilder setVersion(String version) {
            this.version = version;
            return this;
        }

        public ArchitectureBuilder setArchitecture(String architecture) {
            this.architecture = architecture;
            return this;
        }

        public Architecture build() {
            return new Architecture(this);
        }
    }
}
