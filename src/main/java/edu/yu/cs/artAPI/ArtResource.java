package edu.yu.cs.artAPI;

import java.util.List;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/art")
public class ArtResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Art> allArt() {
        return Art.listAll();
    }

    @Transactional
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response newArt(Art art) {
        art.id = null;
        art.persist();
        return Response.status(Status.CREATED).entity(art).build();
    }

    // @Transactional
    // @PUT
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    // @Path("/{id}")
    // public Response updateArt(@PathParam("id") long id, Art art) {
    //     Art.findById(id);
    //     art.id = null;
    //     art.persist();
    //     return Response.status(Status.CREATED).entity(art).build();
    // }
 

}