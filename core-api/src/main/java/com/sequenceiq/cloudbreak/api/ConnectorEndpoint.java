package com.sequenceiq.cloudbreak.api;

import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.sequenceiq.cloudbreak.model.JsonEntity;
import com.sequenceiq.cloudbreak.model.PlatformVariantsJson;

@Path("/connectors")
public interface ConnectorEndpoint {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, JsonEntity> getPlatforms();

    @GET
    @Path("variants")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformVariantsJson getPlatformVariants();

  /*  @GET
    @Path(value = "variants/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> getPlatformVariantByType(@PathParam(value = "type") String type);
*/
   /* @GET
    @Path(value = "disktypes")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformDisksJson getDisktypes();
*/
 /*   @GET
    @Path(value = "disktypes/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> getDisktypeByType(@PathParam(value = "type") String type);
*/
   /* @GET
    @Path(value = "connectors/vmtypes")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformVirtualMachinesJson getVmTypes();*/

 /*   @GET
    @Path(value = "vmtypes/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<VmTypeJson> getVmTypeByType(@PathParam(value = "type") String type);
*/
   /* @GET
    @Path(value = "connectors/regions")
    @Produces(MediaType.APPLICATION_JSON)
    PlatformRegionsJson getRegions();*/

 /*   @GET
    @Path(value = "regions/r/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Collection<String> getRegionRByType(@PathParam(value = "type") String type);

    @GET
    @Path(value = "regions/av/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    Map<String, Collection<String>> getRegionAvByType(@PathParam(value = "type") String type);*/
}
