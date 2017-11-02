package org.arios.game.world.update;

import java.nio.ByteBuffer;
import java.util.*;

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

    public static void sendUpdate(Player player) {
        player.getDetails().getSession().write(render(player));
    }

    /**
     * Handles the player rendering for a player.
     *
     * @param player The player.
     */
    public static IoBuffer render(Player player) {
        RenderInfo viewport = player.getRenderInfo();

        if (!viewport.initialized()) {
            return null;
        }

        IoBuffer stream = new IoBuffer(83, PacketHeader.SHORT);
        IoBuffer updateBlockData = new IoBuffer();

        List<Player> playersToUpdate = new ArrayList<>();

        int skipCount = 0;

        stream.setBitAccess();

        for (int index = 0; index < viewport.getLocalPlayersIndexesCount(); index++) {
            int playerIndex = viewport.getLocalPlayersIndex(index);
            if ((0x1 & viewport.getSlotFlag(playerIndex)) != 0)
                continue;

            if (skipCount > 0) {
                skipCount--;
                viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                continue;
            }
            Player p = viewport.getLocalPlayer(playerIndex);
            if (p == null || !p.isActive() || p.getLocation().getLongestDelta(player.getLocation()) > viewport.getViewingDistance() || !p.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance())) {
                boolean update = viewport.regionUpdate(playerIndex, p.getLocation().toRegionPacked());
                stream.putBits(1, 1);
                stream.putBits(1, 0);
                stream.putBits(2, 0);
                if (update) {
                    int hash = p.getLocation().toRegionPacked();
                    stream.putBits(1, 1);
                    encodeRegion(stream, hash, viewport.getRegionHash(playerIndex));
                    viewport.setRegionHash(playerIndex, p.getLocation().toRegionPacked());
                } else {
                    stream.putBits(1, 0);
                }
                viewport.setLocalPlayer(index, null);
            } else {
                System.out.println("NSN0 :1: ");
                if (p.getProperties().isTeleporting()) {
                    System.out.println("NSN0 :TP: True");
                    Location start = p.getLastPosition();
                    Location destination = p.getLocation();
                    int deltaX = destination.getX() - start.getX();
                    int deltaY = destination.getY() - start.getY();
                    int deltaZ = destination.getZ() - start.getZ();
                    boolean exterior = (Math.abs(deltaX) >= 14 || Math.abs(deltaY) >= 14);

                    stream.putBits(1, 1);
                    stream.putBits(1, p.getUpdateMasks().isUpdateRequired() ? 1 : 0);
                    stream.putBits(2, 3);

                    stream.putBits(1, exterior ? 1 : 0);

                    if (exterior) {
                        stream.putBits(30, (deltaY & 0x3fff) + ((deltaX & 0x3fff) << 14) + ((deltaZ & 0x3) << 28));
                    } else {
                        if (deltaX < 0) {
                            deltaX += 32;
                        }
                        if (deltaY < 0) {
                            deltaY += 32;
                        }

                        stream.putBits(12, (deltaY) + (deltaX << 5) + ((deltaZ & 0x3) << 10));
                    }
                    if(p.getUpdateMasks().isUpdateRequired())
                        playersToUpdate.add(p);
                } else if (p.getWalkingQueue().getWalkDir() != -1 || p.getUpdateMasks().isUpdateRequired()) {
                    System.out.println("NSN0 :TP: False");
                    boolean running = p.getWalkingQueue().getRunDir() != -1;
                    stream.putBits(1, 1);
                    stream.putBits(1, p.getUpdateMasks().isUpdateRequired() ? 1 : 0);
                    stream.putBits(2, running ? 2 : 1);
                    stream.putBits(running ? 4 : 3, running ? p.getWalkingQueue().getRunDir() : p.getWalkingQueue().getWalkDir());
                    if(p.getUpdateMasks().isUpdateRequired())
                        playersToUpdate.add(p);
                } else {
                    System.out.println("NSN0 :TP: Else");
                    for (int idx = index + 1; idx < viewport.getLocalPlayersIndexesCount(); idx++) {
                        int playerIdx = viewport.getLocalPlayersIndex(idx);
                        if ((0x1 & viewport.getSlotFlag(playerIdx)) != 0)
                            continue;

                        Player pl = viewport.getLocalPlayer(playerIdx);
                        if (pl == null || !pl.isActive() || pl.getLocation().getLongestDelta(player.getLocation()) > viewport.getViewingDistance() || !pl.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance()) || pl.getWalkingQueue().getWalkDir() != -1 || pl.getProperties().isTeleporting())
                            break;

                        skipCount++;
                    }
                    System.out.println("NSN0 :TP: Skip");
                    stream.putBits(1, 0);
                    if (skipCount == 0) {
                        //builder.putBits(1, 0);
                        stream.putBits(2, 0);
                    } else if (skipCount < 32) {
                        stream.putBits(2, 1);
                        stream.putBits(5, skipCount);
                    } else if (skipCount < 256) {
                        stream.putBits(2, 2);
                        stream.putBits(8, skipCount);
                    } else if (skipCount < 2048) {
                        stream.putBits(2, 3);
                        stream.putBits(11, skipCount);
                    }
                    //System.out.println("[NSN0] : " + skipCount);
                    viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                }
            }
        }

        stream.setByteAccess();

        skipCount = 0;

        stream.setBitAccess();

        for (int index = 0; index < viewport.getLocalPlayersIndexesCount(); index++) {
            int playerIndex = viewport.getLocalPlayersIndex(index);
            if ((0x1 & viewport.getSlotFlag(playerIndex)) == 0)
                continue;

            if (skipCount > 0) {
                skipCount--;
                viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                continue;
            }
            Player p = viewport.getLocalPlayer(playerIndex);
            if (p == null || !p.isActive() || p.getLocation().getLongestDelta(player.getLocation()) > viewport.getViewingDistance() || !p.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance())) {
                boolean update = viewport.regionUpdate(playerIndex, p.getLocation().toRegionPacked());
                stream.putBits(1, 1);
                stream.putBits(1, 0);
                stream.putBits(2, 0);
                if (update) {
                    int hash = p.getLocation().toRegionPacked();
                    stream.putBits(1, 1);
                    encodeRegion(stream, hash, viewport.getRegionHash(playerIndex));
                    viewport.setRegionHash(playerIndex, p.getLocation().toRegionPacked());
                } else {
                    stream.putBits(1, 0);
                }
                viewport.setLocalPlayer(index, null);
            } else {
                if (p.getProperties().isTeleporting()) {
                    Location start = p.getLastPosition();
                    Location destination = p.getLocation();
                    int deltaX = destination.getX() - start.getX();
                    int deltaY = destination.getY() - start.getY();
                    int deltaZ = destination.getZ() - start.getZ();
                    boolean exterior = (Math.abs(deltaX) >= 14 || Math.abs(deltaY) >= 14);

                    stream.putBits(1, 1);
                    stream.putBits(1, p.getUpdateMasks().isUpdateRequired() ? 1 : 0);
                    stream.putBits(2, 3);

                    stream.putBits(1, exterior ? 1 : 0);

                    if (exterior) {
                        stream.putBits(30, (deltaY & 0x3fff) + ((deltaX & 0x3fff) << 14) + ((deltaZ & 0x3) << 28));
                    } else {
                        if (deltaX < 0) {
                            deltaX += 32;
                        }
                        if (deltaY < 0) {
                            deltaY += 32;
                        }

                        stream.putBits(12, (deltaY) + (deltaX << 5) + ((deltaZ & 0x3) << 10));
                    }
                    if(p.getUpdateMasks().isUpdateRequired())
                        playersToUpdate.add(p);
                } else if (p.getWalkingQueue().getWalkDir() != -1 || p.getUpdateMasks().isUpdateRequired()) {
                    boolean running = p.getWalkingQueue().getRunDir() != -1;
                    stream.putBits(1, 1);
                    stream.putBits(1, p.getUpdateMasks().isUpdateRequired() ? 1 : 0);
                    stream.putBits(2, running ? 2 : 1);
                    stream.putBits(running ? 4 : 3, running ? p.getWalkingQueue().getRunDir() : p.getWalkingQueue().getWalkDir());
                    if(p.getUpdateMasks().isUpdateRequired())
                        playersToUpdate.add(p);
                } else {
                    for (int idx = index + 1; idx < viewport.getLocalPlayersIndexesCount(); idx++) {
                        int playerIdx = viewport.getLocalPlayersIndex(idx);
                        if ((0x1 & viewport.getSlotFlag(playerIdx)) == 0)
                            continue;

                        Player pl = viewport.getLocalPlayer(playerIdx);
                        if (pl == null || !pl.isActive() || pl.getLocation().getLongestDelta(player.getLocation()) > viewport.getViewingDistance() || !pl.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance()) || pl.getWalkingQueue().getWalkDir() != -1 || pl.getProperties().isTeleporting())
                            break;

                        skipCount++;
                    }
                    stream.putBits(1, 0);
                    if (skipCount == 0) {
                        //builder.putBits(1, 0);
                        stream.putBits(2, 0);
                    } else if (skipCount < 32) {
                        stream.putBits(2, 1);
                        stream.putBits(5, skipCount);
                    } else if (skipCount < 256) {
                        stream.putBits(2, 2);
                        stream.putBits(8, skipCount);
                    } else if (skipCount < 2048) {
                        stream.putBits(2, 3);
                        stream.putBits(11, skipCount);
                    }
                    //System.out.println("[NSN1] : " + skipCount);
                    viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                }
            }
        }

        stream.setByteAccess();

        skipCount = 0;

        stream.setBitAccess();

        for (int index = 0; index < viewport.getOutPlayersIndexesCount(); index++) {
            int playerIndex = viewport.getOutPlayersIndex(index);
            if ((0x1 & viewport.getSlotFlag(playerIndex)) == 0)
                continue;

            if (skipCount > 0) {
                skipCount--;
                viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                continue;
            }
            Player p = Repository.getPlayers().get(playerIndex);
            boolean update = p == null ? false : viewport.regionUpdate(playerIndex, p.getLocation().toRegionPacked());
            if (viewport.slotAvailable() && p != null && p != player && p.isActive() && p.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance())) {
                Location position = p.getLocation();
                stream.putBits(1, 1);
                stream.putBits(2, 0);
                stream.putBits(1, update ? 1 : 0);
                if (update) {
                    System.out.println("NSN2 :RU: True1");
                    encodeRegion(stream, viewport.getRegionHash(playerIndex), position.toRegionPacked());
                    System.out.println("here6");
                }
                stream.putBits(13, position.getXInRegion());
                stream.putBits(13, position.getYInRegion());
                stream.putBits(1, p.getUpdateMasks().isUpdateRequired() ? 1 : 0);

                if(p.getUpdateMasks().isUpdateRequired())
                    playersToUpdate.add(p);

                if(update)
                    viewport.setRegionHash(playerIndex, p.getLocation().toRegionPacked());

                viewport.setLocalPlayer(playerIndex, p);
                viewport.incrementLocals();
                viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
            } else {
                if (update) {
                    System.out.println("NSN2 :RU: True");
                    stream.putBits(1, 1);
                    encodeRegion(stream, viewport.getRegionHash(playerIndex), p.getLocation().toRegionPacked());
                    viewport.setRegionHash(playerIndex, p.getLocation().toRegionPacked());
                } else {
                    for (int idx = index + 1; idx < viewport.getOutPlayersIndexesCount(); idx++) {
                        int playerIdx = viewport.getOutPlayersIndex(idx);
                        if ((0x1 & viewport.getSlotFlag(playerIdx)) == 0)
                            continue;

                        Player pl = Repository.getPlayers().get(playerIdx);
                        if (pl != null && pl != player && pl.isActive() && pl.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance()) && !viewport.regionUpdate(playerIdx, pl.getLocation().toRegionPacked()))
                            break;

                        skipCount++;
                    }
                    stream.putBits(1, 0);
                    if (skipCount == 0) {
                        //builder.putBits(1, 0);
                        stream.putBits(2, 0);
                    } else if (skipCount < 32) {
                        stream.putBits(2, 1);
                        stream.putBits(5, skipCount);
                    } else if (skipCount < 256) {
                        stream.putBits(2, 2);
                        stream.putBits(8, skipCount);
                    } else if (skipCount < 2048) {
                        stream.putBits(2, 3);
                        stream.putBits(11, skipCount);
                    }
                    //System.out.println("[NSN2] : " + skipCount);
                    viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                }
            }
        }

        stream.setByteAccess();

        skipCount = 0;

        stream.setBitAccess();

        for (int index = 0; index < viewport.getOutPlayersIndexesCount(); index++) {
            int playerIndex = viewport.getOutPlayersIndex(index);
            if ((0x1 & viewport.getSlotFlag(playerIndex)) != 0)
                continue;

            if (skipCount > 0) {
                skipCount--;
                viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                continue;
            }
            Player p = Repository.getPlayers().get(playerIndex);
            boolean update = p == null ? false : viewport.regionUpdate(playerIndex, p.getLocation().toRegionPacked());
            if (viewport.slotAvailable() && p != null && p != player && p.isActive() && p.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance())) {
                Location position = p.getLocation();
                stream.putBits(1, 1);
                stream.putBits(2, 0);
                stream.putBits(1, update ? 1 : 0);
                if (update) {
                    System.out.println("NSN2 :RU: True1");
                    encodeRegion(stream, viewport.getRegionHash(playerIndex), position.toRegionPacked());
                    System.out.println("here6");
                }
                stream.putBits(13, position.getXInRegion());
                stream.putBits(13, position.getYInRegion());
                stream.putBits(1, p.getUpdateMasks().isUpdateRequired() ? 1 : 0);

                if(p.getUpdateMasks().isUpdateRequired())
                    playersToUpdate.add(p);

                if(update)
                    viewport.setRegionHash(playerIndex, p.getLocation().toRegionPacked());

                viewport.setLocalPlayer(playerIndex, p);
                viewport.incrementLocals();
                viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
            } else {
                if (update) {
                    System.out.println("NSN3 :RU: True");
                    stream.putBits(1, 1);
                    encodeRegion(stream, viewport.getRegionHash(playerIndex), p.getLocation().toRegionPacked());
                    viewport.setRegionHash(playerIndex, p.getLocation().toRegionPacked());
                } else {
                    System.out.println("NSN3 :RU: False");
                    for (int idx = index + 1; idx < viewport.getOutPlayersIndexesCount(); idx++) {
                        int playerIdx = viewport.getOutPlayersIndex(idx);
                        if ((0x1 & viewport.getSlotFlag(playerIdx)) != 0)
                            continue;

                        Player pl = Repository.getPlayers().get(playerIdx);
                        if (pl != null && pl != player && pl.isActive() && pl.getLocation().withinDistance(player.getLocation(), viewport.getViewingDistance()) && !viewport.regionUpdate(playerIdx, pl.getLocation().toRegionPacked()))
                            break;

                        skipCount++;
                    }
                    stream.putBits(1, 0);
                    if (skipCount == 0) {
                        //builder.putBits(1, 0);
                        stream.putBits(2, 0);
                    } else if (skipCount < 32) {
                        stream.putBits(2, 1);
                        stream.putBits(5, skipCount);
                    } else if (skipCount < 256) {
                        stream.putBits(2, 2);
                        stream.putBits(8, skipCount);
                    } else if (skipCount < 2048) {
                        stream.putBits(2, 3);
                        stream.putBits(11, skipCount);
                    }
                    //System.out.println("[NSN3] : " + skipCount);
                    viewport.setSlotFlag(playerIndex, (byte) (viewport.getSlotFlag(playerIndex) | 0x2));
                }
            }
        }

        stream.setByteAccess();

        Iterator pIter = playersToUpdate.iterator();
        while(pIter.hasNext()) {
            Player p = (Player) pIter.next();
            writeMasks(player, p, updateBlockData, false);
            pIter.remove();
        }

        stream.put(updateBlockData);

        /*processLocalPlayers(stream, updateBlockData, true, player);
        processLocalPlayers(stream, updateBlockData, false, player);
        processOutsidePlayers(stream, updateBlockData, true, player);
        processOutsidePlayers(stream, updateBlockData, false, player);
        stream.put(updateBlockData);
        viewport.totalRenderDataSentLength = 0;
        viewport.localPlayersIndexesCount = 0;
        viewport.outPlayersIndexesCount = 0;
        for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
            viewport.slotFlags[playerIndex] >>= 1;
            player = viewport.localPlayers[playerIndex];
            if (player == null)
                viewport.outPlayersIndexes[viewport.outPlayersIndexesCount++] = playerIndex;
            else
                viewport.localPlayersIndexes[viewport.localPlayersIndexesCount++] = playerIndex;
        }*/
        return stream;
    }

    protected static void encodeRegion(IoBuffer builder, int lastRegionHash, int currentRegionHash) {
        int lastRegionX = (lastRegionHash >> 8) & 0xff;
        int lastRegionY = 0xff & lastRegionHash;
        int lastPlane = (lastRegionHash >> 16) & 0x3;
        int currentRegionX = (currentRegionHash >> 8) & 0xff;
        int currentRegionY = 0xff & currentRegionHash;
        int currentPlane = (currentRegionHash >> 16) & 0x3;
        int planeOffset = currentPlane - lastPlane;
        System.out.println("here11111");
        if (lastRegionX == currentRegionX && lastRegionY == currentRegionY) {
            System.out.println("here1");
            builder.putBits(2, 1);
            builder.putBits(2, (planeOffset & 0x3));
        } else if (Math.abs(currentRegionX - lastRegionX) <= 1 && Math.abs(currentRegionY - lastRegionY) <= 1) {
            int opcode;
            int dx = currentRegionX - lastRegionX;
            int dy = currentRegionY - lastRegionY;
            if (dx == -1 && dy == -1) {
                opcode = 0;
            } else if (dx == 1 && dy == -1) {
                opcode = 2;
            } else if (dx == -1 && dy == 1) {
                opcode = 5;
            } else if (dx == 1 && dy == 1) {
                opcode = 7;
            } else if (dy == -1) {
                opcode = 1;
            } else if (dx == -1) {
                opcode = 3;
            } else if (dx == 1) {
                opcode = 4;
            } else if (dy == 1) {
                opcode = 6;
            } else {
                throw new RuntimeException("Invalid delta value for region hash!");
            }
            System.out.println("here2");
            builder.putBits(2, 2);
            builder.putBits(5, ((planeOffset & 0x3) << 3) + (opcode & 0x7));
        } else {
            System.out.println("here3");
            int xOffset = currentRegionX - lastRegionX;
            int yOffset = currentRegionY - lastRegionY;
            builder.putBits(2, 3);
            builder.putBits(18, (yOffset & 0xff) + ((xOffset & 0xff) << 8) + ((planeOffset & 0x3) << 16));
        }
    }

    /*private static void processLocalPlayers(IoBuffer stream, IoBuffer updateBlockData, boolean nsn0, Player player) {
        stream.setBitAccess();
        int skip = 0;//what exactly you trying to do/, this format is hard to read, i am used to eclipse but let me try
        RenderInfo info = player.getRenderInfo();
        // System.out.println("Updating "+info.localPlayersIndexesCount+" local players for "+player.getName(true));
        for (int i = 0; i < info.localPlayersIndexesCount; i++) {
            int playerIndex = info.localPlayersIndexes[i];
            //System.out.println("Local update index: "+playerIndex);
            if (nsn0 ? (0x1 & info.slotFlags[playerIndex]) != 0 : (0x1 & info.slotFlags[playerIndex]) == 0)
                continue;
            if (skip > 0) {
                skip--;
                info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 2);
                continue;
            }//brb breakfast 10 mins
            Player p = info.localPlayers[playerIndex];
            // System.out.println("Local loop: "+player.getName(true)+", "+p.getName(true));
            if (info.needsRemove(p)) {
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
                boolean needAppearenceUpdate = (p.getUpdateMasks().getMaskData() & 0x2) != 0;
                boolean needUpdate = p.getUpdateMasks().isUpdateRequired() || needAppearenceUpdate;
                if (needUpdate)
                    writeMasks(player, p, updateBlockData, needAppearenceUpdate);
                if (p.getProperties().isTeleporting()) {
                    stream.putBits(1, 1); // needs update
                    stream.putBits(1, needUpdate ? 1 : 0);
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
                        if (!needUpdate) //hasnt been sent yet
                            writeMasks(player, p, updateBlockData, true);
                    } else {
                        stream.putBits(1, needUpdate ? 1 : 0);
                        stream.putBits(2, running ? 2 : 1);
                        stream.putBits(running ? 4 : 3, opcode);
                    }
                } else if (needUpdate) {
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
                        if (info.needsRemove(p2)
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
    }*/

    private static void writeMasks(Player writingFor, Player updatable, IoBuffer composer, boolean forceSync) {
        //not even used
        System.out.println("Writing for: " + writingFor.getName(true) + ", updated" + updatable);
        PriorityQueue<UpdateFlag> flags = new PriorityQueue<UpdateFlag>(updatable.getUpdateMasks().flagQueue);
        int maskdata = 0;
        for (UpdateFlag flag : flags) {// you have some order is wrong, it sends less data, this is how im doing it.
            System.out.println(flag.getClass().getName());
            maskdata |= flag.data();
        }
        /*if ((maskdata & 0x2) == 0) {
            maskdata |= 0x2;
            System.out.println("Force appearance update");
            flags.add(new AppearanceFlag(updatable));
        }*/
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

    /*private static void updateRegionHash(IoBuffer stream, int lastRegionHash, int currentRegionHash) {
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
        //System.out.println("Updating "+info.outPlayersIndexesCount+" global players for "+player.getName(true));
        for (int i = 0; i < info.outPlayersIndexesCount; i++) {
            int playerIndex = info.outPlayersIndexes[i];
            //System.out.println("Global update index "+playerIndex);
            if (nsn2 ? (0x1 & info.slotFlags[playerIndex]) == 0
                    : (0x1 & info.slotFlags[playerIndex]) != 0)
                continue;
            if (skip > 0) {
                skip--;
                info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 0x2);
                continue;
            }
            Player p = Repository.getPlayers().get(playerIndex);
            if (info.needsAdd(p)) {//why da hell it read as two then sec
                //System.out.println("Adding player index: " + p.getIndex() + " to client name: " + player.getName(true) + ", index: " + player.getIndex());
                stream.putBits(1, 1);
                stream.putBits(2, 0); // request add
                int hash = p.getLocation().toRegionPacked();
                if (hash == info.regionHashes[playerIndex])
                    stream.putBits(1, 0);//it looks identical..i know ;/
                else {
                    stream.putBits(1, 1);
                    updateRegionHash(stream, info.regionHashes[playerIndex], hash);
                    info.regionHashes[playerIndex] = hash;
                }
                stream.putBits(13, p.getLocation().getRegionX());
                stream.putBits(13, p.getLocation().getRegionY());
                boolean needAppearenceUpdate = (p.getUpdateMasks().getMaskData() & 0x2) != 0;
                writeMasks(player, p, updateBlockData, needAppearenceUpdate);//lmao epic fail
                stream.putBits(1, 1);
                info.localAddedPlayers++;
                info.localPlayers[p.getIndex()] = p;
                info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 0x2);
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
                        if (info.needsAdd(p2)
                                || (p2 != null && p2.getLocation().toRegionPacked() != info.regionHashes[p2Index]))
                            break;
                        skip++;
                    }
                    skipPlayers(stream, skip);
                    info.slotFlags[playerIndex] = (byte) (info.slotFlags[playerIndex] | 0x2);
                }//try
            }
        }
        stream.setByteAccess();
    }

    private static void skipPlayers(IoBuffer stream, int amount) {
        stream.putBits(2, amount == 0 ? 0 : amount > 255 ? 3 : (amount > 31 ? 2 : 1));
        if (amount > 0)
            stream.putBits(amount > 255 ? 11 : (amount > 31 ? 8 : 5), amount);
    }*/

}