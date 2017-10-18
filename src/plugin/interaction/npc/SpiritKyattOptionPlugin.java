package plugin.interaction.npc;

import org.arios.cache.def.impl.NPCDefinition;
import org.arios.cache.def.impl.ObjectDefinition;
import org.arios.game.interaction.OptionHandler;
import org.arios.game.node.Node;
import org.arios.game.node.entity.player.Player;
import org.arios.game.world.map.Location;
import org.arios.game.world.update.flag.context.Animation;
import org.arios.plugin.Plugin;

/**
 * Represents the plugin used to handle the spirit kyatt familiar
 * @author Splinter
 */
public final class SpiritKyattOptionPlugin extends OptionHandler {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	NPCDefinition.forId(7365).getConfigurations().put("option:interact", this);
	ObjectDefinition.forId(28741).getConfigurations().put("option:open", this);
	ObjectDefinition.forId(28743).getConfigurations().put("option:climb-up", this);
	return this;
    }

    @Override
    public boolean handle(final Player player, Node node, String option) {
	if(node.getId() == 7365){
	    player.getDialogueInterpreter().open(7365, node.asNpc());
	}
	if(node.getId() == 28741){
	    player.animate(new Animation(827));
	    player.teleport(new Location(2333, 10015));
	}
	if(node.getId() == 28743){
	    player.animate(new Animation(828));
	    player.teleport(new Location(2328, 3646));
	}
	return true;
    }

}
