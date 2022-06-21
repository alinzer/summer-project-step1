package edu.yu.cs.artAPI;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@NamedQueries({
    @NamedQuery(name = "Art.findByName",
            query = "SELECT a FROM Art a WHERE a.title = :title"),
    @NamedQuery(name = "Art.findAll",
            query = "SELECT a FROM Art a")
})

public class Art extends PanacheEntity {
    @Id
    @GeneratedValue
    private Integer id;
    
    private String title;
    private String creator;

    @ManyToOne
    private Gallery gallery;
    
    public Art (String title) {
        this.title = title;
    }
    
    public void setGallery (Gallery gallery) {
        this.gallery = gallery;
    }
    
    public Gallery getGallery () {
        return this.gallery;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    
    @Override
    public String toString() {
        return "Art{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", creator=" + creator + '/' + 
                ", gallery=" + gallery.getName() +
                '}';
    }
}