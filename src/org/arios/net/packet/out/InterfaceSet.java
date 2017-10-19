package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.InterfaceSetContext;

/**
 * Created by Chris on 7/23/2017.
 */
public class InterfaceSet implements OutgoingPacket<InterfaceSetContext> {

    @Override
    public void send(InterfaceSetContext context) {
        IoBuffer buffer = new IoBuffer(16);
        buffer.putInt((context.getFromRoot() << 16 | context.getFromChild()));
        buffer.putIntB((context.getToRoot() << 16 | context.getToChild()));
        context.getPlayer().getSession().write(buffer);
    }
}