package plugin.dialogue;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.EmoteData;
import org.arios.game.node.item.Item;

/**
 * Represents the dialogue plugin used for the box of health.
 * @author 'Vexia
 * @version 1.0
 */
public final class BoxOfHealth extends DialoguePlugin {

    /**
     * Represents the coins to recieve.
     */
    private static final Item COINS = new Item(995, 5000);

    /**
     * Constructs a new {@code BoxOfHealth} {@code Object}.
     */
    public BoxOfHealth() {
	/**
	 * empty.
	 */
    }

    /**
     * Constructs a new {@code BoxOfHealth} {@code Object}.
     * @param player the player.
     */
    public BoxOfHealth(Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new BoxOfHealth(player);
    }

    @Override
    public boolean open(Object... args) {
	interpreter.sendDialogue("The box hinges creak and appear to be forming audible words....");
	stage = 0;
	return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
	switch (stage) {
	case 0:
	    if (player.getInventory().freeSlots() == 0) {
		player.getPacketDispatch().sendMessage("You don't have enough inventory space.");
		end();
		break;
	    }
	    player.getSavedData().getGlobalData().getStrongHoldRewards()[2] = true;
	    interpreter.sendDialogue("...congratulations adventurer, you have been deemed worthy of this", "reward. You have also unlocked the Idea emote!");
	    stage = 1;
	    player.getInventory().add(COINS);
	    player.getEmotes().unlock(EmoteData.IDEA);
	    break;
	case 1:
	    end();
	    break;
	}
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 96878 };
    }

}