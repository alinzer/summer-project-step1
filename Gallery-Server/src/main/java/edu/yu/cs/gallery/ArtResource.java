package edu.yu.cs.gallery;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URISyntaxException;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Response.Status;


import edu.yu.cs.gallery.repositories.ArtRepository;
import edu.yu.cs.gallery.repositories.GalleryRepository;
import io.quarkus.runtime.StartupEvent;

@Path("/galleries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtResource {

    @Inject 
    ArtRepository ar;
    @Inject 
    GalleryRepository gr; 
    @Inject 
    Utility utility;

    public void init(@Observes StartupEvent se) {
        utility.gallery = gr.findAll().firstResult();
    }


    @GET
    @Path("/{gallery-id}/arts")
    public Response getAll(
            @PathParam("gallery-id") long galleryId,
            @QueryParam("name") String name,
            @QueryParam("creator") String creator, @Context UriInfo uriInfo) throws URISyntaxException {
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            utility.redirect(galleryId, uriInfo);
        }
        return Response.status(Status.OK).entity(ar.search(galleryId, name, creator)).build();
    }

    @POST
    @Transactional
    @Path("/{gallery-id}/arts")
    //Create a piece of art within the gallery of the ID given by the path parameter
    public Response create(@PathParam("gallery-id") long galleryId, Art art, @Context UriInfo uriInfo) throws URISyntaxException {
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            utility.redirect(galleryId, uriInfo);
        }
        art.gallery = utility.gallery;
        ar.persist(art);
        if (!ar.isPersistent(art)) {
            throw new NotFoundException();
        }
        UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
        uriBuilder.path(Long.toString(art.id));
        return Response.created(uriBuilder.build()).entity(art).status(Status.CREATED).build();
    }
  
    @PUT
    @Path("/{gallery-id}/arts/{id}")
    @Transactional
    //Replaces the piece of art that exists at a certain id in a certain gallery with a new one
    public Response update(@PathParam("gallery-id") long galleryId, @PathParam("id") Long id, Art art, @Context UriInfo uriInfo) throws URISyntaxException {   
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            utility.redirect(galleryId, uriInfo);
        }    
        Art entity = ar.findById(id);
        if (entity == null) {
            return Response.status(NOT_FOUND).build();
        }
        
        entity.name = art.name;
        entity.creator = art.creator;

        return Response.status(Status.OK).entity(entity).build();
    } 

    @DELETE
    @Path("/{gallery-id}/arts/{id}")
    @Transactional
    public Response deleteById(@PathParam("gallery-id") long galleryId, @PathParam("id") Long id, @Context UriInfo uriInfo) throws URISyntaxException {   
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            utility.redirect(galleryId, uriInfo);
        }     
        return ar.deleteById(id) ? Response.noContent().build() : Response.status(BAD_REQUEST).build();
    }
}