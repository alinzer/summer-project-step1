package edu.yu.cs.artAPI.repositories;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import edu.yu.cs.artAPI.Art;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ArtRepository implements PanacheRepository<Art> {
    public List<Art> findAllByGallery(String gallery) {
        return list("gallery", gallery);
    }

    public List<Art> findByCreator(long galleryId, String creator){
       return list("gallery_id = ?1 and creator = ?2", galleryId, creator);
    }   
   
    public List<Art> findByGallery(long galleryId, String name) {
        // return list("gallery", gallery);
        return list("gallery_id = ?1 and name = ?2", galleryId, name);
    }
}