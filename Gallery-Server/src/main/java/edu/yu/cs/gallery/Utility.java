package edu.yu.cs.gallery;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;


import edu.yu.cs.gallery.repositories.GalleryRepository;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Singleton
public class Utility {
    protected Gallery gallery;
    protected Map<Long, URL> allServers;
    
    @Inject
    GalleryRepository gr;

    public Response redirect (long id, @Context UriInfo uriInfo) throws URISyntaxException {
        if (allServers.keySet().contains(id)) {
            return Response.temporaryRedirect(new URI (allServers.get(id).toString() + uriInfo.getPath())).build();
        }
        return Response.status(NOT_FOUND).build();
    }
}