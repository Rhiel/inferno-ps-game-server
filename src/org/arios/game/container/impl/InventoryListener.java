package org.arios.game.container.impl;

import org.arios.game.container.Container;
import org.arios.game.container.ContainerEvent;
import org.arios.game.container.ContainerListener;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.ContainerContext;
import org.arios.net.packet.out.ContainerPacket;

/**
 * Handles the inventory container listening.
 *
 * @author Emperor
 */
public final class InventoryListener implements ContainerListener {

    /**
     * The player.
     */
    private final Player player;

    /**
     * Constructs a new {@code InventoryListener} {@code Object}.
     *
     * @param player The player.
     */
    public InventoryListener(Player player) {
        this.player = player;
    }

    /**
     * Updates the required settings etc for the player when the container
     * updates.
     *
     * @param c The container.
     */
    public void update(Container c) {
        player.getSettings().updateWeight();
    }

    @Override
    public void refresh(Container c) {
        PacketRepository.send(ContainerPacket.class, new ContainerContext(player, 149, 0, 93, c, false));
        update(c);
    }

    @Override
    public void update(Container c, ContainerEvent event) {
        PacketRepository.send(ContainerPacket.class, new ContainerContext(player, 149, 0, 93, event.getItems(), false, event.getSlots()));
        update(c);
    }

}