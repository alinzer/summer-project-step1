package edu.yu.cs.artAPI.repositories;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import edu.yu.cs.artAPI.Art;

import io.quarkus.hibernate.orm.panache.Panache;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class ArtRepository implements PanacheRepository<Art> {
    
    public List<Art> search(long galleryId, String name, String creator) {
        return list(
                "gallery_id like ?1 and name like ?2 and creator like ?3",
                initialized(galleryId),
                initialized(name),
                initialized(creator));
    }
    
    //Checks if the argument was initialized to a value. 
    //If not (object is null, int/long is 0, and bool is false), returns the % sign which is a wildcard SQL character.
    //If yes, returns that object (as a string per force due to the % needing to be returned as a string).
    private String initialized(Object o) {
        return (o == null || o.equals(0) || o.equals(false) ? "%" : o.toString());
    }
}