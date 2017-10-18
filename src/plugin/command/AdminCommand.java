package plugin.command;

import org.arios.game.content.skill.Skills;
import org.arios.game.content.skill.member.farming.wrapper.PatchWrapper;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.IronmanMode;
import org.arios.game.node.entity.player.link.SpellBookManager.SpellBook;
import org.arios.game.node.entity.state.EntityState;
import org.arios.game.system.command.CommandPlugin;
import org.arios.game.system.command.CommandSet;
import org.arios.game.world.repository.Repository;
import org.arios.plugin.Plugin;

/**
 * Handles administrator commands.
 * @author Vexia
 */
public final class AdminCommand extends CommandPlugin {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	link(CommandSet.ADMINISTRATOR);
	return this;
    }

    @Override
    public boolean parse(final Player player, String name, String[] args) {
	Player p = null;
	switch (name) {
	case "lunar":
	    player.getSpellBookManager().setSpellBook(SpellBook.LUNAR);
	    player.getSpellBookManager().update(player);
	    break;
	case "conv":
	    Player target;
	    target = Repository.getPlayerByDisplay(args[1]);
	    target.getIronmanManager().setMode(IronmanMode.STANDARD);
	    player.sendMessage("ok m8!");
	    break;
	case "special":
	case "spec":
	    int amount = args.length > 1 ? Integer.parseInt(args[1]) : 100;
	    player.getSettings().setSpecialEnergy(amount);
	    return true;
	case "god":
	    player.setAttribute("godMode", !player.getAttribute("godMode", false));
	    player.sendMessage("God mode=<col=ff0000>" + player.getAttribute("godMode", false));
	    break;
	case "aggro":
	    player.setAttribute("ignore_aggression", !player.getAttribute("ignore_aggression", false));
	    player.sendMessage("Ignore NPC aggression=<col=ff0000>" + player.getAttribute("ignore_aggression", false));
	    break;
	case "heal":
	case "hp":
	case "life":
	    player.getSettings().setSpecialEnergy(100);
	    player.getSettings().updateRunEnergy(-100);
	    player.getSkills().setLifepointsIncrease(0);
	    player.getSkills().rechargePrayerPoints();
	    player.getSkills().heal(100);
	    player.getSkills().setLevel(Skills.SUMMONING, player.getSkills().getStaticLevel(Skills.SUMMONING));
	    player.getStateManager().remove(EntityState.TELEBLOCK);
	    if (player.getFamiliarManager().hasFamiliar()) {
		player.getFamiliarManager().getFamiliar().updateSpecialPoints(-200);
	    }
	    return true;
	case "setinvisible":
	case "seti":
	    player.setInvisible(Boolean.parseBoolean(args[1]));
	    player.sendMessage("invisible=<col=ff0000>" + player.isInvisible());
	    return true;
	case "bank":
	    player.getBank().open();
	    return true;
	case "clearpatches":
	    p = getTarget(args[1]);
	    if (p == null || !p.isActive()) {
		player.sendMessage("Sorry, " + args[1] + " is not online.");
		return true;
	    }
	    for (PatchWrapper wrapper : p.getFarmingManager().getPatches()) {
		if (wrapper != null) {
		    wrapper.addConfigValue(0);
		}
	    }
	    p.sendMessage("Your patches were cleared.");
	    player.sendMessage("Cleared " + args[1] + " patches.");
	    return true;
	}
	return false;
    }

}
