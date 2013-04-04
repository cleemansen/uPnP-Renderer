package org.unividuell.upnp.renderer.statemachine;

import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.support.avtransport.impl.state.AbstractState;
import org.teleal.cling.support.avtransport.impl.state.NoMediaPresent;
import org.teleal.cling.support.avtransport.lastchange.AVTransportVariable;
import org.teleal.cling.support.model.AVTransport;
import org.teleal.cling.support.model.MediaInfo;
import org.teleal.cling.support.model.PositionInfo;

public class MyRendererNoMediaPresent extends NoMediaPresent {
    
    final Logger logger = LoggerFactory.getLogger(MyRendererNoMediaPresent.class);

    public MyRendererNoMediaPresent(AVTransport transport) {
        super(transport);
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        logger.info("set transport uri to '{}'", uri.toString());
        logger.info("MetaData: {}", metaData);

        getTransport().setMediaInfo(new MediaInfo(uri.toString(), metaData));

        // If you can, you should find and set the duration of the track here!
        getTransport().setPositionInfo(new PositionInfo(1, metaData, uri.toString()));

        // It's up to you what "last changes" you want to announce to event
        // listeners
        getTransport()
            .getLastChange()
            .setEventedValue(
                    getTransport()
                        .getInstanceId(),
                        new AVTransportVariable.AVTransportURI(uri), 
                        new AVTransportVariable.CurrentTrackURI(uri));

        return MyRendererStopped.class;
    }
}