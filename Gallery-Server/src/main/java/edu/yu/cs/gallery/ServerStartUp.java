package edu.yu.cs.gallery;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.web.reactive.function.client.WebClient;

import edu.yu.cs.gallery.repositories.GalleryRepository;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import reactor.core.publisher.Mono;

public class ServerStartUp {

    @Inject
    GalleryRepository gr;
    
    static Gallery gallery;

    @Transactional
    public void initEagerly(@Observes StartupEvent se) throws MalformedURLException {
        gallery = gr.findAll().firstResult();
            if (gallery != null) {
                gallery.url = new URL("https://" + System.getenv("URL") + ".ngrok.io");
            }
    }

    @QuarkusMain
    public static class init implements QuarkusApplication {
        @Override
        public int run(String... args) throws Exception {
            if (gallery != null) {
                WebClient webClient = WebClient.create("https://hubserver.ngrok.io");
                webClient.put()
                        .uri("/hub/" + gallery.id)
                        .body(Mono.just(new URL("https://" + System.getenv("URL") + ".ngrok.io")), URL.class)
                        .retrieve()
                        .bodyToMono(String.class).block();
            }
            Quarkus.waitForExit();
            return 0;
        }
    }
}