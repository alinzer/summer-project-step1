package edu.yu.cs.gallery;

import java.net.URL;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class Gallery extends PanacheEntityBase {
    @Id
    public long id;
    public String name;
    
    public URL url;

    @OneToMany(mappedBy = "gallery")
    public List<Art> artList;
    
}