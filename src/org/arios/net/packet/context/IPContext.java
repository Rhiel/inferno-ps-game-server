package org.arios.net.packet.context;

import org.arios.game.node.entity.player.Player;
import org.arios.net.packet.Context;
import org.arios.tools.IPAddressUtils;

public class IPContext implements Context {

    private final Player player;
    private final String ip;

    public IPContext(Player player, String ip) {
        this.player = player;
        this.ip = ip;
    }

    public int getHash() {
        return IPAddressUtils.ipToInt(ip);
    }

    @Override
    public Player getPlayer() {
        return player;
    }
}