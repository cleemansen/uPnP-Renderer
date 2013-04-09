package org.unividuell.upnp.renderer;

import org.fourthline.cling.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.support.lastchange.*;
import org.slf4j.*;

public class MyStarter implements Runnable {
    
    final static Logger logger = LoggerFactory.getLogger(MyStarter.class);
    
    public static void main(String[] args) {
        logger.info("Start a user thread that runs the UPnP stack");
        Thread serverThread = new Thread(new MyStarter());
        serverThread.setDaemon(false);
        serverThread.start();
    }

    @Override
    public void run() {
        try {
            final UpnpService upnpService = new UpnpServiceImpl();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    PlayerBeanHolder.getInstance().getPlayer().close();
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            LocalDevice localDevice = UPnPRenderer.createDevice();
            upnpService.getRegistry().addDevice(localDevice);
            
            // Yes, it's a bit awkward to get the LastChange without a controlpoint
            LastChangeAwareServiceManager manager = (LastChangeAwareServiceManager) localDevice.findServices()[0].getManager();
            new EventFire(manager).run();
            
        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        
    }

}
