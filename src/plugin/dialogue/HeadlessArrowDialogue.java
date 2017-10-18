package plugin.dialogue;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.skill.member.fletching.items.arrow.HeadlessArrowPulse;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;

/**
 * Represents the diaogue plugin used for the headless arrow dialogue.
 * @author 'Vexia
 * @version 1.0
 */
public final class HeadlessArrowDialogue extends DialoguePlugin {

    /**
     * Represents the headless arrow item.
     */
    private static final Item HEADLESS_ARROW = new Item(53);

    /**
     * Represents the item parameter.
     */
    private Item item;

    /**
     * Constructs a new {@code HeadlessArrowDialogue} {@code Object}.
     */
    public HeadlessArrowDialogue() {
	/**
	 * empty.
	 */
    }

    /**
     * Constructs a new {@code HeadlessArrowDialogue} {@code Object}.
     * @param player the player.
     */
    public HeadlessArrowDialogue(Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new HeadlessArrowDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
	item = (Item) args[0];
	player.getInterfaceManager().openChatbox(582);
	player.getPacketDispatch().sendString("<br><br><br><br>" + HEADLESS_ARROW.getName(), 582, 5);
	player.getPacketDispatch().sendItemZoomOnInterface(HEADLESS_ARROW.getId(), 160, 582, 2);
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
	player.getPulseManager().run(new HeadlessArrowPulse(player, item, amount));
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 9043323 };
    }
}