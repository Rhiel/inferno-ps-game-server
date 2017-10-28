package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.PacketHeader;
import org.arios.net.packet.context.InteractionOptionContext;

/**
 * Handles the interaction option changed outgoing packet.
 *
 * @author Emperor
 */
public final class InteractionOption implements OutgoingPacket<InteractionOptionContext> {

    @Override
    public void send(InteractionOptionContext context) {
        IoBuffer buffer = new IoBuffer(117, PacketHeader.BYTE);
        buffer.putS(context.getIndex() + 1).putC(context.getIndex() == 0 ? 1 : 0).putString(context.getName());
        context.getPlayer().getSession().write(buffer);
    }

}