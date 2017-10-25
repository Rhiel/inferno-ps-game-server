package org.arios.net.packet.in;

import java.util.Arrays;

import org.arios.cache.Cache;
import org.arios.cache.def.impl.ItemDefinition;
import org.arios.cache.def.impl.NPCDefinition;
import org.arios.cache.def.impl.ObjectDefinition;
import org.arios.game.node.entity.player.Player;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;
import org.arios.tools.StringUtils;

/**
 * Handles the incoming examine packets.
 *
 * @author Emperor
 */
public final class ExaminePacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
        String name;
        switch (buffer.opcode()) {
            case 88: // Object examine
                int id = buffer.getShortA();
                if (id < 0 || id > Cache.getObjectDefinitionsSize()) {
                    break;
                }
                ObjectDefinition d = ObjectDefinition.forId(id);
                name = d.getName();
                player.debug("Object id: " + id + ", " + (d.getModelIds() != null ? Arrays.toString(d.getModelIds()) : null) + ", " + d.animationId);
                player.getPacketDispatch().sendMessage("It's a" + (StringUtils.isPlusN(name) ? "n " : " ") + name + ".");
                break;
            //case 235:
            case 158: // Item examine
                id = buffer.getLEShort();
                if (id < 0 || id > Cache.getItemDefinitionsSize()) {
                    break;
                }
                // System.out.println("Item: " + id + ", name: " +
                // ItemDefinition.forId(id).getName());
                player.getPacketDispatch().sendMessage(getItemExamine(id));
                break;
            case 237: // NPC examine
                id = buffer.getShort();
                if (id < 0 || id > Cache.getNPCDefinitionsSize()) {
                    break;
                }
                player.debug("NPC id: " + id + ".");
                NPCDefinition def = NPCDefinition.forId(id);
                if (def == null) {
                    break;
                }
                player.getPacketDispatch().sendMessage(def.getExamine());
                break;
        }
    }

    /**
     * Gets the item examine.
     *
     * @param id the id.
     * @return the name.
     */
    public static String getItemExamine(int id) {
        if (id == 995) {
            return "Lovely money!";
        }
        if (ItemDefinition.forId(id).getExamine().length() == 255) {
            return "A set of instructions to be followed.";
        }
        return ItemDefinition.forId(id).getExamine();
    }
}