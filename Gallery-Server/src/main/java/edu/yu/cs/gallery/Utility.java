package edu.yu.cs.gallery;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

    import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import org.springframework.web.reactive.function.client.WebClient;

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
    
    public Response redirect (long[] galleries, @Context UriInfo uriInfo) throws URISyntaxException {
        List<Object> returnEntities = new ArrayList<>();
        for (long lg : galleries) {

            WebClient webClient = WebClient.create(this.allServers.get(lg).toString());
            String path = uriInfo.getPath();
            path = path.replace("batch", Long.toString(lg));
            Object response = webClient.get()
                    .uri(path)
                    .retrieve()
                    .bodyToMono(Object.class).block();
            
            if (response instanceof Collection) {
                returnEntities.addAll((Collection)response);
            } else {
                returnEntities.add(response);
            }
        }
        return Response.status(Status.FOUND).entity(returnEntities).build();
    }
}