package org.arios.net.packet.in;

import java.util.Arrays;
import java.util.List;

import org.arios.ServerConstants;
import org.arios.game.content.dialogue.DialogueAction;
import org.arios.game.interaction.Interaction;
import org.arios.game.interaction.MovementPulse;
import org.arios.game.interaction.Option;
import org.arios.game.node.entity.npc.NPC;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.ai.AIPlayer;
import org.arios.game.node.item.GroundItem;
import org.arios.game.node.item.GroundItemManager;
import org.arios.game.node.object.GameObject;
import org.arios.game.world.map.Location;
import org.arios.game.world.map.RegionManager;
import org.arios.game.world.repository.Repository;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.PlayerContext;
import org.arios.net.packet.out.ClearMinimapFlag;

/**
 * Handles the incoming interaction packets.
 *
 * @author Emperor
 */
public final class InteractionPacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
        if (player == null) {
            return;
        }
        if (buffer.opcode() != 105) {
            player = getPlayer(player);
        }
        if (player.getLocks().isInteractionLocked() || !player.getInterfaceManager().close()) {
            return;
        }
        //TODO: Possible chatbox closing ?
        int x = 0, y = 0;
        int objectId = 0;
        switch (buffer.opcode()) {
    /*case 182: // NPC action 1
        int index = buffer.getShortA();
	    handleNPCInteraction(player, 0, index);
	    break;
	case 197: // NPC action 2
	    index = buffer.getLEShortA();
	    handleNPCInteraction(player, 1, index);
	    break;
	case 82: // NPC action 3
	    index = buffer.getLEShortA();
	    handleNPCInteraction(player, 2, index);
	    break;
	case 241: // NPC action 4
	    index = buffer.getLEShortA();
	    handleNPCInteraction(player, 3, index);
	    break;
	case 239: // NPC action 5
	    index = buffer.getShort();
	    handleNPCInteraction(player, 4, index);
	    break;
	case 50: // Object action 1
	    int x = buffer.getLEShortA();
	    int objectId = buffer.getShort() & 0xFFFF;
	    int y = buffer.getShort();
	    handleObjectInteraction(player, 0, x, y, objectId);
	    break;
	case 28: // Object action 2
	    x = buffer.getLEShort();
	    y = buffer.getShortA();
	    objectId = buffer.getLEShortA() & 0xFFFF;
	    handleObjectInteraction(player, 1, x, y, objectId);
	    break;
	case 67: // Object action 3
	    x = buffer.getLEShort();
	    y = buffer.getLEShortA();
	    objectId = buffer.getShort() & 0xFFFF;
	    handleObjectInteraction(player, 2, x, y, objectId);
	    break;
	case 217: // Object action 4
	    y = buffer.getShort();
	    x = buffer.getShort();
	    objectId = buffer.getLEShort() & 0xFFFF;
	    handleObjectInteraction(player, 3, x, y, objectId);
	    break;
	case 152: // Object action 5
	    objectId = buffer.getLEShortA() & 0xFFFF;
	    x = buffer.getLEShort();
	    y = buffer.getLEShortA();
	    handleObjectInteraction(player, 4, x, y, objectId);
	    break;
	case 185: // Player action 1
	    index = buffer.getLEShortA();
	    handlePlayerInteraction(player, 0, index);
	    break;
	case 242: // Player action 2
	    index = buffer.getLEShort();
	    handlePlayerInteraction(player, 1, index);
	    break;
	case 214: // Player action 3
	    index = buffer.getShortA();
	    handlePlayerInteraction(player, 2, index);
	    break;
	case 228: // Player action 4
	    index = buffer.getLEShortA();
	    handlePlayerInteraction(player, 3, index);
	    break;
	case 249: // Player action 5
	    index = buffer.getLEShortA();
	    handlePlayerInteraction(player, 4, index);
	    break;
	case 245: // Player action 6
	    index = buffer.getShort();
	    handlePlayerInteraction(player, 5, index);
	    break;
	case 52: // Player action 7
	    index = buffer.getLEShort();
	    handlePlayerInteraction(player, 6, index);
	    break;
	case 105: // Player action 8
	    index = buffer.getShort();
	    handlePlayerInteraction(player, 7, index);
	    break;*/
            case 160: // Object action 1
                objectId = buffer.getShortA();
                x = buffer.getLEShort();
                buffer.get();//unknown (boolean - clicking/typing)
                y = buffer.getShortA();
                handleObjectInteraction(player, 0, x, y, objectId);
                break;
            case 130: // Object action 2
                y = buffer.getLEShortA();
                buffer.getS();//unknown (boolean - clicking/typing)
                objectId = buffer.getShort();
                x = buffer.getLEShort();
                handleObjectInteraction(player, 1, x, y, objectId);
                break;
            case 86: // Object action 2
                y = buffer.getLEShort();
                objectId = buffer.getLEShortA();
                buffer.getC();//unknown (boolean - clicking/typing)
                x = buffer.getLEShortA();
                handleObjectInteraction(player, 0, x, y, objectId);
                break;
            case 186: // Ground item action 1
                y = buffer.getShortA();
                int itemId = buffer.getShort();
                x = buffer.getShortA();
                handleGroundItemInteraction(player, 2, itemId, Location.create(x, y, player.getLocation().getZ()));
                break;
	/*case 169: // Ground item action 2
	    x = buffer.getLEShort();
	    y = buffer.getShort();
	    itemId = buffer.getLEShortA();
	    handleGroundItemInteraction(player, 3, itemId, Location.create(x, y, player.getLocation().getZ()));
	    break;*/
        }
    }

    /**
     * Handles the NPC interaction.
     *
     * @param player         the player.
     * @param optionIndexthe option index.
     * @param index          the index.
     */
    private static void handleNPCInteraction(Player player, int optionIndex, final int index) {
        if (index < 1 || index > ServerConstants.MAX_NPCS) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            return;
        }
        final NPC npc = Repository.getNpcs().get(index);
        if (npc == null) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            return;
        }
        if (player.getAttribute("removenpc", false)) {
            npc.clear();
            player.getPacketDispatch().sendMessage("Removed npc=" + npc.toString());
            return;
        }
        NPC shown = npc.getShownNPC(player);
        final Option option = shown.getInteraction().get(optionIndex);
        if (option == null) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            Interaction.handleInvalidInteraction(player, npc, Option.NULL);
            return;
        }
        player.debug("NPC Interacting with \"" + shown.getUsername() + "\" [index=" + index + ", renderable=" + npc.isRenderable() + "]");
        player.debug("option=" + option.getName() + ", slot=" + option.getIndex() + ", id=" + shown.getId() + " original=" + npc.getId() + ", location=" + npc.getLocation() + ".");
        handleAIPLegion(player, 0, optionIndex, index);
        npc.getInteraction().handle(player, option);
    }

    /**
     * Handles object interaction
     *
     * @param player      The interacting player.
     * @param optionIndex The option index.
     * @param x           The x-coordinate of the object.
     * @param y           The y-coordinate of the object.
     * @param objectId    The object id.
     */
    private static void handleObjectInteraction(final Player player, int optionIndex, int x, int y, int objectId) {
        GameObject object = RegionManager.getObject(player.getLocation().getZ(), x, y);
        if (objectId == 29735) {// plate safety.
            player.getPulseManager().run(new MovementPulse(player, Location.create(x, y, player.getLocation().getZ())) {
                @Override
                public boolean pulse() {
                    player.getDialogueInterpreter().sendDialogue("There appears to be a tunnel behind the poster.");
                    player.getDialogueInterpreter().addAction(new DialogueAction() {

                        @Override
                        public void handle(Player player, int buttonId) {
                            player.teleport(new Location(3140, 4230, 2));
                        }

                    });
                    return true;
                }
            }, "movement");
            return;
        }
        if (object == null || object.getId() != objectId) {
            player.debug("GameObject(" + objectId + ") interaction was " + object + " at location " + x + ", " + y + ".");
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            Interaction.handleInvalidInteraction(player, object, Option.NULL);
            return;
        }
        if (!object.isActive()) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            Interaction.handleInvalidInteraction(player, object, Option.NULL);
            return;
        }
        object = object.getChild(player);
        Option option = object.getInteraction().get(optionIndex);
        if (option == null) {
            player.debug("Invalid option" + object + ", original: " + objectId + ".");
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            Interaction.handleInvalidInteraction(player, object, Option.NULL);
            return;
        }
        player.debug(object + ", original=" + objectId + ", option=" + option.getName() + "");
        player.debug("dir=" + object.getDirection());
        if (option.getHandler() != null) {
            player.debug("Object handler: " + option.getHandler().getClass().getSimpleName());
        }
        handleAIPLegion(player, 1, optionIndex, x, y, objectId);
        object.getInteraction().handle(player, option);
    }

    /**
     * Handles player interaction.
     *
     * @param playerThe   player interacting.
     * @param optionIndex The option index.
     * @param index       The target index.
     */
    private static void handlePlayerInteraction(Player player, int optionIndex, int index) {
        if (index < 1 || index > ServerConstants.MAX_PLAYERS) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            return;
        }
        final Player target = Repository.getPlayers().get(index);
        if (target == null || !target.isActive()) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            return;
        }
        final Option option = player.getInteraction().get(optionIndex);
        if (option == null) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            return;
        }
        handleAIPLegion(player, 2, optionIndex, index);
        target.getInteraction().handle(player, option);
    }

    /**
     * Handles the ground item interaction.
     *
     * @param player      The player.
     * @param index       The index of the action.
     * @param itemId      The item id.
     * @param locationThe location of the item.
     */
    private static void handleGroundItemInteraction(final Player player, int index, int itemId, Location location) {
        final GroundItem item = GroundItemManager.get(itemId, location, player);
        if (item == null) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            return;
        }
        final Option option = item.getInteraction().get(index);
        if (option == null) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            Interaction.handleInvalidInteraction(player, item, Option.NULL);
            return;
        }
        item.getInteraction().handle(player, option);
    }

    /**
     * Handles the AIPlayer legion.
     *
     * @param player The player.
     * @param type   The interaction type.
     * @param args   The arguments.
     */
    private static void handleAIPLegion(Player player, int type, int... args) {
        if (player.isArtificial()) {
            List<AIPlayer> legion = player.getAttribute("aip_legion");
            if (legion != null) {
                for (AIPlayer aip : legion) {
                    if (aip != player) {
                        switch (type) {
                            case 0:
                                handleNPCInteraction(aip, args[0], args[1]);
                                break;
                            case 1:
                                handleObjectInteraction(aip, args[0], args[1], args[2], args[3]);
                                break;
                            case 2:
                                handlePlayerInteraction(aip, args[0], args[1]);
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Gets the player instance (used for AIP controlling).
     *
     * @param player The player.
     * @return The player instance, or the AIP when the player is controlling an
     * AIP.
     */
    private static Player getPlayer(Player player) {
        AIPlayer aip = player.getAttribute("aip_select");
        if (aip != null && aip.getLocation().withinDistance(player.getLocation())) {
            return aip;
        }
        return player;
    }

}