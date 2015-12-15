package com.sequenceiq.cloudbreak.api;

import java.util.Collection;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.JsonEntity;
import com.sequenceiq.cloudbreak.model.PlatformDisksJson;
import com.sequenceiq.cloudbreak.model.PlatformRegionsJson;
import com.sequenceiq.cloudbreak.model.PlatformVariantsJson;
import com.sequenceiq.cloudbreak.model.PlatformVirtualMachinesJson;
import com.sequenceiq.cloudbreak.model.VmTypeJson;

public interface ConnectorEndpoint {

    @GET
    @Path("/connectors")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, JsonEntity> getPlatforms();

    @GET
    @Path("/connectors/variants")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformVariantsJson getPlatformVariants();

    @GET
    @Path(value = "/connectors/variants/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> getPlatformVariantByType(@PathParam(value = "type") String type);

    @GET
    @Path(value = "/connectors/disktypes")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformDisksJson getDisktypes();

    @GET
    @Path(value = "/connectors/disktypes/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> getDisktypeByType(@PathParam(value = "type") String type);

    @GET
    @Path(value = "/connectors/vmtypes")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformVirtualMachinesJson getVmTypes();

    @GET
    @Path(value = "/connectors/vmtypes/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<VmTypeJson> getVmTypeByType(@PathParam(value = "type") String type);

    @GET
    @Path(value = "/connectors/regions")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformRegionsJson getRegions();

    @GET
    @Path(value = "/connectors/regions/r/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> getRegionRByType(@PathParam(value = "type") String type);

    @GET
    @Path(value = "/connectors/regions/av/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Collection<String>> getRegionAvByType(@PathParam(value = "type") String type);
}
