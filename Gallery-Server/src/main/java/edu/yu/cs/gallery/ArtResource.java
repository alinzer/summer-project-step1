package edu.yu.cs.gallery;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.net.URISyntaxException;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;


import edu.yu.cs.gallery.repositories.ArtRepository;
import edu.yu.cs.gallery.repositories.GalleryRepository;

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

// ---------------------------------------Single-Requests--------------------------------------------//    
    @GET
    @Path("/{gallery-id}/arts")
    public Response getAll(
            @PathParam("gallery-id") long galleryId,
            @QueryParam("name") String name,
            @QueryParam("creator") String creator, @Context UriInfo uriInfo) throws URISyntaxException {
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            return utility.temporaryRedirect(galleryId, uriInfo);
        }
        return Response.status(Status.OK).entity(ar.search(galleryId, name, creator)).build();
    }
    
    @POST
    @Transactional
    @Path("/{gallery-id}/arts")
    //POST method 
    //Object obj is a filler to enable either an Art or Art array to be passed in to a single endpoint and handled correctly. 
    //An attempt is made to deserialize as either Art or Art array, if it fails a BAD_REQUEST status is returned 
    public Response creates(@PathParam("gallery-id") long galleryId, Object obj, @Context UriInfo uriInfo) throws URISyntaxException {
        ObjectMapper om = new ObjectMapper();
        try {
            Art art = om.convertValue(obj, Art.class);
            return create(galleryId, art, uriInfo);
        } catch (Exception e1) {
            try {
                Art[] arts = om.convertValue(obj, Art[].class);
                return create(galleryId, arts, uriInfo);
            } catch (Exception e2) {
                return Response.status(Status.BAD_REQUEST).build();
            }
        }
    }
    
            //Create a single piece of art within the gallery of the ID given by the path parameter
            private Response create(long galleryId, Art art, UriInfo uriInfo) throws URISyntaxException {
                if (utility.gallery == null || galleryId != utility.gallery.id) {
                    return utility.temporaryRedirect(galleryId, uriInfo);
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
            
            //Create multiple pieces of art within the gallery of the ID given by the path parameter
            public Response create(long galleryId, Art[] arts, UriInfo uriInfo) throws URISyntaxException {
                if (utility.gallery == null || galleryId != utility.gallery.id) {
                    return utility.temporaryRedirect(galleryId, uriInfo);
                }
                for (Art art : arts) {
                    art.gallery = utility.gallery;
                }
                ar.persist(Arrays.asList(arts));
                
                return Response.status(Status.CREATED).entity(arts).build();
            }
    
    @PUT
    @Path("/{gallery-id}/arts/{id}")
    @Transactional
    //Replaces the piece of art that exists at a certain id in a certain gallery with a new one
    public Response update(@PathParam("gallery-id") long galleryId, @PathParam("id") Long id, Art art, @Context UriInfo uriInfo) throws URISyntaxException {
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            return utility.temporaryRedirect(galleryId, uriInfo);
        }
        Art currentArt = ar.findById(id);
        if (currentArt == null) {
            return Response.status(NOT_FOUND).build();
        }

        currentArt.name = art.name;
        currentArt.creator = art.creator;

        return Response.status(Status.OK).entity(currentArt).build();
    }
    
    @PUT
    @Transactional
    @Path("/{gallery-id}/arts")
    // Create a piece of art within the gallery of the ID given by the path parameter
    public Response update(@PathParam("gallery-id") long galleryId, List<Art> arts, @Context UriInfo uriInfo) throws URISyntaxException {
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            return utility.temporaryRedirect(galleryId, uriInfo);
        }
    
        List<Art> successfulArts = new ArrayList<>();
        for (Art art : arts) {
            Art currentArt = ar.findById(art.id);
            if (currentArt == null) {
                return Response.status(NOT_FOUND).entity(successfulArts).build();
            }
            currentArt.name = art.name;
            currentArt.creator = art.creator;
            successfulArts.add(art);
        }
        return Response.status(Status.OK).entity(arts).build();
    }   

    @DELETE
    @Path("/{gallery-id}/arts/{id}")
    @Transactional
    public Response deleteById(@PathParam("gallery-id") long galleryId, @PathParam("id") Long id,
            @Context UriInfo uriInfo) throws URISyntaxException {
        if (utility.gallery == null || galleryId != utility.gallery.id) {
            return utility.temporaryRedirect(galleryId, uriInfo);
        }
        return ar.deleteById(id) ? Response.noContent().build() : Response.status(BAD_REQUEST).build();
    }
    
//---------------------------------------Batch-Requests--------------------------------------------//
    @GET
    @Path("/batch/arts")
    public Response getOnServer(@QueryParam("gallery") long[] galleryIDs, @Context UriInfo uriInfo, @Context Request request) throws URISyntaxException {
        return utility.readRedirect(galleryIDs, uriInfo, request);
    }
    
    @POST
    @Path("/batch/arts")
    public Response postOnServer(@Context UriInfo uriInfo, @Context Request request, Gallery[] galleries) throws URISyntaxException {
        return utility.writeRedirect(galleries, uriInfo, request);
    }
    
    @PUT
    @Path("/batch/arts")
    public Response putOnServer(@Context UriInfo uriInfo, @Context Request request, Gallery[] galleries) throws URISyntaxException {
        return utility.writeRedirect(galleries, uriInfo, request);
    }
    
}