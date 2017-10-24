package org.arios.net.packet.in;

import org.arios.game.node.entity.player.Player;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;

public class DisplayUpdatePacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
        int windowMode = buffer.get(); //Window mode
        int screenWidth = buffer.getShort();
        int screenHeight = buffer.getShort();
        player.getDetails().getClientInfo().setScreenWidth(screenWidth);
        player.getDetails().getClientInfo().setScreenHeight(screenHeight);
        player.getInterfaceManager().switchWindowMode(windowMode);
    }

}
