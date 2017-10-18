package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.InterfaceConfigContext;

/**
 * The outgoing interface configuration packet.
 * @author Emperor
 */
public class InterfaceConfig implements OutgoingPacket<InterfaceConfigContext> {

    @Override
    public void send(InterfaceConfigContext context) {
	IoBuffer buffer = new IoBuffer(19);
	buffer.putIntB(context.getInterfaceId() << 16 | context.getChildId());
	buffer.put(context.isHidden() ? 1 : 0);
	context.getPlayer().getSession().write(buffer);
    }
}
