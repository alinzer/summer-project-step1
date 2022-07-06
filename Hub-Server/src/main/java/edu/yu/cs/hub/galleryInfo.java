package edu.yu.cs.hub;

import java.net.InetAddress;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.*;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;

@Entity
public class galleryInfo extends PanacheEntityBase {
    @Id
    public long galleryId;

    public InetAddress ia;

    public galleryInfo(long galleryId, InetAddress ia) {
        this.galleryId = galleryId;
        this.ia = ia;
    }

    public galleryInfo() {
    }

    public Map<Long, InetAddress> toMap() {
        Map<Long, InetAddress> giMap = new HashMap<>();
        List<galleryInfo> giList = galleryInfo.listAll();

        for (galleryInfo gi : giList) {
            giMap.put(gi.galleryId, gi.ia);
        }

        return giMap;
    }
}
