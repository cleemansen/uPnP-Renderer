package org.unividuell.upnp.renderer.service;

import org.fourthline.cling.model.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.support.avtransport.*;
import org.fourthline.cling.support.avtransport.impl.*;
import org.fourthline.cling.support.model.*;
import org.slf4j.*;

public class MPlayerAVTransportService extends AVTransportService {
    
    final Logger logger = LoggerFactory.getLogger(MPlayerAVTransportService.class);
    
    private static int cnt = 122;

    public MPlayerAVTransportService(Class stateMachineDefinition, Class initialState) {
        super(stateMachineDefinition, initialState);
        logger.info("stateMachineDefinition '{}', initialState '{}', ", stateMachineDefinition, initialState);
    }
    
    @Override
    public PositionInfo getPositionInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        logger.info("instanceId '{}', ", instanceId);
        // long track, String trackDuration, String trackURI, String relTime, String absTime
        PositionInfo info = new PositionInfo(0, ModelUtil.toTimeString(226), "a uri", ModelUtil.toTimeString(++cnt), ModelUtil.toTimeString(cnt));
        logger.info("abs time '{}', total '{}'",
                info.getAbsTime(),
                info.getTrackDurationSeconds());
        return info;
    }
    
    
    @Override
    public DeviceCapabilities getDeviceCapabilities(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        DeviceCapabilities capabilities = super.getDeviceCapabilities(instanceId);
        logger.info("PlayMedia '{}'", capabilities.getPlayMediaString());
        return capabilities;
    }
    
    @Override
    public MediaInfo getMediaInfo(UnsignedIntegerFourBytes instanceId) throws AVTransportException {
        MediaInfo mediaInfo = super.getMediaInfo(instanceId);
        logger.info("mediaDuration '{}', currentURI '{}'", mediaInfo.getMediaDuration(), mediaInfo.getCurrentURI());
        return mediaInfo;
    }

}
