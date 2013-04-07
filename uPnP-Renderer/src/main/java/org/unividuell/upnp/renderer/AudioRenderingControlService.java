package org.unividuell.upnp.renderer;

import org.fourthline.cling.model.types.*;
import org.fourthline.cling.support.model.*;
import org.fourthline.cling.support.renderingcontrol.*;
import org.slf4j.*;

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
//        return new UnsignedIntegerTwoBytes(PlayerBeanHolder.getInstance().getPlayer().getVolume());
        return new UnsignedIntegerTwoBytes(50);
    }

    @Override
    public void setVolume(UnsignedIntegerFourBytes instanceId, String channelName, UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException {
        logger.info("instanceId '{}', channelName '{}', desiredVolume '{}'", instanceId, channelName, desiredVolume);
        PlayerBeanHolder.getInstance().getPlayer().setVolume(desiredVolume.getValue());
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