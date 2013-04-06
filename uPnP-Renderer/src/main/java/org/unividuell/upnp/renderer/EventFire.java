package org.unividuell.upnp.renderer;

import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFire implements Runnable {
    
    final Logger logger = LoggerFactory.getLogger(EventFire.class);
    
    private AVTransportService service;
    
    public EventFire(AVTransportService service) {
        this.service = service;
    }

    public void run() {
        logger.debug("FIRE!");
//        service.fireLastChange();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        new EventFire(service).run();
    }

}
