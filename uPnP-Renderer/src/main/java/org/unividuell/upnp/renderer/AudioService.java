package org.unividuell.upnp.renderer;

import java.io.IOException;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.binding.LocalServiceBindingException;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unividuell.upnp.renderer.statemachine.MyRendererNoMediaPresent;

public class AudioService implements Runnable {
    
    static Logger logger = LoggerFactory.getLogger(AudioService.class);

    private final UDN PI_UDN = UDN.uniqueSystemIdentifier("Pi Audio Renderer");
    
    public AVTransportService avTransportService;

    public LocalDevice localDevice;
    
    public static void main(String[] args) throws Exception {
        logger.info("Start a user thread that runs the UPnP stack");
        Thread serverThread = new Thread(new AudioService());
        serverThread.setDaemon(false);
        serverThread.start();
    }

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
            upnpService.getRegistry().addDevice(createDevice());

        } catch (Exception ex) {
            System.err.println("Exception occured: " + ex);
            ex.printStackTrace(System.err);
            System.exit(1);
        }
    }

    public LocalDevice createDevice() throws ValidationException, LocalServiceBindingException, IOException {

        DeviceIdentity identity = new DeviceIdentity(PI_UDN);

        DeviceType type = new UDADeviceType("MediaRenderer", 1);

        DeviceDetails details = new DeviceDetails(
                "Raspberry Pi", 
                new ManufacturerDetails("unividuell"),
                new ModelDetails("AudioRenderer5000", "An Audio Renderer", "v1"));
        /*
         * Icon icon = new Icon( "image/png", 48, 48, 8,
         * getClass().getResource("icon.png") );
         */
        LocalService<AVTransportService> audioService = 
                new AnnotationLocalServiceBinder().read(AVTransportService.class);

        audioService.setManager(new DefaultServiceManager<AVTransportService>(audioService, null) {
            @Override
            protected AVTransportService createServiceInstance() throws Exception {
                return new AVTransportService(
                        MyRendererStateMachine.class,   // All states
                        MyRendererNoMediaPresent.class  // Initial state
                );
            }
        });
        
        this.avTransportService = audioService.getManager().getImplementation();
//        new EventFire(avTransportService).run();
        localDevice = new LocalDevice(identity, type, details, /* icon, */audioService);
        return localDevice;
    }

}