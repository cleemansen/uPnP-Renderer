package org.unividuell.upnp.renderer;

import org.unividuell.jlala.*;

public class PlayerBeanHolder {
    
    static private final PlayerBeanHolder instance = new PlayerBeanHolder();
    
    private Player mplayer;
    
    private PlayerBeanHolder() {
        if (mplayer == null) {
            // default
            mplayer = PlayerFactory.getInstance().getMPlayer();
            // dummy
//            mplayer = new DummyPlayer();
        }
    }
    
    public static PlayerBeanHolder getInstance() {
        return instance;
    }
    
    public Player getPlayer() {
        return mplayer;
    }

    public void setPlayer(Player player) {
        mplayer = player;
    }

}
