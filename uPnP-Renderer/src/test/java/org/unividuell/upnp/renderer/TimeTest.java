package org.unividuell.upnp.renderer;

import static org.fest.assertions.Assertions.assertThat;

import org.fourthline.cling.model.*;
import org.junit.*;

public class TimeTest {
    
    @Test
    public void timeString() throws Exception {
        // prepare
        String raw = "10.321270";
        float jlalaParser = Float.parseFloat(raw);
        long jlala = (long) jlalaParser;
        
        // execute
        String actual = ModelUtil.toTimeString(jlala);
        
        // verify
        assertThat(actual).isEqualTo("00:00:10");
    }

}
