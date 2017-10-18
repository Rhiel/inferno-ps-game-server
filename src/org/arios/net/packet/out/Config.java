package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.ConfigContext;

/**
 * The config outgoing packet.
 * @author Emperor
 */
public class Config implements OutgoingPacket<ConfigContext> {

    @Override
    public void send(ConfigContext context) {
	IoBuffer buffer;
	if (context.getValue() < Byte.MIN_VALUE || context.getValue() > Byte.MAX_VALUE) {
	    buffer = new IoBuffer(250);
	    buffer.putLEInt(context.getValue());
	    buffer.putLEShortA(context.getId());
	} else {
	    buffer = new IoBuffer(246);
	    buffer.putC(context.getValue());
	    buffer.putLEShortA(context.getId());
	}
	context.getPlayer().getDetails().getSession().write(buffer);
    }
}