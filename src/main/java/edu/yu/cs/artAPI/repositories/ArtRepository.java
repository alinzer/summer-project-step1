package edu.yu.cs.artAPI.repositories;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import edu.yu.cs.artAPI.Art;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ArtRepository implements PanacheRepository<Art> {
    public Art findByName(String name) {
        return find("name", name).firstResult();
    }

    public List<Art> findByCreator(String creator){
       return find("creator", creator).list();
    }   
    
    public List<Art> findByGallery(String gallery) {
        return find("gallery", gallery).list();
    }
   
}