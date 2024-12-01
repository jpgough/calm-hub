package org.finos.calm.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.finos.calm.domain.*;
import org.finos.calm.domain.exception.NamespaceNotFoundException;
import org.finos.calm.domain.exception.FlowNotFoundException;
import org.finos.calm.domain.exception.FlowVersionExistsException;
import org.finos.calm.domain.exception.FlowVersionNotFoundException;
import org.finos.calm.store.FlowStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;

@Path("/calm/namespaces")
public class FlowResource {

    private final FlowStore store;

    private final Logger logger = LoggerFactory.getLogger(FlowResource.class);

    @ConfigProperty(name = "allow.put.operations", defaultValue = "false")
    Boolean allowPutOperations;

    public FlowResource(FlowStore store) {
        this.store = store;
    }

    @GET
    @Path("{namespace}/flows")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Retrieve flows in a given namespace",
            description = "Flows stored in a given namespace"
    )
    public Response getFlowsForNamespace(@PathParam("namespace") String namespace) {
        try {
            return Response.ok(new ValueWrapper<>(store.getFlowsForNamespace(namespace))).build();
        } catch (NamespaceNotFoundException e) {
            logger.error("Invalid namespace [{}] when retrieving flows", namespace, e);
            return invalidNamespaceResponse(namespace);
        }
    }

    @POST
    @Path("{namespace}/flows")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Create flow for namespace",
            description = "Creates a flow for a given namespace with an allocated ID and version 1.0.0"
    )
    public Response createFlowForNamespace(@PathParam("namespace") String namespace, String flowJson) throws URISyntaxException {
        Flow flow = new Flow.FlowBuilder()
                .setNamespace(namespace)
                .setFlow(flowJson)
                .build();

        try {
            return flowWithLocationResponse(store.createFlowForNamespace(flow));
        } catch (NamespaceNotFoundException e) {
            logger.error("Invalid namespace [{}] when creating flow", namespace, e);
            return invalidNamespaceResponse(namespace);
        }
    }

    @GET
    @Path("{namespace}/flows/{flowId}/versions")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Retrieve a list of versions for a given flow",
            description = "Flow versions are not opinionated, outside of the first version created"
    )
    public Response getFlowVersions(@PathParam("namespace") String namespace, @PathParam("flowId") int flowId) {
        Flow flow = new Flow.FlowBuilder()
                .setNamespace(namespace)
                .setId(flowId)
                .build();

        try {
            return Response.ok(new ValueWrapper<>(store.getFlowVersions(flow))).build();
        } catch (NamespaceNotFoundException e) {
            logger.error("Invalid namespace [{}] when getting versions of flow", flow, e);
            return invalidNamespaceResponse(namespace);
        } catch (FlowNotFoundException e) {
            logger.error("Invalid flow [{}] when getting versions of flow", flow, e);
            return invalidFlowResponse(flowId);
        }
    }

    @GET
    @Path("{namespace}/flows/{flowId}/versions/{version}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Retrieve a specific flow at a given version",
            description = "Retrieve flows at a specific version"
    )
    public Response getFlow(@PathParam("namespace") String namespace, @PathParam("flowId") int flowId, @PathParam("version") String version) {
        Flow flow = new Flow.FlowBuilder()
                .setNamespace(namespace)
                .setId(flowId)
                .setVersion(version)
                .build();

        try {
            return Response.ok(store.getFlowForVersion(flow)).build();
        } catch (NamespaceNotFoundException e) {
            logger.error("Invalid namespace [{}] when getting a flow", flow, e);
            return invalidNamespaceResponse(namespace);
        } catch (FlowNotFoundException e) {
            logger.error("Invalid flow [{}] when getting a flow", flow, e);
            return invalidFlowResponse(flowId);
        } catch (FlowVersionNotFoundException e) {
            logger.error("Invalid version [{}] when getting a flow", flow, e);
            return invalidVersionResponse(version);
        }
    }

    @POST
    @Path("{namespace}/flows/{flowId}/versions/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createVersionedFlow(@PathParam("namespace") String namespace, @PathParam("flowId") int flowId, @PathParam("version") String version, String flowJson) throws URISyntaxException {
        Flow flow = new Flow.FlowBuilder()
                .setNamespace(namespace)
                .setId(flowId)
                .setVersion(version)
                .setFlow(flowJson)
                .build();

        try {
            store.createFlowForVersion(flow);
            return flowWithLocationResponse(flow);
        } catch (FlowVersionExistsException e) {
            logger.error("Flow version already exists [{}] when trying to create new flow", flow, e);
            return Response.status(Response.Status.CONFLICT).entity("Version already exists: " + version).build();
        } catch (FlowNotFoundException e) {
            logger.error("Invalid flow [{}] when getting a flow", flow, e);
            return invalidFlowResponse(flowId);
        } catch (NamespaceNotFoundException e) {
            logger.error("Invalid namespace [{}] when getting a flow", flow, e);
            return invalidNamespaceResponse(namespace);
        }
    }

    @PUT
    @Path("{namespace}/flows/{flowId}/versions/{version}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
            summary = "Updates a Flow (if available)",
            description = "In mutable version stores flow updates are supported by this endpoint, operation unavailable returned in repositories without configuration specified"
    )
    public Response updateVersionedFlow(@PathParam("namespace") String namespace, @PathParam("flowId") int flowId, @PathParam("version") String version, String flowJson) throws URISyntaxException {
        Flow flow = new Flow.FlowBuilder()
                .setNamespace(namespace)
                .setId(flowId)
                .setVersion(version)
                .setFlow(flowJson)
                .build();

        if (!allowPutOperations) {
            return Response.status(Response.Status.FORBIDDEN).entity("This Calm Hub does not support PUT operations").build();
        }

        try {
            store.updateFlowForVersion(flow);
            return flowWithLocationResponse(flow);
        } catch (NamespaceNotFoundException e) {
            logger.error("Invalid namespace [{}] when trying to put flow", flow, e);
            return invalidNamespaceResponse(namespace);
        } catch (FlowNotFoundException e) {
            logger.error("Invalid flow [{}] when trying to put flow", flow, e);
            return invalidFlowResponse(flowId);
        }
    }

    private Response flowWithLocationResponse(Flow flow) throws URISyntaxException {
        return Response.created(new URI("/calm/namespaces/" + flow.getNamespace() + "/flows/" + flow.getId() + "/versions/" + flow.getDotVersion())).build();
    }

    private Response invalidNamespaceResponse(String namespace) {
        return Response.status(Response.Status.NOT_FOUND).entity("Invalid namespace provided: " + namespace).build();
    }

    private Response invalidFlowResponse(int flowId) {
        return Response.status(Response.Status.NOT_FOUND).entity("Invalid flow provided: " + flowId).build();
    }

    private Response invalidVersionResponse(String version) {
        return Response.status(Response.Status.NOT_FOUND).entity("Invalid version provided: " + version).build();
    }
}
