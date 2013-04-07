package org.unividuell.upnp.renderer.statemachine;

import java.net.*;

import org.fourthline.cling.support.avtransport.impl.state.*;
import org.fourthline.cling.support.model.*;
import org.slf4j.*;
import org.unividuell.upnp.renderer.*;

public class MyRendererStopped extends Stopped<AVTransport> {
    
    final Logger logger = LoggerFactory.getLogger(MyRendererStopped.class);

    public MyRendererStopped(AVTransport transport) {
        super(transport);
    }

    @Override
    public void onEntry() {
        super.onEntry();
//        logger.info("on entry MyRendererStopped");
        // Optional: Stop playing, release resources, etc.
        PlayerBeanHolder.getInstance().getPlayer().stop();
    }

    public void onExit() {
        logger.info("on exit MyRendererStopped");
        // Optional: Cleanup etc.
    }

    @Override
    public Class<? extends AbstractState> setTransportURI(URI uri, String metaData) {
        // This operation can be triggered in any state, you should think
        // about how you'd want your player to react. If we are in Stopped
        // state nothing much will happen, except that you have to set
        // the media and position info, just like in MyRendererNoMediaPresent.
        // However, if this would be the MyRendererPlaying state, would you
        // prefer stopping first?
        logger.info("setTransportURI in Stopped with uri '{}'", uri.toString());
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> stop() {
        // / Same here, if you are stopped already and someone calls STOP,
        // well...
//        logger.info("STOP MyRendererStopped");
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> play(String speed) {
        // It's easier to let this classes' onEntry() method do the work
//        logger.info("PLAY MyRendererStopped");
        return MyRendererPlaying.class;
    }

    @Override
    public Class<? extends AbstractState> next() {
//        logger.info("NEXT MyRendererStopped");
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> previous() {
//        logger.info("PREV MyRendererStopped");
        return MyRendererStopped.class;
    }

    @Override
    public Class<? extends AbstractState> seek(SeekMode unit, String target) {
        // Implement seeking with the stream in stopped state!
        logger.info("unit '{}', target '{}', ", unit, target);
        return MyRendererStopped.class;
    }
}