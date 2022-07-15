package edu.yu.cs.gallery;

import java.net.MalformedURLException;
import java.net.URL;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.springframework.web.reactive.function.client.WebClient;

import edu.yu.cs.gallery.repositories.GalleryRepository;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.QuarkusMain;
import reactor.core.publisher.Mono;

@QuarkusMain
public class ServerStartUp implements QuarkusApplication{

    @Inject
    GalleryRepository gr;
    @Inject
    Utility utility;


    @ConfigProperty(name = "serverURL")
    String serverURL;

    @ConfigProperty(name = "hubURL")
    String hubURL;

    @Transactional
    public void init(@Observes StartupEvent se) throws MalformedURLException {
        utility.gallery = gr.findAll().firstResult();
        if (utility.gallery != null) {
            utility.gallery.url = new URL("https://" + serverURL);
        }
    }

    @Override
    public int run(String... args) throws Exception {
        if (utility.gallery != null) {
            WebClient webClient = WebClient.create("https://" + hubURL);
            webClient.put()
                    .uri("/hub/" + utility.gallery.id)
                    .body(Mono.just(utility.gallery.url), URL.class)
                    .retrieve()
                    .bodyToMono(String.class).block();
        }
        Quarkus.waitForExit();
        return 0;
}
}