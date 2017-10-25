package org.arios.net.packet.out;

import org.arios.game.node.entity.player.Player;
import org.arios.game.node.object.GameObject;
import org.arios.game.world.map.Location;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.BuildObjectContext;

/**
 * The clear game object outgoing packet.
 *
 * @author Emperor
 */
public final class ClearObject implements OutgoingPacket<BuildObjectContext> {

    /**
     * Writes the packet.
     *
     * @param buffer  The buffer.
     * @param objects The objects.
     */
    public static IoBuffer write(IoBuffer buffer, GameObject object) {
        Location l = object.getLocation();
        buffer.put(19);
        buffer.putA((object.getType() << 2) + (object.getRotation() & 3));
        buffer.putS((l.getChunkOffsetX() << 4) | (l.getChunkOffsetY() & 0x7));
        return buffer;
    }

    @Override
    public void send(BuildObjectContext context) {
        Player player = context.getPlayer();
        GameObject o = context.getGameObject();
        IoBuffer buffer = UpdateAreaPosition.getBuffer(player, o.getLocation().getChunkBase());
        Location l = o.getLocation();
        IoBuffer objectBuf = new IoBuffer(19).putA((o.getType() << 2) + (o.getRotation() & 3))
                .putS((l.getChunkOffsetX() << 4) | (l.getChunkOffsetY() & 0x7));
        player.getSession().write(buffer);
        player.getSession().write(objectBuf);
    }

}