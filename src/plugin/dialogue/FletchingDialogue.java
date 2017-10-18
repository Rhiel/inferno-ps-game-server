package plugin.dialogue;

import org.arios.game.component.Component;
import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.skill.member.fletching.FletchItem;
import org.arios.game.content.skill.member.fletching.FletchType;
import org.arios.game.content.skill.member.fletching.FletchingPulse;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.RunScript;
import org.arios.game.node.item.Item;

/**
 * Represents the dialogue to make a generic fletching item.
 * @author 'Vexia
 * @version 1.0
 */
public final class FletchingDialogue extends DialoguePlugin {

    /**
     * Represents the first item used.
     */
    private Item item;

    /**
     * Represents the fletch type.
     */
    private FletchType type;

    /**
     * Constructs a new {@code FletchingDialogue} {@code Object}.
     */
    public FletchingDialogue() {
	/**
	 * empty.
	 */
    }

    /**
     * Constructs a new {@code FletchingDialogue} {@code Object}.
     * @param player the player.
     */
    public FletchingDialogue(Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new FletchingDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
	item = (Item) args[0];
	type = FletchType.forItem(item);
	final Component component = type.getComponent();
	player.getInterfaceManager().openChatbox(component);
	if (component.getId() != 309) {
	    for (int i = 0; i < type.getItems().length; i++) {
		player.getPacketDispatch().sendItemZoomOnInterface(type.getItems()[i].getProduct().getId(), 160, component.getId(), 2 + i);
		player.getPacketDispatch().sendString("<br><br><br><br>" + type.getItems()[i].getProduct().getName(), component.getId(), (component.getId() - 296) + (i * 4));
	    }
	} else {
	    player.getPacketDispatch().sendString("<br><br><br><br>" + type.getItems()[0].getProduct().getName(), 309, 6);
	    player.getPacketDispatch().sendItemZoomOnInterface(type.getItems()[0].getProduct().getId(), 175, 309, 2);
	}
	return true;
    }

    @Override
    public boolean handle(int interfaceId, int button) {
	int amount = 0;
	int index = 0;
	switch (interfaceId) {
	case 305:// log
	    switch (button) {
	    case 9:
		if (button == 9) {
		    amount = 1;
		}
	    case 8:
		if (button == 8) {
		    amount = 5;
		}
	    case 7:
		if (button == 7) {
		    amount = 10;
		}
	    case 6:
		if (button == 6) {
		    amount = -1;
		}
		index = 0;
		break;
	    case 13:
		amount = 1;
	    case 12:
		if (button == 12) {
		    amount = 5;
		}
	    case 11:
		if (button == 11) {
		    amount = 10;
		}
	    case 10:
		if (button == 10) {
		    amount = -1;
		}
		index = 1;
		break;
	    case 17:
		amount = 1;
	    case 16:
		if (button == 16) {
		    amount = 5;
		}
	    case 15:
		if (button == 15) {
		    amount = 10;
		}
	    case 14:
		if (button == 14) {
		    amount = -1;
		}
		index = 2;
		break;
	    case 21:
		if (button == 21) {
		    amount = 1;
		}
	    case 20:
		if (button == 20) {
		    amount = 5;
		}
	    case 19:
		if (button == 19) {
		    amount = 10;
		}
	    case 18:
		if (button == 18) {
		    amount = -1;
		}
		index = 3;
		break;
	    }
	    break;
	case 304:
	    switch (button) {
	    case 8:
		if (button == 8) {
		    amount = 1;
		}
	    case 7:
		if (button == 7) {
		    amount = 5;
		}
	    case 6:
		if (button == 6) {
		    amount = 10;
		}
	    case 5:
		if (button == 5) {
		    amount = -1;
		}
		index = 0;
		break;
	    case 9:
		if (button == 9) {
		    amount = -1;
		}
	    case 10:
		if (button == 10) {
		    amount = 10;
		}
	    case 11:
		if (button == 11) {
		    amount = 5;
		}
	    case 12:
		if (button == 12) {
		    amount = 1;
		}
		index = 1;
		break;
	    case 16:
		if (button == 16) {
		    amount = 1;
		}
	    case 15:
		if (button == 15) {
		    amount = 5;
		}
	    case 14:
		if (button == 14) {
		    amount = 10;
		}
	    case 13:
		if (button == 13) {
		    amount = -1;
		}
		index = 2;
		break;
	    }
	    break;
	case 303:
	    switch (button) {
	    case 7:
		if (button == 7) {
		    amount = 1;
		}
	    case 6:
		if (button == 6) {
		    amount = 5;
		}
	    case 5:
		if (button == 5) {
		    amount = 10;
		}
	    case 4:
		if (button == 4) {
		    amount = -1;
		}
		index = 0;
		break;
	    case 11:
		if (button == 11) {
		    amount = 1;
		}
	    case 10:
		if (button == 10) {
		    amount = 5;
		}
	    case 9:
		if (button == 9) {
		    amount = 10;
		}
	    case 8:
		if (button == 8) {
		    amount = -1;
		}
		index = 1;
		break;
	    }
	    break;
	case 309:
	    switch (button) {
	    case 6:
		if (button == 6) {
		    amount = 1;
		}
	    case 5:
		if (button == 5) {
		    amount = 5;
		}
	    case 4:
		if (button == 4) {
		    amount = -1;
		}
	    case 3:
		if (button == 3) {
		    amount = player.getInventory().getAmount(type.getLog());
		}
		index = 0;
		break;
	    }
	    break;
	}
	final FletchItem fletch = type.getItems()[index];
	fletch.setType(type);
	if (amount == -1) {
	    player.setAttribute("runscript", new RunScript() {
		@Override
		public boolean handle() {
		    end();
		    player.getPulseManager().run(new FletchingPulse(player, item, (int) getValue(), fletch));
		    return true;
		}
	    });
	    player.getDialogueInterpreter().sendInput(false, "Enter amount:");
	    return true;
	}
	end();
	player.getPulseManager().run(new FletchingPulse(player, item, amount, fletch));
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 98327 };
    }

}