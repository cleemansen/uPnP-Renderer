package org.unividuell.upnp.renderer;

import org.unividuell.jlala.*;

public class PlayerBeanHolder {
    
    static private final PlayerBeanHolder instance = new PlayerBeanHolder();
    
    private Player mplayer;
    
    private PlayerBeanHolder() {
    
    }
    
    public static PlayerBeanHolder getInstance() {
        return instance;
    }
    
    public Player getPlayer() {
        if (mplayer == null) {
            // default
            mplayer = PlayerFactory.getInstance().getMPlayer();
        }
        return mplayer;
    }

    public void setPlayer(Player player) {
        mplayer = player;
    }

}
