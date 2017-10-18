package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.ChildPositionContext;

/**
 * Handles the "reposition interface child" outgoing packet.
 * @author Emperor
 */
public final class RepositionChild implements OutgoingPacket<ChildPositionContext> {

    @Override
    public void send(ChildPositionContext context) {
	context.getPlayer().getSession().write(new IoBuffer(64).putShort(context.getPosition().y).putInt(context.getInterfaceId() << 16 | context.getChildId()).putShort(context.getPosition().x));
    }

}