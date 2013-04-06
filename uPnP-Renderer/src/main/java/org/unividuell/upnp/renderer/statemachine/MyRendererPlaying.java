package org.unividuell.upnp.renderer.statemachine;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.avtransport.impl.state.Playing;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.SeekMode;

public class MyRendererPlaying extends Playing {
    
    final Logger logger = LoggerFactory.getLogger(MyRendererPlaying.class);

    public MyRendererPlaying(AVTransport transport) {
        super(transport);
    }

    @Override
    public void onEntry() {
        super.onEntry();
        // Start playing now!
        logger.info("on entry MyRendererPlaying");
        logger.info("I'm playing now some music!");
        logger.info("lalalalalala *************************************");
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        // Your choice of action here, and what the next state is going to be!
        logger.info("setTransportURI in Playing with uri '{}'", uri.toString());
        return MyRendererPlaying.class;
    }

    @Override
    public Class<? extends AbstractState> stop() {
        // Stop playing!
        logger.info("STOP in Playing");
        return MyRendererStopped.class;
    }

    @Override
    public Class play(String speed) {
        logger.info("PLAY in Playing");
        return null;
    }

    @Override
    public Class pause() {
        logger.info("PAUSE in Playing");
        return null;
    }

    @Override
    public Class next() {
        logger.info("NEXT in Playing");
        return null;
    }

    @Override
    public Class previous() {
        logger.info("PREV in Playing");
        return null;
    }

    @Override
    public Class seek(SeekMode unit, String target) {
        logger.info("SEEK in Playing with SeekMode '{}' and target '{}'", unit.toString(), target);
        return null;
    }

}
