package org.arios.net.packet.in;

import org.arios.game.interaction.MovementPulse;
import org.arios.game.node.entity.Entity;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.ai.AIPlayer;
import org.arios.game.world.map.Location;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.PlayerContext;
import org.arios.net.packet.out.ClearMinimapFlag;

/**
 * Handles an incoming walk packet.
 *
 * @author Emperor
 */
public final class WalkPacket implements IncomingPacket {

    @Override
    public void decode(Player player, int opcode, IoBuffer buffer) {
        player = getPlayer(player);
        if (player == null) {
            return;
        }
        if (player.getLocks().isMovementLocked() || !player.getInterfaceManager().close()|| !player.getInterfaceManager().closeSingleTab() || !player.getDialogueInterpreter().close()) {
            PacketRepository.send(ClearMinimapFlag.class, new PlayerContext(player));
            player.debug("[WalkPacket] did not handle - [locked=" + player.getLocks().isMovementLocked() + "]!");
            return;
        }
//		player.getProperties().setSpell(null);
       // player.getInterfaceManager().close();
      //  player.getInterfaceManager().closeChatbox();
        if(opcode == 99) {
            int remaining = buffer.get();
            boolean running = buffer.getA() == 1;
            int x = buffer.getLEShortA();
            int y = buffer.getShort();
            System.out.println("x: " + x + ", " + y);
            // player.face((Entity) null);
            player.faceLocation(player.getLocation());
            player.getWalkingQueue().reset(running);
            player.getPulseManager().run(new MovementPulse(player, Location.create(x, y, player.getLocation().getZ()), running) {
                @Override
                public boolean pulse() {
                    return true;
                }
            }, "movement");
        }
        if (opcode == 116) {
            buffer.get(); // The x-coordinate of where we clicked on the minimap.
            buffer.get(); // The y-coordinate of where we clicked on the minimap.
            buffer.get();
            buffer.getShort(); // The rotation of the minimap.
            buffer.get(); // Always 57.
            buffer.get();
            buffer.getShort();
            buffer.get();
            buffer.get(); // Always 89
            buffer.get();
            buffer.get();
            buffer.getShort();
            buffer.getShort();
            buffer.get();
        }
    }

    /**
     * Gets the player instance (used for AIP controlling).
     * @param player The player.
     * @return The player instance, or the AIP when the player is controlling an AIP.
     */
    private static Player getPlayer(Player player) {
        if (player == null) {
            return null;
        }
        AIPlayer aip = player.getAttribute("aip_select");
        if (aip != null && aip.getLocation().withinDistance(player.getLocation())) {
            if (!player.getAttribute("aip_perm_select", true)) {
                player.removeAttribute("aip_select");
            }
            return aip;
        }
        return player;
    }

}