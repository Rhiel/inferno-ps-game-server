package plugin.skill.construction.decorations;

import org.arios.cache.def.impl.ObjectDefinition;
import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.interaction.OptionHandler;
import org.arios.game.node.Node;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;
import org.arios.plugin.Plugin;
import org.arios.plugin.PluginManager;


/**
 * Handles the shelves in the kitchen room.
 * @author Splinter
 */
public final class ShelfPlugin extends OptionHandler {


    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	PluginManager.definePlugin(new ShelfDialogue());
	for(int i = 13545; i < 13552; i++){
	    ObjectDefinition.forId(i).getConfigurations().put("option:search", this);   
	}
	return this;
    }
    @Override
    public boolean handle(Player player, Node node, String option) {
	player.getDialogueInterpreter().open(778341, node.getId());
	return true;
    }

    /**
     * Dialogue options for the shelves.
     * @author Splinter
     * @version 1.0
     */
    public final class ShelfDialogue extends DialoguePlugin {

        /**
         * Constructs a new {@code ShelfDialogue} {@code Object}.
         */
        public ShelfDialogue() {
    		/**
    		 * empty.
    		 */
        }

	/**
	 * Constructs a new {@code ShelfDialogue} {@code Object}.
	 * @param player the player.
	 */
	public ShelfDialogue(Player player) {
	    super(player);
	}

	@Override
	public DialoguePlugin newInstance(Player player) {
	    return new ShelfDialogue(player);
	}

	@Override
	public boolean open(Object... args) {
	    int id = (int) args[0];
	    String[] wood = {"Select an Option", "Kettle", "Teapot", "Clay cup"};
	    String[] oak = {"Select an Option", "Kettle", "Teapot"};
	    String[] teak = {"Select an Option", "Kettle", "Teapot", "Porcelain cup", "Empty beer glass", "More Options"};
	    switch(id){
	    case 13545://wood 1
		interpreter.sendOptions(wood[0], wood[1], wood[2], wood[3]);
		stage = 1;
		break;
	    case 13546://wood 2
		interpreter.sendOptions(wood[0], wood[1], wood[2], wood[3], "Empty beer glass");
		stage = 1;
		break;
	    case 13547://wood 3
		interpreter.sendOptions(wood[0], wood[1], wood[2], wood[3], "Empty beer glass", "Cake tin");
		stage = 1;
		break;
	    case 13548://oak 1
		interpreter.sendOptions(oak[0], oak[1], oak[2], "Clay cup", "Empty beer glass" ,"Bowl");
		stage = 2;
		break;
	    case 13549://oak 2
		interpreter.sendOptions(oak[0], oak[1], oak[2], "Porcelain cup", "Empty beer glass", "More Options");
		stage = 3;
		break;
	    case 13550://teak 1
	    case 13551:
		interpreter.sendOptions(teak[0], teak[1], teak[2], teak[3], teak[4], teak[5]);
		stage = 5;
		break;
	    }
	    return true;
	}

	@Override
	public boolean handle(int interfaceId, int buttonId) {
	    if(player.getInventory().freeSlots() < 1){
		player.sendMessage("You need at least one free inventory space to take from the shelves.");
		return true;
	    }
	    switch(stage){
	    case 1://all wood shelves
		switch (buttonId) {
		case 1:
		    end();
		    player.getInventory().add(new Item(7688, 1));
		    break;
		case 2:
		    end();
		    player.getInventory().add(new Item(7702, 1));
		    break;
		case 3:
		    end();
		    player.getInventory().add(new Item(7728, 1));
		    break;
		case 4:
		    end();
		    player.getInventory().add(new Item(1919, 1));
		    break;
		case 5:
		    end();
		    player.getInventory().add(new Item(1887, 1));
		    break;
		}
		break;
	    case 2://Oak shelf #1
		switch (buttonId) {
		case 1:
		    end();
		    player.getInventory().add(new Item(7688, 1));
		    break;
		case 2:
		    end();
		    player.getInventory().add(new Item(7702, 1));
		    break;
		case 3:
		    end();
		    player.getInventory().add(new Item(7728, 1));
		    break;
		case 4:
		    end();
		    player.getInventory().add(new Item(1919, 1));
		    break;
		case 5:
		    end();
		    player.getInventory().add(new Item(1923, 1));
		    break;
		}
		break;
	    case 3://Oak shelves #2 only
		switch (buttonId) {
		case 1:
		    end();
		    player.getInventory().add(new Item(7688, 1));
		    break;
		case 2:
		    end();
		    player.getInventory().add(new Item(7702, 1));
		    break;
		case 3:
		    end();
		    player.getInventory().add(new Item(4244, 1));
		    break;
		case 4:
		    end();
		    player.getInventory().add(new Item(1919, 1));
		    break;
		case 5:
		    interpreter.sendOptions("Select an Option", "Bowl", "Cake tin");
		    stage = 4;
		    break;
		}
		break;
	    case 4://Oak shelves #2 only
		switch (buttonId) {
		case 1:
		    end();
		    player.getInventory().add(new Item(1923, 1));
		    break;
		case 2:
		    end();
		    player.getInventory().add(new Item(1887, 1));
		    break;
		}
	    case 5://teak shelves
		switch (buttonId) {
		case 1:
		    end();
		    player.getInventory().add(new Item(7688, 1));
		    break;
		case 2:
		    end();
		    player.getInventory().add(new Item(7702, 1));
		    break;
		case 3:
		    end();
		    player.getInventory().add(new Item(7735, 1));
		    break;
		case 4:
		    end();
		    player.getInventory().add(new Item(1919, 1));
		    break;
		case 5:
		    interpreter.sendOptions("Select an Option", "Bowl", "Pie dish", "Empty pot");
		    stage = 6;
		    break;
		}
		break;
	    case 6://teak shelves
		switch (buttonId) {
		case 1:
		    end();
		    player.getInventory().add(new Item(1923, 1));
		    break;
		case 2:
		    end();
		    player.getInventory().add(new Item(2313, 1));
		    break;
		case 3:
		    end();
		    player.getInventory().add(new Item(1931, 1));
		    break;
		}
		break;
	    }
	    return true;
	}

        @Override
        public int[] getIds() {
    		return new int[] { 778341 };
        }
    }
}