package edu.yu.cs.hub;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;

@Singleton
public class ServerStartUp {    
    @Inject
    Utility utility;
    
    @Transactional
    public void init(@Observes StartupEvent se) {
        utility.setLeaderID();
        utility.updateServers();
    }
    
}