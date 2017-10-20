package org.arios.game.world.update;

import org.arios.game.node.entity.player.Player;

/**
 * Created by Chris on 7/23/2017.
 */
public enum LocalUpdateStage {

    REMOVE_PLAYER,
    WALKING,
    RUNNING,
    TELEPORTED,
    NO_UPDATE;

    /**
     * Gets the local player updating stages.
     * @param player The player for the update.
     * @param otherPlayer The player to update for.
     * @return The stage of a certain update.
     */
    public static LocalUpdateStage getStage(Player player, Player otherPlayer) {
        if (otherPlayer == null)
            return REMOVE_PLAYER;
        if (!player.getLocation().withinDistance(otherPlayer.getLocation())) {
            return REMOVE_PLAYER;
        } else if (otherPlayer.getProperties().isTeleporting()) {
            return TELEPORTED;
        } else if (otherPlayer.getWalkingQueue().getRunDir() != -1) {
            return RUNNING;
        } else if (otherPlayer.getWalkingQueue().getWalkDir() != -1) {
            return WALKING;
        }
        return otherPlayer.getUpdateMasks().isUpdateRequired() ? NO_UPDATE : null;
    }
}
