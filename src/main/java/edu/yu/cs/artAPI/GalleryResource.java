package edu.yu.cs.artAPI;

import java.util.List;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Response;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import edu.yu.cs.artAPI.repositories.ArtRepository;
import edu.yu.cs.artAPI.repositories.GalleryRepository;

@Path("/galleries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GalleryResource {

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
        return gr.findById(id);
    }

    @GET
    @Path("name/{name}")
    public Gallery getByName(@PathParam("name") String name) {
        return gr.findByName(name);
    }

    @POST
    @Transactional
    public Response create(Gallery gallery) {
        gr.persist(gallery);
        if (gr.isPersistent(gallery)) {
            return Response.status(Status.CREATED).entity(gallery).build();
        }
        return Response.status(NOT_FOUND).build();
    }

    // @POST
    // @Transactional
    // public String create(Gallery gallery) {
    //     gr.persist(gallery);
    //     return (gr.findById(gallery.id).toString() + gr.findById((long) 96).toString());
    // }    

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@PathParam("id") Long id, Gallery Gallery) {
        Gallery entity = gr.findById(id);
        if (entity == null) {
            return Response.status(NOT_FOUND).build();
        }
        entity.name = Gallery.name;
        return Response.status(Status.OK).entity(entity).build();
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Response deleteById(@PathParam("id") Long id) {
        // Response response = Response.status(Status.CREATED).entity(Gallery).build();
        boolean deleted = gr.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(BAD_REQUEST).build();
    }
}