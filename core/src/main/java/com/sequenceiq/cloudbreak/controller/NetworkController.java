package com.sequenceiq.cloudbreak.controller;

import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import com.sequenceiq.cloudbreak.api.NetworkEndpoint;
import com.sequenceiq.cloudbreak.doc.ContentType;
import com.sequenceiq.cloudbreak.doc.ControllerDescription;
import com.sequenceiq.cloudbreak.doc.Notes;
import com.sequenceiq.cloudbreak.doc.OperationDescriptions;
import com.sequenceiq.cloudbreak.domain.CbUser;
import com.sequenceiq.cloudbreak.domain.Network;
import com.sequenceiq.cloudbreak.model.IdJson;
import com.sequenceiq.cloudbreak.model.NetworkJson;
import com.sequenceiq.cloudbreak.service.network.DefaultNetworkCreator;
import com.sequenceiq.cloudbreak.service.network.NetworkService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

@Component
@Api(value = "/networks", description = ControllerDescription.NETWORK_DESCRIPTION, position = 8)
public class NetworkController implements NetworkEndpoint {

    @Inject
    @Qualifier("conversionService")
    private ConversionService conversionService;

    @Inject
    private NetworkService networkService;

    @Inject
    private DefaultNetworkCreator networkCreator;

    @Inject
    private AuthenticatedUserService authenticatedUserService;

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.POST_PRIVATE, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public IdJson postPrivate(NetworkJson networkJson) {
        CbUser user = authenticatedUserService.getCbUser();
        return createNetwork(user, networkJson, false);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.POST_PUBLIC, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public IdJson postPublic(NetworkJson networkJson) {
        CbUser user = authenticatedUserService.getCbUser();
        return createNetwork(user, networkJson, true);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.GET_PRIVATE, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public Set<NetworkJson> getPrivates() {
        CbUser user = authenticatedUserService.getCbUser();
        Set<Network> networks = networkCreator.createDefaultNetworks(user);
        networks.addAll(networkService.retrievePrivateNetworks(user));
        return convert(networks);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.GET_PUBLIC, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public Set<NetworkJson> getPublics() {
        CbUser user = authenticatedUserService.getCbUser();
        Set<Network> networks = networkCreator.createDefaultNetworks(user);
        networks.addAll(networkService.retrieveAccountNetworks(user));
        return convert(networks);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.GET_BY_ID, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public NetworkJson get(Long id) {
        Network network = networkService.getById(id);
        return convert(network);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.GET_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public NetworkJson getPrivate(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        Network network = networkService.getPrivateNetwork(name, user);
        return convert(network);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.GET_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public NetworkJson getPublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        Network network = networkService.getPublicNetwork(name, user);
        return convert(network);
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.DELETE_BY_ID, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public NetworkJson delete(Long id) {
        CbUser user = authenticatedUserService.getCbUser();
        networkService.delete(id, user);
        return new NetworkJson();
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.DELETE_PUBLIC_BY_NAME, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public NetworkJson deletePublic(String name) {
        CbUser user = authenticatedUserService.getCbUser();
        networkService.delete(name, user);
        return new NetworkJson();
    }

    @Override
    @ApiOperation(value = OperationDescriptions.NetworkOpDescription.DELETE_PRIVATE_BY_NAME, produces = ContentType.JSON, notes = Notes.NETWORK_NOTES)
    public NetworkJson deletePrivate(@PathVariable String name) {
        CbUser user = authenticatedUserService.getCbUser();
        networkService.delete(name, user);
        return new NetworkJson();
    }

    private IdJson createNetwork(CbUser user, NetworkJson networkRequest, boolean publicInAccount) {
        Network network = convert(networkRequest, publicInAccount);
        network = networkService.create(user, network);
        return new IdJson(network.getId());
    }

    private Network convert(NetworkJson networkRequest, boolean publicInAccount) {
        Network network = conversionService.convert(networkRequest, Network.class);
        network.setPublicInAccount(publicInAccount);
        return network;
    }

    private NetworkJson convert(Network network) {
        return conversionService.convert(network, NetworkJson.class);
    }

    private Set<NetworkJson> convert(Set<Network> networks) {
        Set<NetworkJson> jsons = new HashSet<>();
        for (Network network : networks) {
            jsons.add(convert(network));
        }
        return jsons;
    }
}
