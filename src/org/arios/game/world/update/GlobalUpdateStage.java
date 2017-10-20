package org.arios.game.world.update;

import org.arios.game.node.entity.player.Player;

/**
 * Created by Chris on 7/23/2017.
 */
public enum GlobalUpdateStage {

    ADD_PLAYER,
    HEIGHT_UPDATED,
    MAP_REGION_DIRECTION,
    TELEPORTED;

    /**
     * Gets the global update stages.
     * @param player The player for the update,
     * @param otherPlayer The players to update for.
     * @return The state.
     */
    public static GlobalUpdateStage getStage(Player player, Player otherPlayer) {
        if (otherPlayer == null || !(otherPlayer.isActive())) {
            return null;
        } else if (player != otherPlayer && player.getLocation().withinDistance(otherPlayer.getLocation())) {
            return ADD_PLAYER;
        } else if (otherPlayer.getRenderInfo().getLastLocation() != null && otherPlayer.getLocation().getZ() != otherPlayer.getRenderInfo().getLastLocation().getZ()) {
            return HEIGHT_UPDATED;
        } else if (otherPlayer.getProperties().isTeleporting() || otherPlayer.getRenderInfo().isOnFirstCycle()) {
            return TELEPORTED;
        }
        return null;
    }
}
