package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.DisplayModelContext;

/**
 * Represents the outgoing packet for the displaying of a node model on an
 * interface.
 *
 * @author Emperor
 */
public final class DisplayModel implements OutgoingPacket<DisplayModelContext> {

    @Override
    public void send(DisplayModelContext context) {
        IoBuffer buffer;
        switch (context.getType()) {
            case PLAYER:
                buffer = new IoBuffer(126);
                buffer.putIntB(context.getInterfaceId() << 16 | context.getChildId());
                break;
            case NPC:
                buffer = new IoBuffer(124);
                buffer.putLEShortA(context.getNodeId());
                buffer.putIntA((context.getInterfaceId() << 16) | context.getChildId());
                break;
            case ITEM:
                int value = context.getAmount() > 0 ? context.getAmount() : context.getZoom();
                buffer = new IoBuffer(20);
                buffer.putLEInt(context.getInterfaceId() << 16 | context.getChildId());
                buffer.putLEShort(context.getNodeId());
                buffer.putLEInt(value);
                break;
            case MODEL:
                buffer = new IoBuffer(67);
                buffer.putLEShortA(context.getNodeId());
                buffer.putLEInt(context.getInterfaceId() << 16 | context.getChildId());
                break;
            default:
                return;
        }
        context.getPlayer().getSession().write(buffer);
    }

}