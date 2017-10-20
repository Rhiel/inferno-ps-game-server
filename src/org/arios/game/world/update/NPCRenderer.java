package org.arios.game.world.update;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;

import org.arios.game.node.entity.npc.NPC;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.info.RenderInfo;
import org.arios.game.world.GameWorld;
import org.arios.game.world.map.RegionManager;
import org.arios.game.world.repository.Repository;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.PacketHeader;

/**
 * The NPC renderer.
 *
 * @author Emperor
 */
public final class NPCRenderer {

    /**
     * Handles the NPC rendering for a player.
     *
     * @param player The player.
     */
    public static void render(Player player) {
        IoBuffer buffer = new IoBuffer(84, PacketHeader.SHORT);
        IoBuffer maskBuffer = new IoBuffer(-1, PacketHeader.NORMAL, ByteBuffer.allocate(1 << 16));
        RenderInfo info = player.getRenderInfo();
        List<NPC> localNpcs = info.getLocalNpcs();
        buffer.setBitAccess();
        buffer.putBits(8, localNpcs.size());
        for (Iterator<NPC> it = localNpcs.iterator(); it.hasNext(); ) {
            NPC npc = it.next();
            boolean withinDistance = player.getLocation().withinDistance(npc.getLocation());
            if (npc == null || npc.destroyed() || npc.isHidden(player) || !withinDistance || npc.getProperties().isTeleporting()) {
                buffer.putBits(1, 1).putBits(2, 3);
                it.remove();
                if (!withinDistance && npc.getAggressiveHandler() != null) {
                    npc.getAggressiveHandler().removeTolerance(player.getIndex());
                }
            } else {
                updateNPCMovement(npc, buffer);
                if (npc.getUpdateMasks().isUpdateRequired()) {
                    updateNPC(maskBuffer, npc);
                }
            }
        }
        if (localNpcs.size() < 255/* && player.getViewDistance() > 0*/) {
            int added = 0;
            for (NPC npc : Repository.getNpcs()) {
                if (added >= 5 || localNpcs.size() >= 255 || player.getProperties().isTeleporting()) {
                    break;
                }
                if (npc == null || npc.destroyed() || npc.isHidden() || !player.getLocation().withinDistance(npc.getLocation())) {
                    continue;
                }
                if (localNpcs.contains(npc)) {
                    continue;
                }
                addNpc(player, npc, buffer);
                if (npc != null && npc.getUpdateMasks() != null && npc.getUpdateMasks().isUpdateRequired()) {
                    updateNPC(maskBuffer, npc);
                }
                added++;
            }
        }
        ByteBuffer masks = maskBuffer.toByteBuffer();
        masks.flip();
        if (masks.hasRemaining()) {
            buffer.putBits(15, 32767);
            buffer.setByteAccess();
            buffer.put(masks);
        } else {
            buffer.setByteAccess();
        }
        player.getSession().write(buffer);
    }

    /**
     * Adds an NPC.
     *
     * @param player
     *            The player.
     * @param npc
     *            The npc.
     * @param buf
     *            The outgoing packet.
     */
    private static void addNpc(Player player, NPC npc, IoBuffer buf) {
        int x = npc.getLocation().getX() - player.getLocation().getX();
        int y = npc.getLocation().getY() - player.getLocation().getY();
        if (x < 0) {
            x += 32;
        }
        if (y < 0) {
            y += 32;
        }
        buf.putBits(15, npc.getIndex());
        buf.putBits(5, y);
        buf.putBits(14, npc.getId());
        buf.putBits(3, npc.getDirection().toInteger());
        buf.putBits(5, x);
        buf.putBits(1, 0); // 1
        buf.putBits(1, npc.getUpdateMasks().isUpdateRequired() ? 1 : 0);
        player.getRenderInfo().getLocalNpcs().add(npc);
    }

    /**
     * Updates an npcs movement.
     *
     * @param npc
     *            The npc.
     * @param buf
     *            The buffer.
     */
    private static void updateNPCMovement(NPC npc, IoBuffer buf) {
        if (npc.getWalkingQueue().getRunDir() == -1) {
            if (npc.getWalkingQueue().getWalkDir() == -1) {
                if (npc.getUpdateMasks().isUpdateRequired()) {
                    buf.putBits(1, 1);
                    buf.putBits(2, 0);
                } else {
                    buf.putBits(1, 0);
                }
            } else {
                buf.putBits(1, 1);
                buf.putBits(2, 1);
                buf.putBits(3, npc.getWalkingQueue().getWalkDir());
                buf.putBits(1, npc.getUpdateMasks().isUpdateRequired() ? 1 : 0);
            }
        } else {
            buf.putBits(1, 1);
            buf.putBits(2, 2);
            buf.putBits(1, 1);
            buf.putBits(3, npc.getWalkingQueue().getWalkDir());
            buf.putBits(3, npc.getWalkingQueue().getRunDir());
            buf.putBits(1, npc.getUpdateMasks().isUpdateRequired() ? 1 : 0);
        }
    }

    /**
     * Writes the NPC flag-based updating.
     *
     * @param packet
     *            The packet to write on.
     * @param npc
     *            The npc.
     */
    private static void updateNPC(IoBuffer packet, NPC npc) {
        int maskdata = 0;
        PriorityQueue<UpdateFlag> flags = new PriorityQueue<UpdateFlag>(npc.getUpdateMasks().flagQueue);
        for (UpdateFlag flag : flags) {
            maskdata |= flag.data();
        }
        packet.put((byte) maskdata);
        while (!flags.isEmpty()) {
            flags.poll().write(packet);
        }
    }

}