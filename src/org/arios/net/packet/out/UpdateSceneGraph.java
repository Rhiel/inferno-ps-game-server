package org.arios.net.packet.out;

import org.arios.cache.xtea.XteaManager;
import org.arios.game.node.entity.player.Player;
import org.arios.game.world.map.RegionManager;
import org.arios.game.world.update.PlayerRenderer;
import org.arios.game.world.update.flag.player.AppearanceFlag;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.PacketHeader;
import org.arios.net.packet.context.SceneGraphContext;

import java.util.List;

/**
 * The update scene graph outgoing packet.
 *
 * @author Emperor
 */
public final class UpdateSceneGraph implements OutgoingPacket<SceneGraphContext> {

    @Override
    public void send(SceneGraphContext context) {
        IoBuffer buffer = new IoBuffer(150, PacketHeader.SHORT);
        IoBuffer xteaBuffer = new IoBuffer();
        Player player = context.getPlayer();
        player.getPlayerFlags().setLastSceneGraph(player.getLocation());
        if (context.isLogin()) {
            player.getRenderInfo().initialize(buffer);
        }
        int count = 0;
        final int chunkX = player.getLocation().getRegionX();
        final int chunkY = player.getLocation().getRegionY();
        boolean forceSend = false;
        if ((48 == chunkX / 8 || chunkX / 8 == 49) && chunkY / 8 == 48) {
            forceSend = true;
        }

        if (48 == chunkX / 8 && chunkY / 8 == 148) {
            forceSend = true;
        }
        for (int xCalc = (chunkX - 6) / 8; xCalc <= ((chunkX + 6) / 8); xCalc++) {
            for (int yCalc = (chunkY - 6) / 8; yCalc <= ((chunkY + 6) / 8); yCalc++) {
                if (!forceSend || yCalc != 49 && 149 != yCalc && 147 != yCalc && xCalc != 50 && (xCalc != 49 || yCalc != 47)) {
                    count++;
                    int region = yCalc + (xCalc << 8);
                    int[] keys = XteaManager.getKey(region);
                    if (keys == null) {
                        keys = new int[4];
                    }
                    xteaBuffer.put(keys);
                }
            }
        }
        buffer
                .putLEShortA(chunkX)
                .putShortA(chunkY)
                .putShort(count)
                .put(xteaBuffer);
        if (!context.isLogin()) {
            player.getDetails().getSession().write(buffer, PlayerRenderer.render(player));
        } else
            player.getDetails().getSession().write(buffer);
    }

}