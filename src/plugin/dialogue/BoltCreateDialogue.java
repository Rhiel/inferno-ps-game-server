package plugin.dialogue;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.skill.member.fletching.items.bolts.Bolt;
import org.arios.game.content.skill.member.fletching.items.bolts.BoltPulse;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;

/**
 * Represents the dialogue plugin used for the bolt creating.
 * @author 'Vexia
 * @version 1.0
 */
public final class BoltCreateDialogue extends DialoguePlugin {

    /**
     * Represents the first item param.
     */
    private Item item;

    /**
     * Represents the second item param.
     */
    private Item second;

    /**
     * Represents the bolt.
     */
    private Bolt bolt;

    /**
     * Constructs a new {@code BoltCreateDialogue} {@code Object}.
     */
    public BoltCreateDialogue() {
	/**
	 * empty.
	 */
    }

    /**
     * Constructs a new {@code BoltCreateDialogue} {@code Object}.
     * @param player the player.
     */
    public BoltCreateDialogue(Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new BoltCreateDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
	item = (Item) args[0];
	second = (Item) args[1];
	bolt = Bolt.forItem(item.getName().toLowerCase().contains("feather") ? second : item);
	player.getInterfaceManager().openChatbox(582);
	player.getPacketDispatch().sendString("<br><br><br><br>" + bolt.getProduct().getName(), 582, 5);
	player.getPacketDispatch().sendItemZoomOnInterface(bolt.getProduct().getId(), 160, 582, 2);
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
	player.getPulseManager().run(new BoltPulse(player, item, bolt, amount));
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 83234 };
    }
}