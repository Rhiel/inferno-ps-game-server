package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.InterfaceContext;

/**
 * Represents the outgoing packet used for closing an interface.
 *
 * @author Emperor
 */
public final class CloseInterface implements OutgoingPacket<InterfaceContext> {

    @Override
    public void send(InterfaceContext context) {
        IoBuffer buffer = new IoBuffer(141);
        int windowPane = context.getWindowId() == 162 ? 162 : context.getPlayer().getInterfaceManager().getWindowsPane();
        buffer.putInt(windowPane << 16 | context.getComponentId());
        context.getPlayer().getSession().write(buffer);
    }

}