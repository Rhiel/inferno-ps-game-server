package plugin.quest.dtreasure;

import org.arios.game.content.global.quest.Quest;
import org.arios.game.interaction.OptionHandler;
import org.arios.game.node.Node;
import org.arios.game.node.entity.player.Player;
import org.arios.plugin.Plugin;

/**
 * Handles the options related to the desert treasure quest.
 * @author Aero
 * @version 1.0
 */
public final class DesertTreasurePlugin extends OptionHandler {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	return this;
    }

    @Override
    public boolean handle(Player player, Node node, String option) {
	@SuppressWarnings("unused")
	final Quest quest = player.getQuestRepository().getQuest("Desert Treasure");
	return true;
    }

}