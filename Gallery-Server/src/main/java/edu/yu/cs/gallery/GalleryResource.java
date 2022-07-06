package edu.yu.cs.gallery;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import edu.yu.cs.gallery.repositories.ArtRepository;
import edu.yu.cs.gallery.repositories.GalleryRepository;
import reactor.core.publisher.Mono;

import java.net.InetAddress;
import java.net.UnknownHostException;

import static javax.ws.rs.core.Response.Status.*;


@Path("/galleries")
@Produces(MediaType.APPLICATION_JSON_VALUE)
@Consumes(MediaType.APPLICATION_JSON_VALUE)
public class GalleryResource {

    public static void main(String[] args) throws UnknownHostException {
        System.out.println(InetAddress.getLocalHost());
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

    String ia;
    Map<Long, InetAddress> allServers;
    
    @Inject
    GalleryRepository gr;
    ArtRepository ar;


    @GET
    public List<Gallery> getAll() {
        return gr.listAll();
    }

    @GET
    @Path("{id}")
    public Gallery getById(@PathParam("id") Long id) {
        return gr.findByIdOptional(id).orElseThrow(NotFoundException::new);
    }

    @GET
    @Path("name/{name}")
    public Gallery getByName(@PathParam("name") String name) {
        Gallery gallery = gr.findByName(name);
        if (gallery == null) {
            throw new NotFoundException();
        }
        return gallery;
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public Response update(@PathParam("id") Long id, Gallery gallery) {
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
    public Response deleteById(@PathParam("id") Long id) {
        boolean deleted = gr.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(BAD_REQUEST).build();
    }

    //User talks to gallery
    @POST
    @Transactional
    public Response create(Gallery gallery, @Context UriInfo uriInfo) throws UnknownHostException {
        ia = InetAddress.getLocalHost().getHostAddress();
        gr.persist(gallery);
        if (!gr.isPersistent(gallery)) {
            throw new NotFoundException();
        }
        // return Response.status(Status.CREATED).entity(gallery).build();
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Long.toString(gallery.id));
        
        this.registerWithHub(gallery.id);
        
        return Response.created(uriBuilder.build()).entity(gallery).status(Status.CREATED).build();
    }
    
    //Gallery talks to hub
    @GET
    @Path("/test")
    public String registerWithHub(long id) throws UnknownHostException {
        
        WebClient webClient = WebClient.create("http://localhost:8088");

        String response = webClient.post()
                .uri("/hub")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"galleryId\":\"" + id + "\",\"ia\":\"" + this.ia + "\"}")
                .retrieve()           
                .bodyToMono(String.class).block();

        System.out.println(response);
        return response;
    }


    //Hub talks to the gallery
    @POST 
    @Path("/servers")
    public Response updateIPs(Map<Long, InetAddress> allServers) {
        this.allServers = allServers;
        if (this.allServers.equals(allServers)) {
            return Response.status(Status.OK).build();
        }
        return Response.status(INTERNAL_SERVER_ERROR).build();
    }


    
    /*
     * Upon creation of a gallery with a POST:
     * User -> THIS Gallery-Server (GalleryResource): Creating a gallery
     * THIS Gallery-Server (GalleryResource) -> Hub-Server (Hub): Posting it's IP address/"registration"
     * Hub-Server (Hub) -> ALL Gallery-Server (GalleryResource): Return the list of the IP addresses
     * 
     * Upon user requesting gallery info in a GET:
     * THIS Gallery-Server (GalleryResource): Check if this gallery can process the request (is there a global way to check all incoming request to a given resource?)
     */
}