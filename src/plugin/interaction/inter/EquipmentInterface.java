package plugin.interaction.inter;

import org.arios.cache.def.impl.ItemDefinition;
import org.arios.game.component.CloseEvent;
import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.container.Container;
import org.arios.game.container.ContainerEvent;
import org.arios.game.container.ContainerListener;
import org.arios.game.container.access.BitregisterAssembler;
import org.arios.game.container.impl.EquipmentContainer;
import org.arios.game.content.global.action.EquipHandler;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.interaction.OptionHandler;
import org.arios.game.node.entity.combat.DeathTask;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.prayer.PrayerType;
import org.arios.game.node.item.Item;
import org.arios.game.system.task.Pulse;
import org.arios.game.world.GameWorld;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.ContainerContext;
import org.arios.net.packet.out.ContainerPacket;
import org.arios.plugin.Plugin;

/**
 * Represents the equipment interface..
 *
 * @author Emperor
 * @version 1.1
 */
public final class EquipmentInterface extends ComponentPlugin {

    public static enum SlotData {

        WEAPON(9, 3, "weapon"),

        CHEST(10, 4, "torso"),

        HELMET(6, 0, "helmet"),

        LEGS(12, 7, "legs"),

        SHIELD(11, 5, "shield"),

        AMULET(8, 2, "amulet"),

        BOOTS(14, 10, "boots"),

        RING(15, 12, "ring"),

        GLOVES(13, 9, "gloves"),

        CAPE(7, 1, "cape"),

        AMMO(16, 13, "ammo")

        ;

        int button, slot;

        SlotData(int button, int slot, String slot_name) {
            this.button = button;
            this.slot = slot;
        }

        public int getButton() {
            return button;
        }

        public int getSlot() {
            return slot;
        }


        public static SlotData getSlotFor(int button) {
            for (SlotData item : SlotData.values()) {
                if (button == item.getButton()) {
                    return item;
                }
            }
            return null;
        }

    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ComponentDefinition.put(387, this);
        ComponentDefinition.put(465, this);
        ComponentDefinition.put(513, this);
        return this;
    }

    @Override
    public boolean handle(final Player p, Component component, int opcode, int button, final int slot, final int itemId) {
        /**
         * Equipment Screen.
         */
        if (component.getId() == 465) {
            if (button != 103) {
                return false;
            }
            switch (opcode) {
                case 77:
                    p.getPulseManager().clear();
                    GameWorld.submit(new Pulse(1, p) {
                        @Override
                        public boolean pulse() {
                            EquipHandler.unequip(p, slot, itemId);
                            return true;
                        }
                    });
                    return true;
                //case 205: //TODO FIGURE OUT WHY THIS USED 205 INSTEAD OF REG RYAN!
              /*  case PacketConstants.ITEM_OPT_2:
                    GameWorld.submit(new Pulse(1, p) {
                        @Override
                        public boolean pulse() {
                            operate(p, slot, itemId);
                            return true;
                        }
                    });
                    return true;*/
            }
            return false;
        }
        /**
         * Inventory Screen.
         */
        if (component.getId() == 513) {
            if (button != 0) {
                return false;
            }
            switch (opcode) {
              /*  case PacketConstants.ITEM_OPT_2:
                    p.getPulseManager().clear();
                    GameWorld.submit(new Pulse(1, p) {
                        @Override
                        public boolean pulse() {
                            if (p.getInventory().get(slot) == null) {
                                return false;
                            } //TODO THIS MAY ALREADY BE USED LATER ON MAYBE REMOVE NULL CHECK?
                            if(p.getInventory().get(slot).getInteraction().get(1) == null) {
                                p.sendMessage("You can't wear that!");
                            } else {
                                p.getInventory().get(slot).getInteraction().get(1).getHandler().handle(p, p.getInventory().get(slot), "wear");
                            }
                            return true;
                        }
                    });
                    return true;*/
            }
            return false;
        }
        if(component.getId() == 387) {
            if(button >= 6 && button <= 16) {
                System.out.println("here");
                if (opcode == 77) {
                    p.getPulseManager().clear();
                    GameWorld.submit(new Pulse(1, p) {
                        @Override
                        public boolean pulse() {
                            System.out.println("here");
                            EquipHandler.unequip(p, SlotData.getSlotFor(button).getSlot(), itemId);
                            return true;
                        }
                    });
                    return true;
                }
            }
        }
        /**
         * Equipment Tab
         */
        switch (opcode) {
            /*case PacketConstants.ITEM_OPT_2:
                GameWorld.submit(new Pulse(1, p) {
                    @Override
                    public boolean pulse() {
                        operate(p, slot, itemId);
                        return true;
                    }
                });
                return true;*/
            default:
                switch (button) {
                    case 21:
                        if (p.getInterfaceManager().isOpened() && p.getInterfaceManager().getOpened().getId() == 102) {
                            return true;
                        }
                        boolean skulled = p.getSkullManager().isSkulled();
                        boolean usingProtect = p.getPrayer().get(PrayerType.PROTECT_ITEMS);
                        p.getInterfaceManager().openComponent(102);
                        p.getPacketDispatch().sendAccessMask(211, 0, 2, 6684690, 4);
                        p.getPacketDispatch().sendAccessMask(212, 0, 2, 6684693, 42);
                        Container[] itemArray = DeathTask.getContainers(p);
                        Container kept = itemArray[0];
                        int state = 0; //1=familiar carrying items
                        int keptItems = skulled ? (usingProtect ? 1 : 0) : (usingProtect ? 4 : 3);
                        int zoneType = p.getZoneMonitor().getType();
                        int pvpType = p.getSkullManager().isWilderness() ? 0 : 1;
                        Object[] params = new Object[] { 11510, 12749, "", state, pvpType, kept.getId(3), kept.getId(2), kept.getId(1), kept.getId(0), keptItems, zoneType };
                        //PacketRepository.send(ContainerPacket.class, new ContainerContext(p, 149, 0, 91, itemArray[1], false)); //THIS PUT THE ITEMS IN INV? LOL TODO REMOVE.
                        p.getPacketDispatch().sendRunScript(118, "iiooooiisii", params);
                        break;
                    case 17:
                        if (p.getInterfaceManager().isOpened() && p.getInterfaceManager().getOpened().getId() == 465) {
                            return true;
                        }
                        /**
                         * Inventory Screen Containers.
                         */
                        final ContainerListener listener = new ContainerListener() {
                            @Override
                            public void update(Container c, ContainerEvent e) {
                                PacketRepository.send(ContainerPacket.class, new ContainerContext(p, 513, 0, 98, e.getItems(), false, e.getSlots()));
                            }

                            @Override
                            public void refresh(Container c) {
                                PacketRepository.send(ContainerPacket.class, new ContainerContext(p, 513, 0, 98, c, false));
                            }
                        };
                        p.getInterfaceManager().openComponent(465).setCloseEvent(new CloseEvent() {
                            @Override
                            public boolean close(Player player, Component c) {
                                player.removeAttribute("equip_stats_open");
                                //player.getInterfaceManager().openDefaultTabs();
                                player.getInventory().getListeners().remove(listener);
                                player.getInterfaceManager().closeSingleTab(); // my way
                                return true;
                            }
                        });
                        p.setAttribute("equip_stats_open", true);

                        //Arios way below
                       // EquipmentContainer.update(p);
                        //p.getInterfaceManager().removeTabs(0, 1, 2, 5, 6, 7, 8, 9, 10, 11, 12, 13);
                        //p.getInterfaceManager().openTab(new Component(149));
                       // p.getInventory().getListeners().add(listener);
                       // p.getInventory().refresh();
                       // ItemDefinition.statsUpdate(p);
                        //p.getPacketDispatch().sendAccessMask(BitregisterAssembler.calculateRegister(2), 20, 465, 0, 13);

                        //My way below
                        p.getInterfaceManager().openSingleTab(new Component(513));
                        break;
                }
        }
        return true;
    }

    /**
     * Operates an item.
     * @param player The player.
     * @param slot The container slot.
     * @param itemId The item id.
     */
    public void operate(Player player, int slot, int itemId) {
        if (slot < 0 || slot > 13) {
            return;
        }
        Item item = player.getEquipment().get(slot);
        if (item == null) {
            return;
        }
        OptionHandler handler = item.getOperateHandler();
        if (handler != null && handler.handle(player, item, "operate")) {
            return;
        }
        player.getPacketDispatch().sendMessage("You can't operate that.");
    }

}
