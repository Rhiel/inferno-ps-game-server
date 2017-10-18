package plugin.skill.fletching;

import org.arios.game.content.skill.member.fletching.items.bolts.Bolt;
import org.arios.game.interaction.NodeUsageEvent;
import org.arios.game.interaction.UseWithHandler;
import org.arios.plugin.Plugin;

/**
 * Represents the bolt creating plugin.
 * @author 'Vexia
 * @version 1.0
 */
public final class BoltCreatePlugin extends UseWithHandler {

    /**
     * Constructs a new {@code BoltCreatePlugin} {@code Object}.
     */
    public BoltCreatePlugin() {
	super(314);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	for (Bolt bolt : Bolt.values()) {
	    addHandler(bolt.getItem().getId(), ITEM_TYPE, this);
	}
	return this;
    }

    @Override
    public boolean handle(NodeUsageEvent event) {
	event.getPlayer().getDialogueInterpreter().open(83234, event.getUsedItem(), event.getBaseItem());
	return true;
    }

}
