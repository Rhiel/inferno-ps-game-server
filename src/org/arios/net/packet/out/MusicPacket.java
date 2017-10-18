package org.arios.net.packet.out;

import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.MusicContext;

/**
 * Outgoing Music packet
 * @author SonicForce41
 */
public class MusicPacket implements OutgoingPacket<MusicContext> {

    @Override
    public void send(MusicContext context) {
	IoBuffer buffer = null;
	if (context.isSecondary()) {
	    buffer = new IoBuffer(132);
	    buffer.putTri(0);
	    buffer.putShort(context.getMusicId());
	} else {
	    buffer = new IoBuffer(172);
	    buffer.putShortA(context.getMusicId());
	}
	context.getPlayer().getDetails().getSession().write(buffer);
    }

}
