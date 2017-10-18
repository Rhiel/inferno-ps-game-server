package org.arios.net.packet.in;

import org.arios.game.interaction.NodeUsageEvent;
import org.arios.game.interaction.UseWithHandler;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.game.node.object.GameObject;
import org.arios.game.world.map.RegionManager;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.PlayerContext;
import org.arios.net.packet.out.ClearMinimapFlag;

/**
 * Handles the item on object packethandler.
 * @author 'Vexia
 */
public class ItemOnObjectPacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
	if (player.getLocks().isInteractionLocked()) {
	    return;
	}
	switch (buffer.opcode()) {
	case 122:
	    int x = buffer.getShort();
	    int slot = buffer.getShort();
	    buffer.getIntA();
	    int id = buffer.getLEShort();
	    int y = buffer.getLEShortA();
	    int objectId = buffer.getLEShort();
	    int z = player.getLocation().getZ();
	    GameObject object = RegionManager.getObject(z, x, y);
	    if (object == null || object.getId() != objectId) {
		PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
		return;
	    }
	    object = object.getChild(player);
	    if (object == null) {
		PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
		break;
	    }
	    final Item used = player.getInventory().get(slot);
	    if (used == null || used.getId() != id) {
		return;
	    }
	    final NodeUsageEvent event = new NodeUsageEvent(player, 0, used, object);
	    UseWithHandler.run(event);
	    break;
	}
    }

}
