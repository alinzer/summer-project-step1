package edu.yu.cs.artAPI.repository;

import edu.yu.cs.artAPI.Gallery;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class GalleryRepository {
    private EntityManager entityManager;
    public GalleryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    public Optional<Gallery> findById(Integer id) {
        Gallery gallery = entityManager.find(Gallery.class, id);
        return gallery != null ? Optional.of(gallery) : Optional.empty();
    }
    public List<Gallery> findAll() {
        return entityManager.createQuery("from Gallery").getResultList();
    }
    public Optional<Gallery> findByName(String name) {
        Gallery gallery = entityManager.createNamedQuery("Gallery.findByName", Gallery.class)
                .setParameter("name", name)
                .getSingleResult();
        return gallery != null ? Optional.of(gallery) : Optional.empty();
    }
    public Optional<Gallery> save(Gallery gallery) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(gallery);
            entityManager.getTransaction().commit();
            return Optional.of(gallery);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
