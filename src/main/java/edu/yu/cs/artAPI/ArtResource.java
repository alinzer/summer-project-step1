package edu.yu.cs.artAPI;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response.Status;

import edu.yu.cs.artAPI.repositories.ArtRepository;
import edu.yu.cs.artAPI.repositories.GalleryRepository;

@Path("/galleries")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ArtResource {

    @Inject ArtRepository ar;
    @Inject GalleryRepository gr; 
    

    @GET
    @Path("/{gallery-id}/arts")
    public List<Art> getAll(
            @PathParam("gallery-id") long galleryId,
            @QueryParam("name") String name,
            @QueryParam("creator") String creator) {
        if (name == null & creator == null) {
            // Gets all of the art from a specific gallery
            return gr.findByIdOptional(galleryId).orElseThrow(NotFoundException::new).artList;
        } else if (creator == null) {
            //Optional query param of "name"
            return ar.findByName(galleryId, name);
        } else if (name == null) {
            // Optional query param of "creator"
            return ar.findByName(galleryId, creator);
        } else {
            // Optional query param of "name" and "creator"
            return ar.findByNameAndCreator(galleryId, name, creator);
        }
    }

    @POST
    @Transactional
    @Path("/{gallery-id}/arts")
    //Create a piece of art within the gallery of the ID given by the path parameter
    public Response create(@PathParam("gallery-id") long galleryId, Art art, @Context UriInfo uriInfo) {
        Gallery gallery = gr.findByIdOptional(galleryId).orElseThrow(NotFoundException::new);
        art.gallery = gallery;
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
    public Response update(@PathParam("gallery-id") Long galleryId, @PathParam("id") Long id, Art art) {
        //Checks that the gallery-id refers to a gallery; if not, throws a 404
        gr.findByIdOptional(galleryId).orElseThrow(NotFoundException::new);
        
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
    public Response deleteById(@PathParam("gallery-id") Long galleryId, @PathParam("id") Long id) {
        //Checks that the gallery-id refers to a gallery; if not, throws a 404
        gr.findByIdOptional(galleryId).orElseThrow(NotFoundException::new);
        
        boolean deleted = ar.deleteById(id);
        return deleted ? Response.noContent().build() : Response.status(BAD_REQUEST).build();

    }
}