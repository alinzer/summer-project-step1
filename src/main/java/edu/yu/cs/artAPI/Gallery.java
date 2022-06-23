package edu.yu.cs.artAPI;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Gallery extends PanacheEntity {
    public String name;

    @OneToMany (mappedBy = "gallery")
    public List<Art> artList = new ArrayList<>();
    

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Art> getArtList() {
        return this.artList;
    }

    public void setArtList(List<Art> artList) {
        this.artList = artList;
    }
    
}