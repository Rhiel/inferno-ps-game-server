package org.arios.game.container.impl;

import java.nio.ByteBuffer;

import org.arios.game.component.CloseEvent;
import org.arios.game.component.Component;
import org.arios.game.container.Container;
import org.arios.game.container.ContainerEvent;
import org.arios.game.container.ContainerListener;
import org.arios.game.container.ContainerType;
import org.arios.game.container.SortType;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.IronmanMode;
import org.arios.game.node.item.Item;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.ContainerContext;
import org.arios.net.packet.out.ContainerPacket;
import org.arios.parser.item.ItemConfiguration;

/**
 * Represents the bank container.
 *
 * @author Emperor
 */
public final class BankContainer extends Container {

    /**
     * The player reference.
     */
    private Player player;

    /**
     * The bank listener.
     */
    private final BankListener listener;

    /**
     * Set {@code true} to note items.
     */
    private boolean noteItems;

    /**
     * If the bank is open.
     */
    private boolean open;

    /**
     * Construct a new {@code BankContainer} {@code Object}.
     * @param player The player reference.
     */
    public BankContainer(Player player) {
        super(536, ContainerType.ALWAYS_STACK, SortType.HASH);
        super.register(listener = new BankListener(player));
        this.player = player;
    }

    /**
     * Open the bank.
     */
    public void open() {
        if (open) {
            return;
        }
        if (!player.getBankPinManager().isUnlocked()) {
            player.getBankPinManager().openType(1);
            return;
        }
        player.getConfigManager().set(867, 48304248);
        player.getPacketDispatch().sendCS2Script(917, new Object[] { -1, -2147483648 });
        player.getPacketDispatch().sendAccessMask(1181438, 3, 15, 0, 27);
        player.getPacketDispatch().sendAccessMask(1054, 12, 15, 0, 27);
        super.refresh();
        player.getInventory().getListeners().add(listener);
        player.getInventory().refresh();
        player.getInterfaceManager().openComponent(12).setCloseEvent((player, c) -> {
            BankContainer.this.close();
            return true; //Return true so the component gets removed.
        });
        player.getInterfaceManager().openSingleTab(new Component(15));
        open = true;
        shift();
        add(new Item(4151));
    }

    /**
     * Closes the bank.
     */
    public void close() {
        open = false;
        player.getInventory().getListeners().remove(listener);
        player.getInterfaceManager().closeSingleTab();
        //TODO: Add anything else for banking.
    }

    /**
     * Adds an item to the bank container.
     * @param slot The item slot.
     * @param amount The amount.
     */
    public void addItem(int slot, int amount) {
        if (slot < 0 || slot > player.getInventory().capacity() || amount < 1) {
            return;
        }
        Item item = player.getInventory().get(slot);
        if (item == null) {
            return;
        }
        int maximum = player.getInventory().getAmount(item);
        if (amount > maximum) {
            amount = maximum;
        }
        int maxCount = super.getMaximumAdd(item);
        if (amount > maxCount) {
            amount = maxCount;
            if (amount < 1) {
                player.getPacketDispatch().sendMessage("There is not enough space left in your bank.");
                return;
            }
        }
        if (!item.getDefinition().getConfiguration(ItemConfiguration.BANKABLE, true)) {
            player.sendMessage("A magical force prevents you from banking this item");
            return;
        }
        item = new Item(item.getId(), amount, item.getCharge());
        boolean unnote = !item.getDefinition().isUnnoted();
        if (player.getInventory().remove(item, slot, true)) {
            Item add = unnote ? new Item(item.getDefinition().getNoteId(), amount, item.getCharge()) : item;
            if (unnote && !add.getDefinition().isUnnoted()) {
                add = item;
            }
            super.add(add);
        }
    }

    /**
     * Takes a item from the bank container and adds one to the inventory container.
     * @param slot The slot.
     * @param amount The amount.
     */
    public void takeItem(int slot, int amount) {
        if (slot < 0 || slot > super.capacity() || amount <= 0) {
            return;
        }
        Item item = player.getBank().get(slot);
        if (item == null) {
            return;
        }
        if (amount > item.getAmount()) {
            amount = item.getAmount(); //It always stacks in the bank.
        }
        item = new Item(item.getId(), amount, item.getCharge());
        int noteId = item.getDefinition().getNoteId();
        Item add = noteItems && noteId > 0 ? new Item(noteId, amount, item.getCharge()) : item;
        int maxCount = player.getInventory().getMaximumAdd(add);
        if (amount > maxCount) {
            item.setAmount(maxCount);
            add.setAmount(maxCount);
            if (maxCount < 1) {
                player.getPacketDispatch().sendMessage("Not enough space in your inventory.");
                return;
            }
        }
        if (noteItems && noteId < 0) {
            player.getPacketDispatch().sendMessage("This item can't be withdrawn as a note.");
            add = item;
        }
        if (super.remove(item, slot, true)) {
            player.getInventory().add(add);
        }
    }

    /**
     * Checks if the item can be added.
     * @param item the item.
     * @return {@code True} if so.
     */
    public boolean canAdd(Item item) {
        return item.getDefinition().getConfiguration(ItemConfiguration.BANKABLE, true);
    }

    /**
     * If items have to be noted.
     * @return If items have to be noted {@code true}.
     */
    public boolean isNoteItems() {
        return noteItems;
    }

    /**
     * Set if items have to be noted.
     * @param noteItems If items have to be noted {@code true}.
     */
    public void setNoteItems(boolean noteItems) {
        this.noteItems = noteItems;
    }

    /**
     * Listens to the bank container.
     * @author Emperor
     */
    private static class BankListener implements ContainerListener {

        /**
         * The player reference.
         */
        private Player player;

        /**
         * Construct a new {@code BankListener} {@code Object}.
         * @param player The player reference.
         */
        public BankListener(Player player) {
            this.player = player;
        }

        @Override
        public void update(Container c, ContainerEvent event) {
            if (c instanceof BankContainer) {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, -1, 64207, 95, event.getItems(), false, event.getSlots()));
            } else {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, -1, 64209, 93, event.getItems(), false, event.getSlots()));
            }
        }

        @Override
        public void refresh(Container c) {
            if (c instanceof BankContainer) {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, -1, 64207, 95, c.toArray(), c.capacity(), false));
            } else {
                PacketRepository.send(ContainerPacket.class, new ContainerContext(player, -1, 64209, 93, c.toArray(), 28, false));
            }
        }
    }
}
