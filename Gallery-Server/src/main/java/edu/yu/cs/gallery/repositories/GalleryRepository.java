package edu.yu.cs.gallery.repositories;

import javax.enterprise.context.ApplicationScoped;

import edu.yu.cs.gallery.Art;
import edu.yu.cs.gallery.Gallery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class GalleryRepository implements PanacheRepository<Gallery> {
    public Gallery findByName(String name) {
        return find("name", name).firstResult();
    }

    public Gallery findByArt(Art art) {
        return find("artList", art).firstResult();
    }
}