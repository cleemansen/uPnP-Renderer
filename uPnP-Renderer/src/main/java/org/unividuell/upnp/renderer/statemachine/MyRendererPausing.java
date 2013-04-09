package org.unividuell.upnp.renderer.statemachine;

import java.net.*;

import org.fourthline.cling.support.avtransport.impl.state.*;
import org.fourthline.cling.support.model.*;
import org.slf4j.*;
import org.unividuell.upnp.renderer.*;

public class MyRendererPausing extends PausedPlay<AVTransport> {
    
    final Logger logger = LoggerFactory.getLogger(MyRendererPausing.class);

    public MyRendererPausing(AVTransport transport) {
        super(transport);
    }
    
    @Override
    public void onEntry() {
        super.onEntry();
        PlayerBeanHolder.getInstance().getPlayer().pause();
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        logger.info("uri '{}'", uri.toString());
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> stop() {
        logger.info("STOP");
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> play(String speed) {
        logger.info("PLAY");
        return MyRendererPlaying.class;
    }

}
