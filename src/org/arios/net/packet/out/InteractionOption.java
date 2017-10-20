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
        IoBuffer buffer = new IoBuffer(247, PacketHeader.BYTE);
        buffer.putString(context.getName()).putS(context.getIndex() + 1).putA(context.getIndex() == 0 ? 1 : 0);
        context.getPlayer().getSession().write(buffer);
    }

}