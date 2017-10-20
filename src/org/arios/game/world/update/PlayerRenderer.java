package org.arios.game.world.update;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.info.RenderInfo;
import org.arios.game.world.map.Location;
import org.arios.game.world.map.RegionManager;
import org.arios.game.world.repository.Repository;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.PacketHeader;

/**
 * Handles the player rendering.
 *
 * @author Emperor
 */
public final class PlayerRenderer {

    /**
     * Handles the player rendering for a player.
     *
     * @param player The player.
     */
    public static void render(Player player) {
        if (player.getPlayerFlags().isUpdateSceneGraph()) {
            player.updateSceneGraph(false);
        }
        IoBuffer buffer = new IoBuffer(83, PacketHeader.SHORT);
        IoBuffer flags = new IoBuffer();
        RenderInfo info = player.getRenderInfo();
        int skipCount = -1;
        buffer.setBitAccess();
        for (int i = 0; i < info.localsCount; i++) {
            int index = info.locals[i];
            LocalUpdateStage stage = LocalUpdateStage.getStage(player, Repository.getPlayers().get(index));
            if (stage == null) {
                skipCount++;
            } else {
                putSkip(skipCount, buffer);
                skipCount = -1;
                updateLocalPlayer(player, Repository.getPlayers().get(index), buffer, stage, flags, index);
            }
        }
        putSkip(skipCount, buffer);
        skipCount = -1;
        buffer.setByteAccess();
        buffer.setBitAccess();
        for (int i = 0; i < info.globalsCount; i++) {
            int index = info.globals[i];
            GlobalUpdateStage stage = GlobalUpdateStage.getStage(player, Repository.getPlayers().get(index));
            if (stage == null) {
                skipCount++;
            } else {
                putSkip(skipCount, buffer);
                skipCount = -1;
                updateGlobalPlayer(player, Repository.getPlayers().get(index), buffer, stage, flags);
            }
        }
        putSkip(skipCount, buffer);
        skipCount = -1;
        buffer.setByteAccess();
        buffer.put(flags);
        player.getDetails().getSession().write(buffer);
    }

    private static void updateLocalPlayer(Player player, Player p,
                                          IoBuffer buffer, LocalUpdateStage stage,
                                          IoBuffer flagBased, int index) {
        buffer.putBits(1, 1);
        buffer.putBits(1, stage.ordinal() == 0 ? 0 : (p.getUpdateMasks().isUpdateRequired() ? 1 : 0));
        buffer.putBits(2, stage.ordinal() % 4);
        switch (stage) {
            case REMOVE_PLAYER:
                if (p != null) {
                    if (p.getProperties().isTeleporting()) {
                        updateGlobalPlayer(player, p, buffer, GlobalUpdateStage.TELEPORTED, flagBased);
                    } else if (p.getLocation().getZ() != p.getRenderInfo().getLastLocation().getZ()) {
                        updateGlobalPlayer(player, p, buffer, GlobalUpdateStage.HEIGHT_UPDATED, flagBased);
                    } else {
                        buffer.putBits(1, 0);
                    }
                } else {
                    buffer.putBits(1, 0);
                }
                player.getRenderInfo().getLocalPlayers().remove(p);
                break;
            case WALKING:
                buffer.putBits(3, p.getWalkingQueue().getWalkDir());
                if(player.getInterfaceManager().getOverlay() != null && player.getInterfaceManager().getOverlay().getId() == 595)
                    player.getPacketDispatch().sendCS2Script(1749, new Object[]{player.getLocation().toPositionPacked()});
                break;
            case RUNNING:
                buffer.putBits(4, p.getWalkingQueue().getRunDir());
                if(player.getInterfaceManager().getOverlay() != null && player.getInterfaceManager().getOverlay().getId() == 595)
                    player.getPacketDispatch().sendCS2Script(1749, new Object[]{player.getLocation().toPositionPacked()});
                break;
            case TELEPORTED:
                Location delta = Location.getDelta(p.getRenderInfo().getLastLocation(), p.getLocation());
                int deltaX = delta.getX() < 0 ? -delta.getX() : delta.getX();
                int deltaY = delta.getY() < 0 ? -delta.getY() : delta.getY();
                if (deltaX <= 15 && deltaY <= 15) {
                    buffer.putBits(1, 0);
                    int deltaZ = delta.getZ() < 0 ? -delta.getZ() : delta.getZ();
                    deltaX = delta.getX() < 0 ? delta.getX() + 32 : delta.getX();
                    deltaY = delta.getY() < 0 ? delta.getY() + 32 : delta.getY();
                    deltaZ = delta.getZ();
                    buffer.putBits(12, (deltaY & 0x1f) | ((deltaX & 0x1f) << 5) | ((deltaZ & 0x3) << 10));
                } else {
                    buffer.putBits(1, 1);
                    buffer.putBits(30, (delta.getY() & 0x3fff) | ((delta.getX() & 0x3fff) << 14) | ((delta.getZ() & 0x3) << 28));
                }
                break;
            case NO_UPDATE:
                break;
            default:
                break;
        }
        if (p != null && stage != LocalUpdateStage.REMOVE_PLAYER
                && p.getUpdateMasks().isUpdateRequired()) {
            writeMasks(player, p, flagBased, false);
        }
    }

    private static void updateGlobalPlayer(Player player, Player p, IoBuffer buffer, GlobalUpdateStage stage, IoBuffer flagBased) {
        buffer.putBits(1, 1);
        buffer.putBits(2, stage.ordinal());
        switch (stage) {
            case ADD_PLAYER:
                if (p.getRenderInfo().getLastLocation() != null && p.getLocation().getZ() != p.getRenderInfo().getLastLocation().getZ()) {				updateGlobalPlayer(player, p, buffer, GlobalUpdateStage.HEIGHT_UPDATED, flagBased);
                } else {
                    updateGlobalPlayer(player, p, buffer, GlobalUpdateStage.TELEPORTED, flagBased);
                    //buffer.putBits(1, 0);
                }
                buffer.putBits(13, p.getLocation().getX() - (p.getLocation().getRegionX() << 6)); //6
                buffer.putBits(13, p.getLocation().getY() - (p.getLocation().getRegionY() << 6)); //6
                buffer.putBits(1, 1);
                player.getRenderInfo().getLocalPlayers().add(p);
                writeMasks(player, p, flagBased, true);
                break;
            case HEIGHT_UPDATED:
                int z = p.getLocation().getZ() - p.getRenderInfo().getLastLocation().getZ();
                buffer.putBits(2, z);
                break;
            case TELEPORTED:
                buffer.putBits(18, (p.getLocation().getZ() << 16)
                        | (((p.getLocation().getRegionX() >> 3) & 0xFF) << 8)
                        | ((p.getLocation().getRegionY() >> 3) & 0xFF));
                break;
            case MAP_REGION_DIRECTION:
                break;
            default:
                break;
        }
    }

    private static void putSkip(int skipCount, IoBuffer packet) {
        if (skipCount > -1) {
            packet.putBits(1, 0);
            if (skipCount == 0) {
                packet.putBits(2, 0);
            } else if (skipCount < 32) {
                packet.putBits(2, 1);
                packet.putBits(5, skipCount);
            } else if (skipCount < 256) {
                packet.putBits(2, 2);
                packet.putBits(8, skipCount);
            } else if (skipCount < 2048) {
                packet.putBits(2, 3);
                packet.putBits(11, skipCount);
            }
        }
    }

    private static void writeMasks(Player writingFor, Player updatable, IoBuffer composer, boolean forceSync) {
        int maskdata = 0;
        PriorityQueue<UpdateFlag> flags = new PriorityQueue<UpdateFlag>(updatable.getUpdateMasks().flagQueue);
        for (UpdateFlag flag : flags) {
            System.out.println(flag.getClass().getName());
            maskdata |= flag.data();
        }
        /*if (forceSync && (maskdata & 0x40) == 0) {
            maskdata |= 0x40;
            flags.add(new AppearanceFlag(updatable));
        }*/
        if (maskdata > 0x100) {
            maskdata |= 0x2;
            composer.put((byte) (maskdata & 0xFF));
            composer.put((byte) (maskdata >> 8));
        } else {
            composer.put((byte) maskdata);
        }
        while (!flags.isEmpty()) {
            flags.poll().write(composer);
        }
    }
}