package org.unividuell.upnp.renderer;

import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventFire implements Runnable {
    
    final Logger logger = LoggerFactory.getLogger(EventFire.class);
    
    private LastChangeAwareServiceManager manager;
    
    public EventFire(LastChangeAwareServiceManager manager) {
        this.manager = manager;
    }

    public void run() {
        while (true) {
            logger.debug("FIRE!");
            manager.fireLastChange();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
