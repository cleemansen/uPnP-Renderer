package org.unividuell.upnp.renderer;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AudioRenderingControlService extends AbstractAudioRenderingControl {

    final Logger logger = LoggerFactory.getLogger(AudioRenderingControlService.class);
    
    @Override
    public boolean getMute(UnsignedIntegerFourBytes instanceId, String channelName) throws RenderingControlException {
        logger.info("instanceId '{}', channelName '{}'", instanceId, channelName);
        return false;
    }

    @Override
    public void setMute(UnsignedIntegerFourBytes instanceId, String channelName, boolean desiredMute) throws RenderingControlException {
        logger.info("instanceId '{}', channelName '{}', desiredMute '{}'", instanceId, channelName, desiredMute);
    }

    @Override
    public UnsignedIntegerTwoBytes getVolume(UnsignedIntegerFourBytes instanceId, String channelName) throws RenderingControlException {
        return new UnsignedIntegerTwoBytes(50);
    }

    @Override
    public void setVolume(UnsignedIntegerFourBytes instanceId, String channelName, UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException {
        logger.info("instanceId '{}', channelName '{}', desiredVolume '{}'", instanceId, channelName, desiredVolume);
    }

    @Override
    protected Channel[] getCurrentChannels() {
        return new Channel[] {
                Channel.Master
        };
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        return new UnsignedIntegerFourBytes[0];
    }
}