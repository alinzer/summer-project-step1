package edu.yu.cs.artAPI.repositories;

import javax.enterprise.context.ApplicationScoped;

import edu.yu.cs.artAPI.Art;
import edu.yu.cs.artAPI.Gallery;

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