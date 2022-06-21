package edu.yu.cs.artAPI;

import edu.yu.cs.artAPI.Art;
import edu.yu.cs.artAPI.Gallery;
import edu.yu.cs.artAPI.repository.ArtRepository;
import edu.yu.cs.artAPI.repository.GalleryRepository;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;
import java.util.Optional;
public class JpaExample {
    public static void main(String[] args) {
        // Create our entity manager
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("ArtList");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        // Create our repositories
        ArtRepository artRepository = new ArtRepository(entityManager);
        GalleryRepository galleryRepository = new GalleryRepository(entityManager);
        // Create an gallery and add 3 Arts to his list of Arts
        Gallery gallery = new Gallery("Gallery 1");
        gallery.addArt(new Art("Art 1"));
        gallery.addArt(new Art("Art 2"));
        gallery.addArt(new Art("Art 3"));
        Optional<Gallery> savedGallery = galleryRepository.save(gallery);
        System.out.println("Saved gallery: " + savedGallery.get());
        // Find all galleries
        List<Gallery> galleries = galleryRepository.findAll();
        System.out.println("Galleries:");
        galleries.forEach(System.out::println);
        // Find gallery by name
        Optional<Gallery> galleryByName = galleryRepository.findByName("Gallery 1");
        System.out.println("Searching for an gallery by name: ");
        galleryByName.ifPresent(System.out::println);
        // Search for a Art by ID
        Optional<Art> foundArt = artRepository.findById(2);
        foundArt.ifPresent(System.out::println);
        // Search for a Art with an invalid ID
        Optional<Art> notFoundArt = artRepository.findById(99);
        notFoundArt.ifPresent(System.out::println);
        // List all arts
        List<Art> arts = artRepository.findAll();
        System.out.println("Arts in database:");
        arts.forEach(System.out::println);
        // Find a art by name
        Optional<Art> queryArt1 = artRepository.findByName("Art 2");
        System.out.println("Query for art 2:");
        queryArt1.ifPresent(System.out::println);
        // Find a boartok by name using a named query
        Optional<Art> queryArt2 = artRepository.findByNameNamedQuery("Art 3");
        System.out.println("Query for art 3:");
        queryArt2.ifPresent(System.out::println);
        // Add a art to gallery 1
        Optional<Gallery> gallery1 = galleryRepository.findById(1);
        gallery1.ifPresent(a -> {
            a.addArt(new Art("Art 4"));
            System.out.println("Saved gallery: " + galleryRepository.save(a));
        });
        // Close the entity manager and associated factory
        entityManager.close();
        entityManagerFactory.close();
    }
}
