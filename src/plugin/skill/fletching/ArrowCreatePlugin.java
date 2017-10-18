package plugin.skill.fletching;

import org.arios.game.content.skill.member.fletching.items.arrow.ArrowHead;
import org.arios.game.interaction.NodeUsageEvent;
import org.arios.game.interaction.UseWithHandler;
import org.arios.plugin.Plugin;

/**
 * Represents the plugin used to create arrows.
 * @author 'Vexia
 * @version 1.0
 */
public class ArrowCreatePlugin extends UseWithHandler {

    /**
     * Constructs a new {@code ArrowCreatePlugin} {@code Object}.
     */
    public ArrowCreatePlugin() {
	super(314, 53);
    }

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	addHandler(52, ITEM_TYPE, this);
	for (ArrowHead head : ArrowHead.values()) {
	    addHandler(head.getTips().getId(), ITEM_TYPE, this);
	}
	return this;
    }

    @Override
    public boolean handle(NodeUsageEvent event) {
	if (event.getUsedItem().getId() == 314 && event.getBaseItem().getId() == 52 || event.getUsedItem().getId() == 52 && event.getBaseItem().getId() == 314) {
	    event.getPlayer().getDialogueInterpreter().open(9043323, event.getUsedItem(), event.getBaseItem());
	} else {
	    event.getPlayer().getDialogueInterpreter().open(9823821, event.getUsedItem(), event.getBaseItem());
	}
	return true;
    }

}
