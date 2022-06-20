package edu.yu.cs.artAPI;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class Art extends PanacheEntity {
    public String title;
    public String creator;

    @ManyToOne
    public Gallery gallery;
}