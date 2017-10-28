package org.arios.game.node.entity.player.info;

import java.util.LinkedList;
import java.util.List;

import org.arios.ServerConstants;
import org.arios.game.node.entity.Entity;
import org.arios.game.node.entity.npc.NPC;
import org.arios.game.node.entity.player.Player;
import org.arios.game.world.map.Location;
import org.arios.game.world.repository.Repository;
import org.arios.net.Constants;
import org.arios.net.packet.IoBuffer;

/**
 * Holds a player's render information.
 *
 * @author Emperor
 */
public final class RenderInfo {

    private Player player;

    public byte[] slotFlags;

    public Player[] localPlayers;
    public int[] localPlayersIndexes;
    public int localPlayersIndexesCount;

    public int[] outPlayersIndexes;
    public int outPlayersIndexesCount;

    public int[] regionHashes;

    public byte[][] cachedAppearencesHashes;
    public int totalRenderDataSentLength;

    /**
     * The list of local NPCs.
     */
    private List<NPC> localNpcs = new LinkedList<NPC>();

    /**
     * The appearance time stamps (in millisecond).
     */
    private final long[] appearanceStamps = new long[ServerConstants.MAX_PLAYERS];

    /**
     * The entities requiring a mask update.
     */
    private Entity[] maskUpdates = new Entity[256];

    /**
     * The mask update count.
     */
    private int maskUpdateCount;

    /**
     * The last location of this player.
     */
    private Location lastLocation;

    /**
     * If the player has just logged in.
     */
    private boolean onFirstCycle = true;

    /**
     * If the player has prepared appearance data this cycle.
     */
    private boolean preparedAppearance;

    /**
     * The amount of local players added this tick.
     */
    public int localAddedPlayers;

    /**
     * The maximum amount of local players being added per tick. This is to
     * decrease time it takes to load crowded places (such as home).
     */
    private static final int MAX_PLAYER_ADD = 15;

    /**
     * Constructs a new {@code RenderInfo} {@code Object}.
     *
     * @param player The player.
     */
    public RenderInfo(Player player) {
        this.player = player;
        slotFlags = new byte[2048];
        localPlayers = new Player[2048];
        localPlayersIndexes = new int[ServerConstants.MAX_PLAYERS];
        outPlayersIndexes = new int[2048];
        regionHashes = new int[2048];
        cachedAppearencesHashes = new byte[ServerConstants.MAX_PLAYERS][];
    }

    /**
     * Updates the player rendering information.
     */
    public void updateInformation() {
        onFirstCycle = false;
        lastLocation = player.getLocation();
        preparedAppearance = false;
    }

    /**
     * Registers an entity requiring a mask update.
     *
     * @param entity The entity.
     */
    public void registerMaskUpdate(Entity entity) {
        maskUpdates[maskUpdateCount++] = entity;
    }

    /**
     * Gets the localNpcs.
     *
     * @return The localNpcs.
     */
    public List<NPC> getLocalNpcs() {
        return localNpcs;
    }

    /**
     * Sets the localNpcs.
     *
     * @param localNpcs The localNpcs to set.
     */
    public void setLocalNpcs(List<NPC> localNpcs) {
        this.localNpcs = localNpcs;
    }

    /**
     * Gets the onFirstCycle.
     *
     * @return The onFirstCycle.
     */
    public boolean isOnFirstCycle() {
        return onFirstCycle;
    }

    /**
     * Sets the onFirstCycle.
     *
     * @param onFirstCycle The onFirstCycle to set.
     */
    public void setOnFirstCycle(boolean onFirstCycle) {
        this.onFirstCycle = onFirstCycle;
    }

    /**
     * Gets the lastLocation.
     *
     * @return The lastLocation.
     */
    public Location getLastLocation() {
        return lastLocation;
    }

    /**
     * Sets the lastLocation.
     *
     * @param lastLocation The lastLocation to set.
     */
    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    /**
     * Gets the appearanceStamps.
     *
     * @return The appearanceStamps.
     */
    public long[] getAppearanceStamps() {
        return appearanceStamps;
    }

    /**
     * Sets the prepared appearance flag.
     *
     * @param prepared If the player has prepared appearance setting this cycle.
     */
    public void setPreparedAppearance(boolean prepared) {
        this.preparedAppearance = prepared;
    }

    /**
     * Checks if the player has prepared appearance data this cycle.
     *
     * @return {@code True} if so.
     */
    public boolean preparedAppearance() {
        return preparedAppearance;
    }

    /**
     * The skipped player indexes.
     */
    public final byte[] skips = new byte[2048];

    public void enterWorld(IoBuffer stream) {
        stream.setBitAccess();
        stream.putBits(30, player.getLocation().toPositionPacked());
        localPlayers[player.getIndex()] = player;
        localPlayersIndexes[localPlayersIndexesCount++] = player.getIndex();
        for (int playerIndex = 1; playerIndex < 2048; playerIndex++) {
            if (playerIndex == player.getIndex())
                continue;
            Player player = Repository.getPlayers().get(playerIndex);
            stream.putBits(18, regionHashes[playerIndex] = player == null ? 0 : player.getLocation().toRegionPacked());
            outPlayersIndexes[outPlayersIndexesCount++] = playerIndex;

        }
        stream.setByteAccess();
    }

    private boolean needsRemove(Player p) {
        // can't just do this or you'll get chat from other dungeons
        return p != player && (!p.isActive() || !(player.getLocation().withinDistance(p.getLocation(), player.getSettings().hasLargeSceneView() ? 126 : 14)));
    }

    private boolean needsAdd(Player p) {
        return p != null && p.getSettings().isRunToggled() && (player.getLocation().withinDistance(p.getLocation(), player.getSettings().hasLargeSceneView() ? 126 : 14)
                 && localAddedPlayers < MAX_PLAYER_ADD);
    }

}