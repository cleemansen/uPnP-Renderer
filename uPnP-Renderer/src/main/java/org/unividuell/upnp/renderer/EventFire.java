package org.unividuell.upnp.renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.support.avtransport.impl.AVTransportService;

public class EventFire implements Runnable {
    
    final Logger logger = LoggerFactory.getLogger(EventFire.class);
    
    private AVTransportService service;
    
    public EventFire(AVTransportService service) {
        this.service = service;
    }

    public void run() {
        logger.debug("FIRE!");
        service.fireLastChange();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        new EventFire(service).run();
    }

}
