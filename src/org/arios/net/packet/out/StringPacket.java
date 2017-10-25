package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.PacketHeader;
import org.arios.net.packet.context.StringContext;

/**
 * The outgoing set component string packet.
 *
 * @author Emperor
 */
public class StringPacket implements OutgoingPacket<StringContext> {

    @Override
    public void send(StringContext context) {
        System.out.println("interface: " + context.getInterfaceId());
        IoBuffer buffer = new IoBuffer(42, PacketHeader.SHORT);
        buffer.putInt((context.getInterfaceId() << 16) | context.getLineId());
        buffer.putString(context.getString());
        context.getPlayer().getDetails().getSession().write(buffer);
    }
}
