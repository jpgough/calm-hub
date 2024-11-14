package org.finos.calm.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.finos.calm.domain.ValueWrapper;
import org.finos.calm.store.CoreSchemaStore;

import java.util.ArrayList;
import java.util.Map;

@Path("/calm/schemas")
public class CoreSchemaResource {

    private final CoreSchemaStore coreSchemaStore;

    public CoreSchemaResource(CoreSchemaStore coreSchemaStore) {
        this.coreSchemaStore = coreSchemaStore;
    }

    @GET
    public ValueWrapper<String> schemaVersions() {
        return new ValueWrapper<>(coreSchemaStore.getVersions());
    }

    @GET
    @Path("{version}/meta")
    public Response schemasForVersion(@PathParam("version") String version) {
        Map<String, Object> schemas = coreSchemaStore.getSchemasForVersion(version);
        if (schemas == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Version not found: " + version)
                    .build();
        }
        return Response.ok(new ValueWrapper<>(new ArrayList<>(schemas.keySet()))).build();
    }

    @GET
    @Path("{version}/meta/{schemaName}")
    public Response getSchema(@PathParam("version") String version,
                              @PathParam("schemaName") String schemaName) {
        Map<String, Object> schemas = coreSchemaStore.getSchemasForVersion(version);
        if (schemas == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Version not found: " + version)
                    .build();
        }
        if(!schemas.containsKey(schemaName)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Schema: [" + schemaName + "] not found for version: [" + version + "]").build();
        }

        return Response.ok(schemas.get(schemaName)).build();
    }
}