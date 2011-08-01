package com.nitrous.gwtearth.visitors.client;

import java.util.HashSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.nitrous.gwtearth.visitors.shared.CityMetric;
import com.nitrous.gwtearth.visitors.shared.RpcSvcException;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("rpc")
public interface VisitorService extends RemoteService {
    
    /**
     * Retrieve information about visitor countries
     * @return The visitor city information. 
     * @throws RpcSvcException
     */
    public HashSet<CityMetric> fetchVisitorInformation() throws RpcSvcException;
    
}
