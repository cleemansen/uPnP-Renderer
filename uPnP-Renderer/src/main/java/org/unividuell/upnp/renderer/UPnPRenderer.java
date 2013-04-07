/*
 * Copyright (C) 2013 4th Line GmbH, Switzerland
 *
 * The contents of this file are subject to the terms of either the GNU
 * Lesser General Public License Version 2 or later ("LGPL") or the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */
package org.unividuell.upnp.renderer;

import org.fourthline.cling.binding.annotations.*;
import org.fourthline.cling.model.meta.*;
import org.fourthline.cling.model.types.*;
import org.fourthline.cling.support.avtransport.impl.*;
import org.fourthline.cling.support.avtransport.lastchange.*;
import org.fourthline.cling.support.lastchange.*;
import org.fourthline.cling.support.renderingcontrol.lastchange.*;
import org.slf4j.*;
import org.unividuell.upnp.renderer.service.*;
import org.unividuell.upnp.renderer.statemachine.*;

/**
 */
public class UPnPRenderer {
    
    final Logger logger = LoggerFactory.getLogger(UPnPRenderer.class);

    public static LocalService<AVTransportService> createAVTransportService() throws Exception {

        LocalService<AVTransportService> service = 
                new AnnotationLocalServiceBinder().read(MPlayerAVTransportService.class);

        // Service's which have "logical" instances are very special, they use the
        // "LastChange" mechanism for eventing. This requires some extra wrappers.
        LastChangeParser lastChangeParser = new AVTransportLastChangeParser();

        service.setManager(
                new LastChangeAwareServiceManager<AVTransportService>(service, lastChangeParser) {
                    @Override
                    protected AVTransportService createServiceInstance() throws Exception {
                        return new MPlayerAVTransportService(
                                MyRendererStateMachine.class,   // All states
                                MyRendererNoMediaPresent.class  // Initial state
                        );
                    }
                }
        );
        return service;
    }
    
    public static LocalService<AudioRenderingControlService> createRenderingControlService() throws Exception {

        LocalService<AudioRenderingControlService> service =
                new AnnotationLocalServiceBinder().read(AudioRenderingControlService.class);

        LastChangeParser lastChangeParser = new RenderingControlLastChangeParser();

        service.setManager(
                new LastChangeAwareServiceManager<AudioRenderingControlService>(
                        service,
                        AudioRenderingControlService.class,
                        lastChangeParser
                )
        );
        return service;
    }

    public static LocalDevice createDevice() throws Exception {
        return new LocalDevice(
                new DeviceIdentity(UDN.uniqueSystemIdentifier("uPnP JLaLa")),
                new UDADeviceType("MediaRenderer"),
                new DeviceDetails("uPnP JLaLa"),
                new LocalService[]{
                        createAVTransportService(),
                        createRenderingControlService()
                }
        );
    }

}
