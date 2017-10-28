package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.PacketHeader;
import org.arios.net.packet.context.RunScriptContext;

/**
 * The run script outgoing packet.
 *
 * @author Emperor
 */
public class RunScriptPacket implements OutgoingPacket<RunScriptContext> {

    @Override
    public void send(RunScriptContext context) {
        String string = context.getString();
        Object[] objects = context.getObjects();
        IoBuffer buffer = new IoBuffer(144, PacketHeader.SHORT);
        buffer.putString(string);
        if(objects != null) {
            for (int index = objects.length - 1; index >= 0; index--) {
                if (string.charAt(index) == 's') {
                    buffer.putString((String) objects[index]);
                } else {
                    buffer.putInt((Integer) objects[index]);
                }
            }
        }
        buffer.putInt(context.getId());
        context.getPlayer().getDetails().getSession().write(buffer);
    }
}
