package org.arios.net.packet.in;

import org.arios.game.container.Container;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;

/**
 * Represents the packet to handle an item slot switch.
 * @author 'Vexia
 */
public class SlotSwitchPacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
	final int slot = buffer.getShort();
	final int interfaceId = (buffer.getIntA() >> 16);
	final boolean insert = buffer.get() == 1;
	final int secondSlot = buffer.getLEShortA();
	final Container container = interfaceId == 12 ? player.getBank() : (interfaceId == 15 || interfaceId == 149 || interfaceId == 11) ? player.getInventory() : null;
	if (container == null) {
	    return;
	}
	final Item item = container.get(slot);
	final Item second = container.get(secondSlot);
	if (item == null) {
	    return;
	}
	if (!insert) {
	    container.replace(second, slot);
	    container.replace(item, secondSlot);
	    if (item != null) {
		item.setIndex(secondSlot);
	    }
	    if (second != null) {
		second.setIndex(slot);
	    }
	} else {
	    container.insert(slot, secondSlot);
	}
    }

}
