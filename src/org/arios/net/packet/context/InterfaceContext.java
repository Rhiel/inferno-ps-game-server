package org.arios.net.packet.context;

import org.arios.game.node.entity.player.Player;
import org.arios.net.packet.Context;

/**
 * The interface packet context.
 * @author Emperor
 *
 */
public final class InterfaceContext implements Context {

    /**
     * The player.
     */
    private Player player;

    /**
     * The window id.
     */
    private int windowId;

    /**
     * The component id.
     */
    private int componentId;

    /**
     * The component id.
     */
    private int fullscreenComponentId;

    /**
     * The interface id.
     */
    private final int interfaceId;

    /**
     * If the interface can be walked over.
     */
    private final boolean walkable;

    /**
     * Constructs a new {@code InterfaceContext} {@code Object}.
     * @param player The player.
     * @param windowId The window id.
     * @param componentId The window component id.
     * @param interfaceId The interface id.
     * @param walkable If we can walk over the interface.
     */
    public InterfaceContext(Player player, int windowId, int componentId, int fullscreenComponentId, int interfaceId, boolean walkable) {
        this.player = player;
        this.windowId = windowId;
        this.componentId = componentId;
        this.fullscreenComponentId = fullscreenComponentId;
        this.interfaceId = interfaceId;
        this.walkable = walkable;
    }

    /**
     * Transforms this context for the new player & id.
     * @param player The player.
     * @param id The new interface id.
     * @return The interface context created.
     */
    public InterfaceContext transform(Player player, int id) {
        return new InterfaceContext(player, windowId, componentId, fullscreenComponentId, id, walkable);
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    /**
     * Sets the player.
     * @param player The player.
     * @return This context instance.
     */
    public Context setPlayer(Player player) {
        this.player = player;
        return this;
    }

    /**
     * Gets the windowId.
     * @return The windowId.
     */
    public int getWindowId() {
        return windowId;
    }

    public void setWindowId(int id) { this.windowId = id; }

    /**
     * Sets the component id.
     * @param componentId The component id.
     */
    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    /**
     * Gets the componentId.
     * @return The componentId.
     */
    public int getComponentId() {
        return getPlayer().getInterfaceManager().isResizable() ? fullscreenComponentId : componentId;
    }

    public int getFixedComponentId() {
        return componentId;
    }
    public int getFullComponentId() {
        return fullscreenComponentId;
    }

    /**
     * Gets the interfaceId.
     * @return The interfaceId.
     */
    public int getInterfaceId() {
        return interfaceId;
    }

    /**
     * Gets the walkable.
     * @return The walkable.
     */
    public boolean isWalkable() {
        return walkable;
    }

}