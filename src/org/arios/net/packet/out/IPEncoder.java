package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.IPContext;

public class IPEncoder implements OutgoingPacket<IPContext> {
    @Override
    public void send(IPContext context) {
        IoBuffer buffer = new IoBuffer(193);
        buffer.putLEInt(context.getHash());
        context.getPlayer().getSession().write(buffer);
    }
}