package edu.yu.cs.artAPI;

import java.util.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@NamedQueries({
    @NamedQuery(name = "Gallery.findByName",
            query = "SELECT g FROM Gallery g WHERE g.name = :name")
})

public class Gallery extends PanacheEntity {
    private Integer id;
    
    private String name;
    
    @OneToMany (mappedBy = "gallery", cascade = CascadeType.ALL)
    private List <Art> artList = new ArrayList<>();
    
    public Gallery(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<Art> getArtList() {
        return artList;
    }
    public void addArt(Art art) {
        artList.add(art);
        art.setGallery(this);
    }
    
    @Override
    public String toString() {
        return "Gallery{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", art=" + artList +
                '}';
    }
}