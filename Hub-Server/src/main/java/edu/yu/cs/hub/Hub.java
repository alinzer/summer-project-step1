package edu.yu.cs.hub;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;
import java.net.InetAddress;
import java.util.*;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Path("/hubs")
@RegisterRestClient
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Hub {
    
    @Entity
    public class galleryInfo extends PanacheEntityBase {
        @Id
        public long galleryId;
        public InetAddress ia;
        
        public galleryInfo(long galleryId, InetAddress ia) {
            this.galleryId = galleryId;
            this.ia = ia;
        }
        
        public Map<Long, InetAddress> toMap() {
            Map<Long, InetAddress> giMap = new HashMap<>();
            List<galleryInfo> giList = galleryInfo.listAll();
             
            for (galleryInfo gi : giList) {
                giMap.put(gi.galleryId, gi.ia);
            }
            
            return giMap;
            // Map<long, InetAddress>  
        }
    }
    
    @POST
    @Transactional
    public Response create(long galleryId, InetAddress ia,  @Context UriInfo uriInfo) {
        galleryInfo gi = new galleryInfo(galleryId, ia);
        gi.persist();

        Map<Long, InetAddress> allGI = gi.toMap();
        for (long currentId : allGI.keySet()) {
            
            //Code to update each IP with the new data 
        }

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Long.toString(gi.galleryId));
        return Response.created(uriBuilder.build()).entity(gi).status(Status.CREATED).build();
    }
}
