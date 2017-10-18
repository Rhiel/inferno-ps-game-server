package plugin.skill.construction.decorations;

import org.arios.cache.def.impl.ObjectDefinition;
import org.arios.game.interaction.OptionHandler;
import org.arios.game.node.Node;
import org.arios.game.node.entity.player.Player;
import org.arios.plugin.Plugin;

/**
 * Handles the interactions for the bell pulls which summon servants.
 * @author Splinter
 */
public final class BellPullPlugin extends OptionHandler {


    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	ObjectDefinition.forId(13574).getConfigurations().put("option:pull", this);
	ObjectDefinition.forId(13575).getConfigurations().put("option:pull", this);
	ObjectDefinition.forId(13576).getConfigurations().put("option:pull", this);
	return this;
    }
    @Override
    public boolean handle(Player player, Node node, String option) {
	//TODO: summon servant
	return true;
    }
}