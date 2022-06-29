package edu.yu.cs.artAPI;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@FilterDef (
    name = "search", 
        parameters = {
            @ParamDef(name = "galleryId", type = "long"),
            @ParamDef(name = "name", type = "string"),
            @ParamDef(name = "creator", type = "string")
        },
    defaultCondition = "gallery_id=:galleryId and name=:name and creator=:creator"
)

@Filter (name = "search")
@Entity
public class Art extends PanacheEntity {
    public String name;
    public String creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JoinColumn(name = "gallery_id")
    public Gallery gallery;

}