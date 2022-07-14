package edu.yu.cs.hub;

import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;
import javax.inject.Inject;
import javax.transaction.Transactional;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import java.net.URL;
import java.util.*;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;

import reactor.core.publisher.Mono;

@Path("/hub")
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
public class Hub {
    
    @Inject
    GalleryInfoRepository galleryInfoRepo;

    //Gallery talking to hub (during user POST to create a gallery)
    //Hub talks back to gallery and returns the updated list of the IPs 
    @POST
    @Transactional
    public long create(GalleryInfo gi, @Context UriInfo uriInfo) throws JsonProcessingException  {
        galleryInfoRepo.persist(gi);

        this.sendURLMap ();

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Long.toString(gi.id));
        
        return gi.id;
    }
    
    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Response update(@PathParam("id") Long id, URL url) {
        System.out.println(url);
        GalleryInfo gi = galleryInfoRepo.findById(id);
        if (gi == null) {
            throw new NotFoundException();
        }
        gi.url = url;
        System.out.println(galleryInfoRepo.toMap().toString());
        System.out.println(galleryInfoRepo.listAll());   
        
        this.sendURLMap ();
        return Response.status(Status.OK).build();
    }
    
    @DELETE
    @Path("{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        URL url = galleryInfoRepo.findById(id).url;
        boolean deleted = galleryInfoRepo.deleteById(id);
        if (deleted) {
            sendURLMap(Arrays.asList(url));
        }
        return deleted ? Response.noContent().build() : Response.status(BAD_REQUEST).build();
    }

    private void sendURLMap() {
        Map<Long, URL> allGI = galleryInfoRepo.toMap();
        for (long currentId : allGI.keySet()) {
            // Code to update each IP with the new data
            postToGallery(galleryInfoRepo.toMap().get(currentId));
        }
    }
    
    private void sendURLMap(List<URL> extraURLs) {
        sendURLMap();
        for (URL url : extraURLs) {
            postToGallery(url);
        }
    }

    private void postToGallery(URL url) {
        WebClient webClient = WebClient.create(url.toString());
        webClient.post()
                .uri("/galleries/servers")
                .body(Mono.just(galleryInfoRepo.toMap()), Map.class)
                .retrieve()
                .bodyToMono(String.class).block();
    }
    
}
