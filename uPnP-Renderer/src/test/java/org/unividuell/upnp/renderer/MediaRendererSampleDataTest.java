package org.unividuell.upnp.renderer;

import static org.fest.assertions.Assertions.assertThat;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.fest.assertions.Fail;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.support.avtransport.callback.GetCurrentTransportActions;
import org.fourthline.cling.support.avtransport.callback.GetDeviceCapabilities;
import org.fourthline.cling.support.avtransport.callback.GetMediaInfo;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.GetTransportInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.avtransport.impl.AVTransportService;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.model.DeviceCapabilities;
import org.fourthline.cling.support.model.MediaInfo;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.junit.Before;
import org.junit.Test;
import org.unividuell.upnp.renderer.MediaRendererSampleData.AudioRenderingControlService;

public class MediaRendererSampleDataTest {
    
    LocalService<AVTransportService> service;
    LocalService<AudioRenderingControlService> createRenderingControlService;
    
    final String[] lcValue = new String[1];
    
    LastChangeAwareServiceManager manager;
    
    @Before
    public void setup() throws Throwable {
        service = MediaRendererSampleData.createAVTransportService();
        createRenderingControlService = MediaRendererSampleData.createRenderingControlService();
        
        // Yes, it's a bit awkward to get the LastChange without a controlpoint
        PropertyChangeSupport pcs = service.getManager().getPropertyChangeSupport();
        pcs.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent ev) {
                if (ev.getPropertyName().equals("LastChange"))
                    lcValue[0] = (String) ev.getNewValue();
            }
        });
        
        // prepare
        ActionCallback setAVTransportURIAction = 
                new SetAVTransportURI(
                        service, 
                        "http://10.0.0.1/file.mp3", 
                        "NO METADATA") {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Fail.fail("Something was wrong!");
            }
        };
        setAVTransportURIAction.run();
        
        manager = (LastChangeAwareServiceManager)service.getManager();
        manager.fireLastChange();
    }

    @Test
    public void deviceCapabilities() {

        ActionCallback getDeviceCapsAction =
                new GetDeviceCapabilities(service) {
                    @Override
                    public void received(ActionInvocation actionInvocation, DeviceCapabilities caps) {
                        assertThat(caps.getPlayMedia()[0].toString()).isEqualTo("NETWORK");
                        assertThat(caps.getRecMedia()[0].toString()).isEqualTo("NOT_IMPLEMENTED");
                        assertThat(caps.getRecQualityModes()[0].toString()).isEqualTo("NOT_IMPLEMENTED");
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        Fail.fail("Something was wrong!");
                    }
                };
        getDeviceCapsAction.run();
    }
    
    @Test
    public void setAVTransportURIAction_MediaInfo_PositionInfoAction() {
        
        // prepare
        ActionCallback getTransportInfo =
                new GetTransportInfo(service) {
                    @Override
                    public void received(ActionInvocation invocation, TransportInfo transportInfo) {
                        // verify
                        assertThat(transportInfo.getCurrentTransportState()).isEqualTo(TransportState.STOPPED);
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        Fail.fail("Something was wrong!");
                    }
                };
        // execute
        getTransportInfo.run();
        
        ActionCallback getMediaInfoAction =
                new GetMediaInfo(service) {
                    @Override
                    public void received(ActionInvocation invocation, MediaInfo mediaInfo) {
                        assertThat(mediaInfo.getCurrentURI()).isEqualTo("http://10.0.0.1/file.mp3");
                        assertThat(mediaInfo.getCurrentURIMetaData()).isEqualTo("NO METADATA");
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        Fail.fail("Something was wrong!");
                    }
                };
        getMediaInfoAction.run();
        
        ActionCallback getPositionInfoAction =
                new GetPositionInfo(service) {
                    @Override
                    public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                        assertThat(positionInfo.getTrackURI()).isEqualTo("http://10.0.0.1/file.mp3");
                        assertThat(positionInfo.getTrackMetaData()).isEqualTo("NO METADATA");
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        System.err.println(defaultMsg);
                        Fail.fail("Something was wrong!");
                    }
                };
        getPositionInfoAction.run();
    }
    
    @Test
    public void getCurrentTransportActions() {
        
        ActionCallback getCurrentTransportActions =
                new GetCurrentTransportActions(service) {
                    @Override
                    public void received(ActionInvocation invocation, TransportAction[] actions) {
                        List<TransportAction> currentActions = Arrays.asList(actions);
                        assertThat(currentActions)
                            .containsExactly(
                                TransportAction.Stop,
                                TransportAction.Play,
                                TransportAction.Next,
                                TransportAction.Previous,
                                TransportAction.Seek);
                    }

                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        Fail.fail("Something was wrong!");
                    }
                };
        getCurrentTransportActions.run();
    }
    
    @Test
    public void inStop() throws Throwable {
        
        // prepare                                                                      // DOC:INC2
        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange( // DOC:CTRL3
                new AVTransportLastChangeParser(),
                lastChangeString
        );
        assertThat(
                lastChange.getEventedValue(
                        0, // Instance ID!
                        AVTransportVariable.AVTransportURI.class
                ).getValue())
                    .isEqualTo(URI.create("http://10.0.0.1/file.mp3"));
        assertThat(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.CurrentTrackURI.class
                ).getValue())
                .isEqualTo(URI.create("http://10.0.0.1/file.mp3"));
        assertThat(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.TransportState.class
                ).getValue())
                .isEqualTo(TransportState.STOPPED);
    }
    
    @Test
    public void inPlay() throws Throwable {
        
        ActionCallback playAction = // DOC:CTRL2
                new Play(service) {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                }; // DOC:CTRL2
        playAction.run();

        manager.fireLastChange();
        
        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(
                new AVTransportLastChangeParser(),
                lastChangeString
        );
        assertThat(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.TransportState.class
                ).getValue())
                    .isEqualTo(TransportState.PLAYING);
    }
    
    @Test
    public void inStop2() throws Throwable {
        
        ActionCallback stopAction = // DOC:CTRL2
                new Stop(service) {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                };
        stopAction.run();
        
        manager.fireLastChange();
        
        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(
                new AVTransportLastChangeParser(),
                lastChangeString
                );
        assertThat(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.TransportState.class
                        ).getValue())
                        .isEqualTo(TransportState.STOPPED);
    }
    
    @Test
    public void inPause() throws Throwable {
        ActionCallback playAction = // DOC:CTRL2
                new Play(service) {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
                }; // DOC:CTRL2
        playAction.run();
        manager.fireLastChange();
        
        ActionCallback pauseAction = // DOC:CTRL2
                new Pause(service) {
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        // Something was wrong
                    }
        };
        pauseAction.run();
        manager.fireLastChange();
        
        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(
                new AVTransportLastChangeParser(),
                lastChangeString
                );
        assertThat(
                lastChange.getEventedValue(
                        0,
                        AVTransportVariable.TransportState.class
                        ).getValue())
                        .isEqualTo(TransportState.PAUSED_PLAYBACK);
    }

}
