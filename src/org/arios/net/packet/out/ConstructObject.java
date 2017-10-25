package org.arios.net.packet.out;

import org.arios.game.node.entity.player.Player;
import org.arios.game.node.object.GameObject;
import org.arios.game.world.map.Location;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.BuildObjectContext;

/**
 * The construct game object packet.
 *
 * @author Emperor
 */
public final class ConstructObject implements OutgoingPacket<BuildObjectContext> {

    /**
     * Writes the packet.
     *
     * @param buffer  The buffer.
     * @param objects The objects.
     */
    public static IoBuffer write(IoBuffer buffer, GameObject object) {
        Location l = object.getLocation();
        buffer.put(178)
                .putC((l.getChunkOffsetX() << 4) | (l.getChunkOffsetY() & 0x7))
                .putLEShortA(object.getId())
                .putS((object.getType() << 2) | (object.getRotation() & 0x3));
        return buffer;
    }

    @Override
    public void send(BuildObjectContext context) {
        Player player = context.getPlayer();
        GameObject o = context.getGameObject();
        IoBuffer buffer = UpdateAreaPosition.getBuffer(player, o.getLocation().getChunkBase());
        Location l = o.getLocation();
        IoBuffer oBuf = new IoBuffer(178).putC((l.getChunkOffsetX() << 4) | (l.getChunkOffsetY() & 0x7))
                .putLEShortA(o.getId())
                .putS((o.getType() << 2) | (o.getRotation() & 0x3));
        player.getSession().write(buffer);
        player.getSession().write(oBuf);
    }

}