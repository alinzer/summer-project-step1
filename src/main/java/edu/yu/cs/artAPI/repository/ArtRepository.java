package edu.yu.cs.artAPI.repository;

import edu.yu.cs.artAPI.Art;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class ArtRepository {
    private EntityManager entityManager;
    public ArtRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Optional<Art> findById(Integer id) {
        Art art = entityManager.find(Art.class, id);
        return art != null ? Optional.of(art) : Optional.empty();
    }
    
    public List<Art> findAll() {
        return entityManager.createQuery("from Art").getResultList();
    }
    
    public Optional<Art> findByName(String name) {
        Art art = entityManager.createQuery("SELECT a FROM Art a WHERE a.name = :name", Art.class)
                .setParameter("name", name)
                .getSingleResult();
        return art != null ? Optional.of(art) : Optional.empty();
    }
    
    public Optional<Art> findByNameNamedQuery(String name) {
        Art art = entityManager.createNamedQuery("Art.findByName", Art.class)
                .setParameter("name", name)
                .getSingleResult();
        return art != null ? Optional.of(art) : Optional.empty();
    }
    
    public Optional<Art> save(Art art) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(art);
            entityManager.getTransaction().commit();
            return Optional.of(art);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
