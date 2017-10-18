package org.arios.net.packet.out;

import org.arios.game.container.ContainerEvent;
import org.arios.game.node.item.Item;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.PacketHeader;
import org.arios.net.packet.context.ContainerContext;

/**
 * Represents the outgoing container packet.
 * @author Emperor
 */
public final class ContainerPacket implements OutgoingPacket<ContainerContext> {

    @Override
    public void send(ContainerContext context) {
	IoBuffer buffer = null;
	if (context.isClear()) {
	    buffer = new IoBuffer(96);
	    buffer.putInt(context.getInterfaceId() << 16 | context.getChildId());
	} else {
	    boolean slotBased = context.getSlots() != null;
	    buffer = new IoBuffer(slotBased ? 159 : 104, PacketHeader.SHORT);
	    buffer.putInt(context.getInterfaceId() << 16 | context.getChildId()).putShort(context.getType());
	    if (slotBased) {
		for (int slot : context.getSlots()) {
		    buffer.putSmart(slot);
		    Item item = context.getItems()[slot];
		    if (item != null && !item.equals(ContainerEvent.NULL_ITEM)) {
			buffer.putShort(item.getId() + 1);
			int amount = item.getAmount();
			if (amount < 0 || amount > 254) {
			    buffer.put(255).putInt(amount);
			} else {
			    buffer.put(amount);
			}
		    } else {
			buffer.putShort(0);
		    }
		}
	    } else {
		buffer.putShort(context.getItems().length);
		for (Item item : context.getItems())
		    if (item != null) {
			buffer.putLEShortA(item.getId() + 1);
			int amount = item.getAmount();
			if (amount < 0 || amount > 254) {
			    buffer.put(255).putInt(amount);
			} else {
			    buffer.put(amount);
			}
		    } else {
			buffer.putLEShortA(0).put(0);
		    }
	    }
	}
	context.getPlayer().getSession().write(buffer);
    }

}