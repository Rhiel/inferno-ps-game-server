package plugin.skill.fletching;

import org.arios.game.content.skill.member.fletching.items.gem.GemBolt;
import org.arios.game.interaction.NodeUsageEvent;
import org.arios.game.interaction.UseWithHandler;
import org.arios.plugin.Plugin;

/**
 * Represents the plugin used to handle gem bolt making.
 * @author 'Vexia
 * @version 1.0
 */
public class BoltGemPlugin extends UseWithHandler {

    /**
     * Constructs a new {@code BoltGemPlugin} {@code Object}.
     */
    public BoltGemPlugin() {
	super(45, 46, 9187, 9188, 9189, 9190, 9191, 9192, 9193, 9194);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	for (GemBolt bolt : GemBolt.values()) {
	    addHandler(bolt.getBase().getId(), ITEM_TYPE, this);
	}
	return this;
    }

    @Override
    public boolean handle(NodeUsageEvent event) {
	event.getPlayer().getDialogueInterpreter().open(238743, event.getUsedItem(), event.getBaseItem());
	return true;
    }

}
