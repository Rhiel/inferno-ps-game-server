package org.arios.net.packet.in;

import org.arios.game.interaction.NodeUsageEvent;
import org.arios.game.interaction.UseWithHandler;
import org.arios.game.node.entity.npc.NPC;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.game.world.repository.Repository;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;

/**
 * Handles item used on entity packet.
 *
 * @author Emperor
 */
public class ItemUseOnPacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
        if (player.getLocks().isInteractionLocked()) {
            return;
        }
        switch (buffer.opcode()) {
            case 58: // Item on NPC
                int itemId = buffer.getLEShort();
                int interfaceId = buffer.getIntA() >> 16;
                int slotId = buffer.getShortA();
                int npcIndex = buffer.getLEShort();
                NPC npc = Repository.getNpcs().get(npcIndex);
                Item item = player.getInventory().get(slotId);
                if (item == null || item.getId() != itemId) {
                    return;
                }
                NodeUsageEvent event = new NodeUsageEvent(player, interfaceId, item, npc);
                UseWithHandler.run(event);
                break;
            case 61: // Item on Player
                interfaceId = buffer.getInt() >> 16;
                itemId = buffer.getShort();
                slotId = buffer.getLEShortA();
                int playerIndex = buffer.getShortA();
                Player target = Repository.getPlayers().get(playerIndex);
                item = player.getInventory().get(slotId);
                if (target == null || item == null || item.getId() != itemId) {
                    return;
                }
                event = new NodeUsageEvent(player, interfaceId, item, target);
                UseWithHandler.run(event);
                break;
        }
    }

}
