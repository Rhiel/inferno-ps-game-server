package plugin.skill.construction.decorations;

import org.arios.cache.def.impl.ObjectDefinition;
import org.arios.game.content.skill.Skills;
import org.arios.game.interaction.OptionHandler;
import org.arios.game.node.Node;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.game.node.object.GameObject;
import org.arios.game.node.object.ObjectBuilder;
import org.arios.plugin.Plugin;

/**
 * Handles the various fireplaces that you may light in Construction.
 * @author Splinter
 */
public final class FireplacePlugin extends OptionHandler {


    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	ObjectDefinition.forId(13609).getConfigurations().put("option:light", this);
	ObjectDefinition.forId(13611).getConfigurations().put("option:light", this);
	ObjectDefinition.forId(13613).getConfigurations().put("option:light", this);
	return this;
    }
    @Override
    public boolean handle(Player player, Node node, String option) {
	switch(option){
	case "light":
	    if(!player.getInventory().contains(1511, 1)|| !player.getInventory().contains(590, 1)){
		player.sendMessage("You must have a set of logs and a tinderbox to light the fireplace.");
		return true;
	    }
	    GameObject obj = (GameObject) node.asObject();
	    player.sendMessage("You light the fireplace.");
	    player.getInventory().remove(new Item(1511));
	    player.getSkills().addExperience(Skills.FIREMAKING, 80);
	    switch(obj.getId()){
	    case 13609:
		ObjectBuilder.replace(new GameObject(13609, obj.getLocation()), new GameObject(13610, obj.getLocation(), obj.getRotation()), 300);
		player.sendMessage("It will stay lit for three minutes.");
		break;
	    case 13611:
		ObjectBuilder.replace(new GameObject(13611, obj.getLocation()), new GameObject(13612, obj.getLocation(), obj.getRotation()), 500);
		player.sendMessage("It will stay lit for five minutes.");
		break;
	    case 13613:
		ObjectBuilder.replace(new GameObject(13613, obj.getLocation()), new GameObject(13614, obj.getLocation(), obj.getRotation()), 1000);
		player.sendMessage("It will stay lit for ten minutes.");
		break;
	    }
	    break;
	}
	return true;
    }
}