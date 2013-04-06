package org.unividuell.upnp.renderer;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                    upnpService.shutdown();
                }
            });

            // Add the bound local device to the registry
            upnpService.getRegistry().addDevice(MediaRendererSampleData.createDevice());

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
        
    }

}
