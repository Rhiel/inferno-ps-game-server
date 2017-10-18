package org.arios.net.packet.in;

import org.arios.game.node.entity.player.Player;
import org.arios.game.system.communication.CommunicationInfo;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;
import org.arios.tools.StringUtils;

/**
 * Represents the packet used to handle all incoming packets related to
 * communication.
 * @author 'Vexia
 */
public final class CommunicationPacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
	switch (buffer.opcode()) {
	case 92:
	    CommunicationInfo.add(player, StringUtils.longToString(buffer.getLong()));
	    break;
	case 47:
	    CommunicationInfo.remove(player, StringUtils.longToString(buffer.getLong()), false);
	    break;
	case 137:
	    CommunicationInfo.block(player, StringUtils.longToString(buffer.getLong()));
	    break;
	case 13:
	    CommunicationInfo.remove(player, StringUtils.longToString(buffer.getLong()), true);
	    break;
	case 69:
	    String name = StringUtils.longToString(buffer.getLong());
	    String message = StringUtils.decryptPlayerChat(buffer, buffer.get() & 0xFF);
	    if (player.getDetails().getPortal().isMuted()) {
		player.getPacketDispatch().sendMessage("You have been " + (player.getDetails().getPortal().isPermMute() ? "permanently" : "temporarily") + " muted due to breaking a rule.");
		return;
	    }
	    CommunicationInfo.sendMessage(player, name, message);
	    break;
	}
    }

}
