package edu.yu.cs.gallery;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import edu.yu.cs.gallery.repositories.GalleryRepository;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;

@Singleton
public class Utility {
    protected Gallery gallery;
    protected Map<Long, URL> allServers;
    protected Long leaderID;
    
    @Inject
    GalleryRepository gr;

    //Forwards the URL to the client with a 307
    protected Response temporaryRedirect(long id, @Context UriInfo uriInfo) throws URISyntaxException {
        if (allServers.keySet().contains(id)) {
            return Response.temporaryRedirect(new URI(allServers.get(id).toString() + uriInfo.getPath())).build();
        }
        return Response.status(NOT_FOUND).build();
    }
    
    //URI must contain the string "batch" in place of the galleryId 
    @SuppressWarnings("all")
    private int redirect(long galleryId, UriInfo uriInfo, HttpMethod httpMethod, Object body, List<Object> returnEntities) throws URISyntaxException {
        WebClient webClient = WebClient.create(this.allServers.get(galleryId).toString());
        
        String path = uriInfo.getPath();
        path = path.replace("batch", Long.toString(galleryId));
        
        ResponseEntity<Object> response = webClient.method(httpMethod)
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toEntity(Object.class)
                .block();

        Object responseObject = response.getBody();
        if (responseObject instanceof Collection) {
            returnEntities.addAll((Collection)responseObject);
        } else {
            returnEntities.add(responseObject);
        }
        return response.getStatusCodeValue();
    }
    
    protected Response readRedirect (long[] galleryIDs, UriInfo uriInfo, Request request) throws URISyntaxException {
        List<Object> returnEntities = new ArrayList<>();
        
        for (long galleryID : galleryIDs) {
            if (allServers.containsKey(galleryID)) {
                int statusCode = this.redirect(galleryID, uriInfo, HttpMethod.valueOf(request.getMethod()), "", returnEntities);
                if (statusCode != 200) {
                    return Response.status(statusCode).entity(returnEntities).build();
                }
            }
        }
        
        return Response.status(Status.OK).entity(returnEntities).build();
    }
    
    protected Response writeRedirect(Gallery[] galleries, UriInfo uriInfo, Request request) throws URISyntaxException {
        //If the user attempts to make a batch write to a server that is not the leader, return a temporaryRedirect to the correct server.
        if (this.gallery.id != leaderID) {
            return temporaryRedirect(leaderID, uriInfo);
        }

        List<Object> returnEntities = new ArrayList<>();
        
        for (Gallery gallery : galleries) {
            if (allServers.containsKey(gallery.id)) {
                int statusCode = this.redirect(gallery.id, uriInfo, HttpMethod.valueOf(request.getMethod()), gallery.artList, returnEntities);
                if (statusCode != 200 && statusCode != 201) {
                    return Response.status(statusCode).entity(returnEntities).build();
                }
            }
        }
        
        return Response.status(Status.OK).entity(returnEntities).build();
    }
    
}