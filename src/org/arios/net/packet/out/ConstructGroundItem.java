package org.arios.net.packet.out;

import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.game.world.map.Location;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.BuildItemContext;

/**
 * Represents the outgoing packet of constructing a ground item.
 *
 * @author Emperor
 */
public final class ConstructGroundItem implements OutgoingPacket<BuildItemContext> {

    /**
     * Writes the packet.
     *
     * @param buffer The buffer.
     * @param item   The item.
     */
    public static IoBuffer write(IoBuffer buffer, Item item) {
        Location l = item.getLocation();
        buffer.put(187);
        buffer.putShortA(item.getId());
        buffer.putShort(item.getAmount());
        buffer.putA((l.getChunkOffsetX() << 4) | (l.getChunkOffsetY() & 0x7));
        return buffer;
    }

    @Override
    public void send(BuildItemContext context) {
        Player player = context.getPlayer();
        Item item = context.getItem();
        IoBuffer buffer = UpdateAreaPosition.getBuffer(player, item.getLocation().getChunkBase());
        Location l = item.getLocation();
        IoBuffer itemBuf = new IoBuffer(187);
        itemBuf.putShortA(item.getId());
        itemBuf.putShort(item.getAmount());
        itemBuf.putA((l.getChunkOffsetX() << 4) | (l.getChunkOffsetY() & 0x7));
        player.getDetails().getSession().write(buffer);
        player.getDetails().getSession().write(itemBuf);
    }
}
