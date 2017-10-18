package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.AnimateInterfaceContext;

/**
 * The animate interface outgoing packet.
 * @author Emperor
 */
public class AnimateInterface implements OutgoingPacket<AnimateInterfaceContext> {

    @Override
    public void send(AnimateInterfaceContext context) {
	IoBuffer buffer = new IoBuffer(225);
	buffer.putInt((context.getInterfaceId() << 16) + context.getChildId());
	buffer.putLEShortA(context.getAnimationId());
	context.getPlayer().getDetails().getSession().write(buffer);
    }
}