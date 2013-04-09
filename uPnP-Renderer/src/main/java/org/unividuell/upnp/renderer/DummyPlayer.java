package org.unividuell.upnp.renderer;

import java.io.*;

import org.slf4j.*;
import org.unividuell.jlala.*;

public class DummyPlayer implements Player {

    final Logger logger = LoggerFactory.getLogger(DummyPlayer.class);
    
    @Override
    public void stop() {
        logger.info("stop: ");
    }

    @Override
    public void play() {
        logger.info("play: ");

    }

    @Override
    public void togglePause() {

        logger.info("togglePause: ");
    }

    @Override
    public void pause() {

        logger.info("pause: ");
    }

    @Override
    public void setVolume(float vol) {

        logger.info("setVolume: vol '{}'", vol);
    }

    @Override
    public long getVolume() {

        logger.info("getVolume: ");
        return 0;
    }

    @Override
    public void changeVolumeRelative(float change) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTrackPositionPercentage(float percentage) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setTrackPositionAbsolute(long milliseconds) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getTrackPosition() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public float getTrackPositionPercentage() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void loadFile(String uri, boolean appendToPlaylist) throws IOException {
        logger.info("loadFile: " + "uri '{}', appendToPlaylist '{}', ", uri, appendToPlaylist);

    }

    @Override
    public void muteToggle() {
        // TODO Auto-generated method stub

    }

    @Override
    public void muteOn() {
        // TODO Auto-generated method stub

    }

    @Override
    public void muteOff() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        logger.info("close: ");
        
    }

}
