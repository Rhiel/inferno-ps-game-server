package org.arios.game.world.update;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.PriorityQueue;

import org.arios.cache.misc.buffer.ByteBufferUtils;
import org.arios.game.node.entity.impl.WalkingQueue;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.info.RenderInfo;
import org.arios.game.world.map.Direction;
import org.arios.game.world.map.Location;
import org.arios.game.world.map.RegionManager;
import org.arios.game.world.map.RunningDirection;
import org.arios.game.world.repository.Repository;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.game.world.update.flag.player.AppearanceFlag;
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
        IoBuffer stream = new IoBuffer(83, PacketHeader.SHORT);
        IoBuffer updateBlockData = new IoBuffer();
        RenderInfo info = player.getRenderInfo();
        processLocalPlayers(stream, updateBlockData, true, player);
        processLocalPlayers(stream, updateBlockData, false, player);
        processOutsidePlayers(stream, updateBlockData, true, player);
        processOutsidePlayers(stream, updateBlockData, false, player);
        stream.put(updateBlockData);
        player.getDetails().getSession().write(stream);
        info.totalRenderDataSentLength = 0;
        info.localPlayersIndexesCount = 0;
        info.outPlayersIndexesCount = 0;
        for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
            info.slotFlags[playerIndex] >>= 1;
            player = info.localPlayers[playerIndex];
            if (player == null)
                info.outPlayersIndexes[info.outPlayersIndexesCount++] = playerIndex;
            else
                info.localPlayersIndexes[info.localPlayersIndexesCount++] = playerIndex;
        }
    }

    private static void processLocalPlayers(IoBuffer stream, IoBuffer updateBlockData, boolean nsn0, Player player) {
        stream.setBitAccess();
        int skip = 0;//what exactly you trying to do/, this format is hard to read, i am used to eclipse but let me try
        RenderInfo info = player.getRenderInfo();
        for (int i = 0; i < info.localPlayersIndexesCount; i++) {
            int playerIndex = info.localPlayersIndexes[i];
            if (nsn0 ? (0x1 & info.slotFlags[playerIndex]) != 0 : (0x1 & info.slotFlags[playerIndex]) == 0)
                continue;
            if (skip > 0) {
                skip--;
                info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 2);
                continue;
            }//brb breakfast 10 mins
            Player p = info.localPlayers[playerIndex];
            if (needsRemove(p, player)) {
                stream.putBits(1, 1); // needs update
                stream.putBits(1, 0); // no masks update needeed
                stream.putBits(2, 0); // request remove
                info.regionHashes[playerIndex] = p.getRenderInfo().getLastLocation() == null
                        ? p.getLocation().toRegionPacked() : p.getRenderInfo().getLastLocation().toRegionPacked();
                int hash = p.getLocation().toRegionPacked();
                if (hash == info.regionHashes[playerIndex])// this one is hard to debug in, do u wanna see the client error? its buffer mismatch, but ok, naw ur good xD
                    stream.putBits(1, 0);
                else {
                    stream.putBits(1, 1);
                    updateRegionHash(stream, info.regionHashes[playerIndex], hash);//done
                    info.regionHashes[playerIndex] = hash;
                }
                info.localPlayers[playerIndex] = null;
            } else {
                if (p != null && p.getUpdateMasks().isUpdateRequired()) {
                    writeMasks(p, p, updateBlockData, true);
                }
                if (p.getProperties().isTeleporting()) {
                    stream.putBits(1, 1); // needs update
                    stream.putBits(1, (p.getUpdateMasks().isUpdateRequired() ? 1 : 0));
                    stream.putBits(2, 3);
                    int xOffset = p.getLocation().getX() - p.getRenderInfo().getLastLocation().getX();
                    int yOffset = p.getLocation().getY() - p.getRenderInfo().getLastLocation().getY();
                    int planeOffset = p.getLocation().getZ()
                            - p.getRenderInfo().getLastLocation().getZ();
                    if (Math.abs(p.getLocation().getX() - p.getRenderInfo().getLastLocation().getX()) <= 14 //14 for safe
                            && Math.abs(p.getLocation().getY() - p.getRenderInfo().getLastLocation().getY()) <= 14) { //14 for safe
                        stream.putBits(1, 0);
                        if (xOffset < 0) // viewport used to be 15 now 16
                            xOffset += 32;
                        if (yOffset < 0)
                            yOffset += 32;
                        stream.putBits(12, yOffset + (xOffset << 5)
                                + (planeOffset << 10));
                    } else {
                        stream.putBits(1, 1);
                        stream.putBits(30, (yOffset & 0x3fff)
                                + ((xOffset & 0x3fff) << 14)
                                + ((planeOffset & 0x3) << 28));
                    }
                } else if (p.getWalkingQueue().getWalkDir() != -1) {
                    boolean running;
                    int opcode;
                    WalkingQueue queue = p.getWalkingQueue();
                    if (queue.getRunDir() != -1) {
                        running = true;
                        opcode = queue.getRunDir();
                    } else {
                        running = false;
                        opcode = queue.getWalkDir();
                    }
                    stream.putBits(1, 1);
                    Direction d = Direction.values()[queue.getWalkDir()];
                    if ((d.getStepX() == 0 && d.getStepY() == 0)) {
                        stream.putBits(1, 1); //quick fix
                        stream.putBits(2, 0);
                        if (!p.getUpdateMasks().isUpdateRequired()) //hasnt been sent yet
                            writeMasks(p, p, updateBlockData, false);
                    } else {
                        stream.putBits(1, (p.getUpdateMasks().isUpdateRequired() ? 1 : 0));
                        stream.putBits(2, running ? 2 : 1);
                        stream.putBits(running ? 4 : 3, opcode);
                    }
                } else if (p.getUpdateMasks().isUpdateRequired()) {
                    stream.putBits(1, 1); // needs update
                    stream.putBits(1, 1);
                    stream.putBits(2, 0);
                } else { // skip
                    stream.putBits(1, 0); // no update needed
                    for (int i2 = i + 1; i2 < info.localPlayersIndexesCount; i2++) {
                        int p2Index = info.localPlayersIndexes[i2];
                        if (nsn0 ? (0x1 & info.slotFlags[p2Index]) != 0
                                : (0x1 & info.slotFlags[p2Index]) == 0)
                            continue;
                        Player p2 = info.localPlayers[p2Index];
                        if (needsRemove(p2, player)
                                || p2.getProperties().isTeleporting()
                                || p2.getWalkingQueue().getWalkDir() != -1
                                || (p2.getUpdateMasks().isUpdateRequired()
                                || (!p.getUpdateMasks().isUpdateRequired())))
                            break;
                        skip++;
                    }
                    skipPlayers(stream, skip);
                    info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 2);
                }

            }
        }
        stream.setByteAccess();
    }

    private static void writeMasks(Player writingFor, Player updatable, IoBuffer composer, boolean forceSync) {
        int maskdata = 0;
        PriorityQueue<UpdateFlag> flags = new PriorityQueue<UpdateFlag>(updatable.getUpdateMasks().flagQueue);
        for (UpdateFlag flag : flags) {// you have some order is wrong, it sends less data, this is how im doing it.
            System.out.println(flag.getClass().getName());
            maskdata |= flag.data();
        }//tried disabling the update plags? no
        if (maskdata > 0x100) {
            maskdata |= 0x4;
            composer.put((byte) (maskdata & 0xFF));
            composer.put((byte) (maskdata >> 8));
        } else {
            composer.put((byte) maskdata);
        }
        while (!flags.isEmpty()) {
            flags.poll().write(composer);//didnt get that far yet
        }
    }

    private static void updateRegionHash(IoBuffer stream, int lastRegionHash, int currentRegionHash) {
        int lastRegionX = lastRegionHash >> 8;
        int lastRegionY = 0xff & lastRegionHash;
        int lastPlane = lastRegionHash >> 16;
        int currentRegionX = currentRegionHash >> 8;
        int currentRegionY = 0xff & currentRegionHash;
        int currentPlane = currentRegionHash >> 16;
        int planeOffset = currentPlane - lastPlane;
        if (lastRegionX == currentRegionX && lastRegionY == currentRegionY) {
            stream.putBits(2, 1);
            stream.putBits(2, planeOffset);
        } else if (Math.abs(currentRegionX - lastRegionX) <= 1 && Math.abs(currentRegionY - lastRegionY) <= 1) {
            int opcode;
            int dx = currentRegionX - lastRegionX;
            int dy = currentRegionY - lastRegionY;
            if (dx == -1 && dy == -1)
                opcode = 0;
            else if (dx == 1 && dy == -1)
                opcode = 2;
            else if (dx == -1 && dy == 1)
                opcode = 5;
            else if (dx == 1 && dy == 1)
                opcode = 7;
            else if (dy == -1)
                opcode = 1;
            else if (dx == -1)
                opcode = 3;
            else if (dx == 1)
                opcode = 4;
            else
                opcode = 6;
            stream.putBits(2, 2);
            stream.putBits(5, (planeOffset << 3) + (opcode & 0x7));
        } else {
            int xOffset = currentRegionX - lastRegionX;
            int yOffset = currentRegionY - lastRegionY;
            stream.putBits(2, 3);
            stream.putBits(18, (yOffset & 0xff) + ((xOffset & 0xff) << 8) + (planeOffset << 16));
        }
    }

    private static void processOutsidePlayers(IoBuffer stream, IoBuffer updateBlockData, boolean nsn2, Player player) {
        stream.setBitAccess();
        int skip = 0;
        RenderInfo info = player.getRenderInfo();
        info.localAddedPlayers = 0;
        for (int i = 0; i < info.outPlayersIndexesCount; i++) {
            int playerIndex = info.outPlayersIndexes[i];
            if (nsn2 ? (0x1 & info.slotFlags[playerIndex]) == 0
                    : (0x1 & info.slotFlags[playerIndex]) != 0)
                continue;
            if (skip > 0) {
                skip--;
                info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 2);
                continue;
            }
            Player p = Repository.getPlayers().get(playerIndex);
            if (needsAdd(p, player)) {//why da hell it read as two then sec
                System.out.println("Adding player index: " + p.getIndex() + " to client name: " + player.getName(true) + ", index: " + player.getIndex());
                stream.putBits(1, 1);
                stream.putBits(2, 0); // request add
                int hash = p.getLocation().toRegionPacked();
                if (hash == info.regionHashes[playerIndex])
                    stream.putBits(1, 0);//it looks identical..i know ;/
                else {
                    stream.putBits(1, 1);
                    updateRegionHash(stream, info.regionHashes[playerIndex], hash);
                    info.regionHashes[playerIndex] = hash;
                }//its 6? it changed in osrs, oh, ok i think you are sending the same player index
                stream.putBits(13, p.getLocation().getXInRegion());
                stream.putBits(13, p.getLocation().getYInRegion());
                //   if (p != null && p.getUpdateMasks().isUpdateRequired()) {
                //      writeMasks(p, p, updateBlockData, true);
                //  }could u try?
                // stream.putBits(1, 1); this part doesn't exist on the clie
                stream.setByteAccess();
                stream.putString("Test!");
                stream.setBitAccess();
                info.localAddedPlayers++;
                info.localPlayers[p.getIndex()] = p;
                info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 2);
            } else {
                int hash = p == null ? info.regionHashes[playerIndex] : p.getLocation().toRegionPacked();
                if (p != null && hash != info.regionHashes[playerIndex]) {
                    stream.putBits(1, 1);
                    updateRegionHash(stream, info.regionHashes[playerIndex], hash);
                    info.regionHashes[playerIndex] = hash;
                } else {
                    stream.putBits(1, 0); // no update needed
                    for (int i2 = i + 1; i2 < info.outPlayersIndexesCount; i2++) {
                        int p2Index = info.outPlayersIndexes[i2];
                        if (nsn2 ? (0x1 & info.slotFlags[p2Index]) == 0
                                : (0x1 & info.slotFlags[p2Index]) != 0)
                            continue;
                        Player p2 = Repository.getPlayers().get(p2Index);
                        if (needsAdd(p2, player)
                                || (p2 != null && p2.getLocation().toRegionPacked() != info.regionHashes[p2Index]))
                            break;
                        skip++;
                    }
                    skipPlayers(stream, skip);
                    info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 2);
                }//try
            }
        }
        stream.setByteAccess();
    }

    private static void skipPlayers(IoBuffer stream, int amount) {
        stream.putBits(2, amount == 0 ? 0 : amount > 255 ? 3 : (amount > 31 ? 2 : 1));
        if (amount > 0)
            stream.putBits(amount > 255 ? 11 : (amount > 31 ? 8 : 5), amount);
    }

    private static boolean needsRemove(Player p, Player player) {
        return (!p.isPlaying() || !player.getLocation().withinDistance(p.getLocation(), 14));
    }

    private static boolean needsAdd(Player p, Player player) {
        return p != null && p.isPlaying() && player.getLocation().withinDistance(p.getLocation(), 14) && player.getRenderInfo().localAddedPlayers < MAX_PLAYER_ADD;
    }

    private static int MAX_PLAYER_ADD = 15;

    public static final byte[] DIRECTION_DELTA_X = new byte[]{-1, 0, 1, -1,
            1, -1, 0, 1};
    public static final byte[] DIRECTION_DELTA_Y = new byte[]{1, 1, 1, 0, 0,
            -1, -1, -1};

    public static int getPlayerWalkingDirection(int dx, int dy) {
        if (dx == -1 && dy == -1) {
            return 0;
        }
        if (dx == 0 && dy == -1) {
            return 1;
        }
        if (dx == 1 && dy == -1) {
            return 2;
        }
        if (dx == -1 && dy == 0) {
            return 3;
        }
        if (dx == 1 && dy == 0) {
            return 4;
        }
        if (dx == -1 && dy == 1) {
            return 5;
        }
        if (dx == 0 && dy == 1) {
            return 6;
        }
        if (dx == 1 && dy == 1) {
            return 7;
        }
        return -1;
    }

}