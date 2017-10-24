package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.InterfaceContext;

/**
 * The interface outgoing packet.
 *
 * @author Emperor
 */
public final class Interface implements OutgoingPacket<InterfaceContext> {

    @Override
    public void send(InterfaceContext context) {
        IoBuffer buffer = new IoBuffer(127);
        int windowPane = context.getWindowId() == 162 ? 162 : context.getPlayer().getInterfaceManager().getWindowsPane();
        buffer.putS(context.isWalkable() ? 1 : 0);
        buffer.putLEShort(context.getInterfaceId());
        buffer.putIntB(windowPane << 16 | context.getComponentId());
        context.getPlayer().getDetails().getSession().write(buffer);
    }

}