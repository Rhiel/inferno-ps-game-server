package org.arios.net.packet.out;

import org.arios.game.node.entity.player.Player;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.PacketHeader;
import org.arios.net.packet.context.SceneGraphContext;

/**
 * The update scene graph outgoing packet.
 * @author Emperor
 */
public final class UpdateSceneGraph implements OutgoingPacket<SceneGraphContext> {

    /**
     * The XTEA keys.
     */
    public static final int[] KEYS = new int[] { 14881828, -6662814, 58238456, 146761213 };

    @Override
    public void send(SceneGraphContext context) {
	IoBuffer buffer = new IoBuffer(44, PacketHeader.SHORT);
	Player player = context.getPlayer();
	player.getPlayerFlags().setLastSceneGraph(player.getLocation());
	for (int regionX = (player.getLocation().getRegionX() - 6) / 8; regionX <= ((player.getLocation().getRegionX() + 6) / 8); regionX++) {
	    for (int regionY = (player.getLocation().getRegionY() - 6) / 8; regionY <= ((player.getLocation().getRegionY() + 6) / 8); regionY++) {
		for (int i = 0; i < KEYS.length; i++) {
		    buffer.putIntA(KEYS[i]);
		}
	    }
	}
	buffer.putShort(player.getLocation().getRegionY()).putLEShortA(player.getLocation().getRegionX()).putLEShortA(player.getLocation().getSceneX()).putC(player.getLocation().getZ()).putShortA(player.getLocation().getSceneY());
	player.getDetails().getSession().write(buffer);
    }

}