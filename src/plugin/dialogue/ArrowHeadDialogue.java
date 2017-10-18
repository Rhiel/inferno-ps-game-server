package plugin.dialogue;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.skill.member.fletching.items.arrow.ArrowHead;
import org.arios.game.content.skill.member.fletching.items.arrow.ArrowHeadPulse;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;

/**
 * Represents the arrow head dialogue plugin.
 * @author 'Vexia
 * @version 1.0
 */
public final class ArrowHeadDialogue extends DialoguePlugin {

    /**
     * Represents the item param.
     */
    private Item item;

    /**
     * Represents the second item param.
     */
    private Item second;

    /**
     * Represents the arrow head type.
     */
    private ArrowHead arrow;

    /**
     * Constructs a new {@code ArrowHeadDialogue} {@code Object}.
     */
    public ArrowHeadDialogue() {
	/**
	 * empty.
	 */
    }

    /**
     * Constructs a new {@code ArrowHeadDialogue} {@code Object}.
     * @param player the player.
     */
    public ArrowHeadDialogue(Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new ArrowHeadDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
	item = (Item) args[0];
	second = (Item) args[1];
	arrow = ArrowHead.forItem(item.getName().toLowerCase().contains("tip") ? item : second);
	if (arrow == null) {
	    player.getPacketDispatch().sendMessage("Nothing interesting happens.");
	    return false;
	}
	player.getInterfaceManager().openChatbox(582);
	player.getPacketDispatch().sendString("<br><br><br><br>" + arrow.getProduct().getName(), 582, 5);
	player.getPacketDispatch().sendItemZoomOnInterface(arrow.getProduct().getId(), 160, 582, 2);
	return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
	end();
	int amount = 0;
	switch (buttonId) {
	case 4:
	    amount = 5;
	    break;
	case 5:
	    amount = 1;
	    break;
	case 3:
	    amount = 10;
	    break;
	}
	player.getPulseManager().run(new ArrowHeadPulse(player, item, arrow, amount));
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 9823821 };
    }
}