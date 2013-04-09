package org.unividuell.upnp.renderer;

import static org.fest.assertions.Assertions.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

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
import org.fourthline.cling.support.avtransport.callback.Seek;
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
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.model.TransportAction;
import org.fourthline.cling.support.model.TransportInfo;
import org.fourthline.cling.support.model.TransportState;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.unividuell.jlala.Player;

@RunWith(MockitoJUnitRunner.class)
public class UPnPRendererTest {

    LocalService<AVTransportService> service;
    LocalService<AudioRenderingControlService> createRenderingControlService;

    final String[] lcValue = new String[1];

    LastChangeAwareServiceManager manager;
    
    @Mock
    Player mockPlayer;
    
    @Before
    public void setup() throws Throwable {
        service = UPnPRenderer.createAVTransportService();
        createRenderingControlService = UPnPRenderer.createRenderingControlService();
        
        // Mock
        PlayerBeanHolder.getInstance().setPlayer(mockPlayer);
        
        // Yes, it's a bit awkward to get the LastChange without a controlpoint
        PropertyChangeSupport pcs = service.getManager().getPropertyChangeSupport();
        pcs.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent ev) {
                if (ev.getPropertyName().equals("LastChange"))
                    lcValue[0] = (String) ev.getNewValue();
            }
        });

        // prepare
        ActionCallback setAVTransportURIAction = new SetAVTransportURI(service, "http://10.0.0.1/file.mp3",
                "NO METADATA") {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                
                Fail.fail("Something was wrong! " + defaultMsg);
            }
        };
        setAVTransportURIAction.run();
        // we start in stop mode after setting the uri.
        verify(mockPlayer).stop();

        manager = (LastChangeAwareServiceManager) service.getManager();
        manager.fireLastChange();
    }

    @Test
    public void deviceCapabilities() {

        ActionCallback getDeviceCapsAction = new GetDeviceCapabilities(service) {
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
        ActionCallback getTransportInfo = new GetTransportInfo(service) {
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

        ActionCallback getMediaInfoAction = new GetMediaInfo(service) {
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

        ActionCallback getPositionInfoAction = new GetPositionInfo(service) {
            @Override
            public void received(ActionInvocation invocation, PositionInfo positionInfo) {
                assertThat(positionInfo.getTrackURI()).isEqualTo("http://10.0.0.1/file.mp3");
                assertThat(positionInfo.getTrackMetaData()).isEqualTo("NOT_IMPLEMENTED");
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

        ActionCallback getCurrentTransportActions = new GetCurrentTransportActions(service) {
            @Override
            public void received(ActionInvocation invocation, TransportAction[] actions) {
                List<TransportAction> currentActions = Arrays.asList(actions);
                assertThat(currentActions).containsExactly(TransportAction.Stop, TransportAction.Play,
                        TransportAction.Next, TransportAction.Previous, TransportAction.Seek);
            }

            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                Fail.fail("Something was wrong!");
            }
        };
        getCurrentTransportActions.run();
    }

    @Test
    public void afterTransportURIAction_PressStop() throws Throwable {

        // prepare // DOC:INC2
        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange( // DOC:CTRL3
                new AVTransportLastChangeParser(), lastChangeString);
        assertThat(lastChange.getEventedValue(0, // Instance ID!
                AVTransportVariable.AVTransportURI.class).getValue()).isEqualTo(URI.create("http://10.0.0.1/file.mp3"));
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.CurrentTrackURI.class).getValue()).isEqualTo(
                URI.create("http://10.0.0.1/file.mp3"));
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.STOPPED);
    }

    @Test
    public void inStop_PressPlay() throws Throwable {

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
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.PLAYING);
        
        // verify
        verify(mockPlayer).loadAndPlay(anyString(), anyBoolean());
    }

    @Test
    public void inStop_PressPause() throws Throwable {
        // prepare
        // this action is not present in mode STOP
        ActionCallback pauseAction = // DOC:CTRL2
        new Pause(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };

        // execute
        pauseAction.run();
        manager.fireLastChange();

        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);

        // verify
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.STOPPED);
        verify(mockPlayer).stop();
    }

    @Test
    public void inPlay_PressPause() throws Throwable {
        // prepare

        ActionCallback playAction = new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        };
        playAction.run();

        ActionCallback pauseAction = // DOC:CTRL2
        new Pause(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };

        // execute
        pauseAction.run();
        manager.fireLastChange();

        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);

        // verify
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.PAUSED_PLAYBACK);
        
        verify(mockPlayer).stop();
        verify(mockPlayer).loadAndPlay(anyString(), anyBoolean());
        verify(mockPlayer).pause();
        verifyNoMoreInteractions(mockPlayer);
    }

    @Test
    public void inPlay_PressStop() throws Throwable {
        // prepare

        ActionCallback playAction = new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        };
        playAction.run();

        ActionCallback stopAction = // DOC:CTRL2
        new Stop(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };

        // execute
        stopAction.run();
        manager.fireLastChange();

        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);

        // verify
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.STOPPED);
        
        verify(mockPlayer, times(2)).stop();
        verify(mockPlayer).loadAndPlay(anyString(), anyBoolean());
        verifyNoMoreInteractions(mockPlayer);
    }
    
    @Test
    public void inPlay_PressSeek() throws Throwable {
        // prepare
        
        ActionCallback playAction = new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        };
        playAction.run();
        
        ActionCallback seekAction = // DOC:CTRL2
                new Seek(service, SeekMode.ABS_TIME, "zwšlf") {
                    
                    @Override
                    public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                        Fail.fail("not good!");
                    }
                };
        
        // execute
        seekAction.run();
        manager.fireLastChange();
        
        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);
        
        // verify
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.PLAYING);
//        verify(mockPlayer).play();
//        verify(mockPlayer).stop();
//        verifyNoMoreInteractions(mockPlayer);
    }

    @Test
    public void inPause_PressPause() throws Throwable {
        // prepare

        ActionCallback playAction = new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        };
        playAction.run();

        ActionCallback pauseAction = // DOC:CTRL2
        new Pause(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };
        pauseAction.run();
        pauseAction = // DOC:CTRL2
        new Pause(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };

        // execute
        pauseAction.run();
        manager.fireLastChange();

        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);

        // verify
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.PAUSED_PLAYBACK);
    }

    @Test
    public void inPause_PressPlay() throws Throwable {
        // prepare

        ActionCallback playAction = new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        };
        playAction.run();

        ActionCallback pauseAction = // DOC:CTRL2
        new Pause(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };
        pauseAction.run();
        playAction = // DOC:CTRL2
        new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };

        // execute
        playAction.run();
        manager.fireLastChange();

        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);

        // verify
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.PLAYING);
    }

    @Test
    public void inPause_PressStop() throws Throwable {
        // prepare

        ActionCallback playAction = new Play(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
            }
        };
        playAction.run();

        ActionCallback pauseAction = // DOC:CTRL2
        new Pause(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };
        pauseAction.run();
        ActionCallback stopAction = new Stop(service) {
            @Override
            public void failure(ActionInvocation invocation, UpnpResponse operation, String defaultMsg) {
                // Something was wrong
            }
        };

        // execute
        stopAction.run();
        manager.fireLastChange();

        String lastChangeString = lcValue[0];
        LastChange lastChange = new LastChange(new AVTransportLastChangeParser(), lastChangeString);

        // verify
        assertThat(lastChange.getEventedValue(0, AVTransportVariable.TransportState.class).getValue()).isEqualTo(
                TransportState.STOPPED);
    }

}
