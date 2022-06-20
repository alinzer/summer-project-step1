package edu.yu.cs.artAPI;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Gallery extends PanacheEntity {
    public String name;

    @OneToMany (mappedBy = "gallery")
    public List <Art> artList = new ArrayList<>();
    
}