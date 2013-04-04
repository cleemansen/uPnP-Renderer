package org.unividuell.upnp.renderer;

import org.teleal.cling.support.avtransport.impl.AVTransportStateMachine;
import org.teleal.common.statemachine.States;
import org.unividuell.upnp.renderer.statemachine.MyRendererNoMediaPresent;
import org.unividuell.upnp.renderer.statemachine.MyRendererPlaying;
import org.unividuell.upnp.renderer.statemachine.MyRendererStopped;

@States({
    MyRendererNoMediaPresent.class,
    MyRendererStopped.class,
    MyRendererPlaying.class
})
interface MyRendererStateMachine extends AVTransportStateMachine {}
