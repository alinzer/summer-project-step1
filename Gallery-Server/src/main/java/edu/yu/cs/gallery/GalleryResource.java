package edu.yu.cs.gallery;

import edu.yu.cs.gallery.repositories.GalleryRepository;
import io.quarkus.runtime.StartupEvent;

import java.util.*;
import java.net.*;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;

import javax.ws.rs.core.Response.Status;
import static javax.ws.rs.core.Response.Status.*;

import org.springframework.http.*;
import org.springframework.web.reactive.function.client.WebClient;

@Path("/galleries")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
public class GalleryResource {
    Gallery gallery;
    static Map<Long, URL> allServers;
    
    @Inject
    GalleryRepository gr;
    @Inject 
    ServerStartUp ssu;

    public void init(@Observes StartupEvent se) {
        this.gallery = gr.findAll().firstResult();
    }

    @GET
    public List<Gallery> getOnServer() {
        return gr.listAll();
        //return gr.find().firstResult();
    }

    @GET
    @Path("{id}")
    public Response getById(@PathParam("id") Long id, @Context UriInfo uriInfo) throws URISyntaxException {
        if (this.gallery == null || id != this.gallery.id) {
            return redirect(id, uriInfo);
        }
        return Response.status(Status.FOUND).entity(gr.findById(id)).build();
    }
    
    @PUT
    @Path("{id}")
    @Transactional
    public Response update(@PathParam("id") Long id, Gallery gallery, @Context UriInfo uriInfo) throws URISyntaxException {
        if (this.gallery == null || id != this.gallery.id) {
            return redirect(id, uriInfo);
        }
        Gallery entity = gr.findById(id);
        if (entity == null) {
            throw new NotFoundException();
        }
        entity.name = gallery.name;
        return Response.status(Status.OK).entity(entity).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteById(@PathParam("id") Long id, @Context UriInfo uriInfo) throws URISyntaxException {
        if (this.gallery == null || id != this.gallery.id) {
            return redirect(id, uriInfo);
        }
        boolean deleted = gr.deleteById(id);
        if (deleted) {
            this.gallery = null;
            WebClient webClient = WebClient.create("https://hubserver.ngrok.io");
    
            webClient.delete()
                    .uri("/hub/" + id)
                    .retrieve()           
                    .bodyToMono(Long.class).block();
        }
        return deleted ? Response.noContent().build() : Response.status(BAD_REQUEST).build();
    }

    /*
     * Upon creation of a gallery with a POST:
     * User -> THIS Gallery-Server (GalleryResource): Creating a gallery
     * THIS Gallery-Server (GalleryResource) -> Hub-Server (Hub): Posting it's
     * url/"registration"
     * Hub-Server (Hub) -> ALL Gallery-Server (GalleryResource): Return the list of
     * the IP addresses
     * 
     * Upon user requesting gallery info in a GET:
     * THIS Gallery-Server (GalleryResource): Check if this gallery can process the
     * request (is there a global way to check all incoming request to a given
     * resource?)
     */

    //User talks to gallery
    @POST
    @Transactional
    public Response create(Gallery gallery, @Context UriInfo uriInfo) throws UnknownHostException, MalformedURLException {
        if (this.gallery != null) {
            return Response.status(502, "This Gallery-Server already has a gallery assigned").build();
        }
        
        this.gallery = gallery;
        gallery.url = new URL ("https://" + System.getenv("URL") + ".ngrok.io");
        gallery.id = this.registerWithHub(gallery.url);
        
        gr.persist(gallery);
        if (!gr.isPersistent(gallery)) {
            throw new NotFoundException();
        }

        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Long.toString(gallery.id));
        
        return Response.created(uriBuilder.build()).entity(gallery).status(Status.CREATED).build();
    }
    
    //Gallery talks to hub
    private long registerWithHub(URL url) throws UnknownHostException {
        WebClient webClient = WebClient.create("https://hubserver.ngrok.io");
        Long response = webClient.post()
                .uri("/hub")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"url\":\"" + url + "\"}")
                .retrieve()           
                .bodyToMono(Long.class).block();

        return response;
    }

    //Hub talks to the gallery
    @POST 
    @Path("/servers")
    public Response updateIPs(Map<Long, URL> as) {
        System.out.println("in updateIPs");
        allServers = as;
        if (allServers.equals(allServers)) {
            return Response.status(Status.OK).build();
        }
        return Response.status(INTERNAL_SERVER_ERROR).build();
    }
   
    protected static Response redirect (long id, @Context UriInfo uriInfo) throws URISyntaxException {
        if (allServers.keySet().contains(id)) {
            return Response.temporaryRedirect(new URI (allServers.get(id).toString() + uriInfo.getPath())).build();
        }
        return Response.status(NOT_FOUND).build();
    }
    
    // For testing purposes
    @Path("/servers")
    @GET
    public Map<Long, URL> IPs() {
        System.out.println(allServers);
        return allServers;
    }

}