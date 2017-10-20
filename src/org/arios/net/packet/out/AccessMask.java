package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.AccessMaskContext;

/**
 * The access mask outgoing packet.
 *
 * @author Empero
 */
public class AccessMask implements OutgoingPacket<AccessMaskContext> {

    @Override
    public void send(AccessMaskContext context) {
        IoBuffer buffer = new IoBuffer(72);
        buffer.putShortA(context.getId());
        buffer.putIntB(context.getOffset());
        buffer.putInt(context.getInterfaceId() << 16 | context.getChildId());
        buffer.putShortA(context.getLength());
        context.getPlayer().getSession().write(buffer);
    }
}
