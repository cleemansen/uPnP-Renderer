package org.unividuell.upnp.renderer.statemachine;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.avtransport.impl.state.PausedPlay;
import org.teleal.cling.support.model.AVTransport;

public class MyRendererPausing extends PausedPlay<AVTransport> {
    
    final Logger logger = LoggerFactory.getLogger(MyRendererPausing.class);

    public MyRendererPausing(AVTransport transport) {
        super(transport);
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        logger.info("uri '{}'", uri.toString());
        return MyRendererPausing.class;
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
