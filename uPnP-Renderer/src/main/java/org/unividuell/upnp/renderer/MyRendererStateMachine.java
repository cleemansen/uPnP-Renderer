package org.unividuell.upnp.renderer;

import org.fourthline.cling.support.avtransport.impl.AVTransportStateMachine;
import org.seamless.statemachine.States;
import org.unividuell.upnp.renderer.statemachine.MyRendererNoMediaPresent;
import org.unividuell.upnp.renderer.statemachine.MyRendererPausing;
import org.unividuell.upnp.renderer.statemachine.MyRendererPlaying;
import org.unividuell.upnp.renderer.statemachine.MyRendererStopped;

@States({
    MyRendererNoMediaPresent.class,
    MyRendererStopped.class,
    MyRendererPlaying.class,
    MyRendererPausing.class
})
interface MyRendererStateMachine extends AVTransportStateMachine {}
