package org.arios.net.packet.in;

import org.arios.game.interaction.NodeUsageEvent;
import org.arios.game.interaction.UseWithHandler;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;

/**
 * The incoming item action packet.
 * @author Emperor
 */
public class ItemActionPacket implements IncomingPacket {

    @SuppressWarnings("unused")
    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
	if (player.getLocks().isInteractionLocked()) {
	    return;
	}
	int usedWithItemId = -1;
	int usedWithSlot = -1;
	int interfaceHash1 = -1;
	int interfaceId1 = -1;
	int childId1 = -1;
	int usedItemId = -1;
	int usedSlot = -1;
	int interfaceHash2 = -1;
	int interfaceId2 = -1;
	int childId2 = -1;
	switch (buffer.opcode()) {
	case 188:
	    interfaceHash1 = buffer.getInt();
	    usedWithItemId = buffer.getShortA();
	    usedWithSlot = buffer.getLEShortA();
	    usedItemId = buffer.getLEShort();
	    usedSlot = buffer.getShort();
	    interfaceHash2 = buffer.getLEInt();
	    interfaceId1 = interfaceHash1 >> 16;
	    childId1 = interfaceHash1 & 0xFFFF;
	    interfaceId2 = interfaceHash2 >> 16;
	    childId2 = interfaceHash2 & 0xFFFF;
	    break;
	default:
	    return;
	}
	Item used = player.getInventory().get(usedSlot);
	Item with = player.getInventory().get(usedWithSlot);
	if (used == null || with == null || used.getId() != usedItemId || with.getId() != usedWithItemId) {
	    return;
	}
	if (usedItemId < usedWithItemId) {
	    Item item = used;
	    used = with;
	    with = item;
	}
	NodeUsageEvent event = new NodeUsageEvent(player, interfaceId1, used, with);
	UseWithHandler.run(event);
    }
}