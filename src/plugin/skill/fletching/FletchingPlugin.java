package plugin.skill.fletching;

import org.arios.game.interaction.NodeUsageEvent;
import org.arios.game.interaction.UseWithHandler;
import org.arios.plugin.Plugin;

/**
 * Represents the plugin used to open the fletching dialogue.
 * @author 'Vexia
 * @version 1.0
 */
public class FletchingPlugin extends UseWithHandler {

    /**
     * Constructs a new {@code FletchingPlugin} {@code Object}.
     */
    public FletchingPlugin() {
	super(1511, 1521, 1519, 1517, 1515, 1513, 6332, 6333);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	addHandler(946, ITEM_TYPE, this);
	return this;
    }

    @Override
    public boolean handle(NodeUsageEvent event) {
	event.getPlayer().getDialogueInterpreter().open(98327, event.getUsedItem(), event.getBaseItem());
	return true;
    }

}
