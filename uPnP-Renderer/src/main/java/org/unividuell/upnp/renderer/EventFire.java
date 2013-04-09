package org.unividuell.upnp.renderer;

import org.fourthline.cling.support.lastchange.*;
import org.slf4j.*;

public class EventFire implements Runnable {
    
    final Logger logger = LoggerFactory.getLogger(EventFire.class);
    
    private final LastChangeAwareServiceManager manager;
    
    public EventFire(LastChangeAwareServiceManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        while (true) {
            logger.debug("FIRE!");
            manager.fireLastChange();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
