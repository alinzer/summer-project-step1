package edu.yu.cs.hub;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.*;

@Singleton
public class Utility {
    protected long hubLeaderID;
    
    @Inject 
    GalleryInfoRepository galleryInfoRepo;
    
    protected void setLeaderID (){
        if (galleryInfoRepo.count() > 0) {
            Random random = new Random();
            hubLeaderID = random.nextLong() % (galleryInfoRepo.count()) + 1;
        }
    }
    
    protected void updateServers() {
        Map<Long, URL> allGI = galleryInfoRepo.toMap();
        for (long currentId : allGI.keySet()) {
            // Code to update each IP with the new data
            URL url = galleryInfoRepo.toMap().get(currentId);
            postMapToGallery(url);
            postLeaderToGallery(url);
        }
    }
    
    //This method enables the URL map to be sent to URLs beyond those in the current URL map. 
    //Current use-case is a DELETE, where the URL is deleted from the map, but the gallery located at that URL should still receive that updated map so that it can properly redirect requests. So, this method is called with otherURLs containing the URL of the deleted gallery.
    //Future use-cases for this method might include exporting the URL map to backup servers, or doing a mass delete where more than one URL is deleted from the map and the map needs to be sent to many URLs which are now not present in the map.
    protected void updateServers(List<URL> otherURLs) {
        updateServers();
        for (URL url : otherURLs) {
            postMapToGallery(url);
        }
    }
    
    protected void postLeaderToGallery(URL url) {
        WebClient webClient = WebClient.create(url.toString());
        webClient.post()
                .uri("/galleries/leader")
                .body(Mono.just(hubLeaderID), Long.class)
                .retrieve()
                .bodyToMono(String.class).block();
    }

    protected void postMapToGallery(URL url) {
        WebClient webClient = WebClient.create(url.toString());
        webClient.post()
                .uri("/galleries/servers")
                .body(Mono.just(galleryInfoRepo.toMap()), Map.class)
                .retrieve()
                .bodyToMono(String.class).block();
    }
}