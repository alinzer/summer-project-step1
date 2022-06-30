package edu.yu.cs.artAPI;

import java.util.List;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import static javax.ws.rs.core.Response.Status.*;

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

    @POST
    @Transactional
    public Response create(Gallery gallery, @Context UriInfo uriInfo) {
        gr.persist(gallery);
        if (!gr.isPersistent(gallery)) {
            throw new NotFoundException();
        }
        // return Response.status(Status.CREATED).entity(gallery).build();
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Long.toString(gallery.id));
        return Response.created(uriBuilder.build()).entity(gallery).status(Status.CREATED).build();
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
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
}