package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.content.skill.free.crafting.TanningProduct;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.RunScript;
import org.arios.game.node.item.Item;
import org.arios.plugin.Plugin;

/**
 * @author 'Vexia
 */
public class TanningInterface extends ComponentPlugin {

    /**
     * Method used to create a new instance.
     * @param arg
     * @return
     * @throws Throwable
     */
    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	ComponentDefinition.put(324, this);
	return this;
    }

    @Override
    public boolean handle(Player player, Component component, int opcode, int button, int slot, int itemId) {
	TanningProduct def = null;
	switch (button) {
	case 148:
	case 140:
	case 132:
	case 124:
	    def = TanningProduct.SOFT_LEATHER;
	    break;
	case 149:
	case 141:
	case 133:
	case 125:
	    def = TanningProduct.HARD_LEATHER;
	    break;
	case 150:
	case 142:
	case 134:
	case 126:
	    def = TanningProduct.SNAKESKIN;
	    break;
	case 151:
	case 143:
	case 135:
	case 127:
	    def = TanningProduct.SNAKESKIN2;
	    break;
	case 152:
	case 144:
	case 136:
	case 128:
	    def = TanningProduct.GREEN_DHIDE;
	    break;
	case 153:
	case 145:
	case 137:
	case 129:
	    def = TanningProduct.BLUEDHIDE;
	    break;
	case 154:
	case 146:
	case 138:
	case 130:
	    def = TanningProduct.REDDHIDE;
	    break;
	case 155:
	case 147:
	case 139:
	case 131:
	    def = TanningProduct.BLACKDHIDE;
	    break;
	}
	if (def == null) {
	    return true;
	}
	int amount = 0;
	if (button >= 148 && button <= 155) {
	    amount = 1;
	}
	if (button >= 140 && button <= 147) {
	    amount = 5;
	}
	final TanningProduct deff = def;
	if (button >= 132 && button <= 139) {
	    player.setAttribute("runscript", new RunScript() {
		@Override
		public boolean handle() {
		    int amt = (int) getValue();
		    TanningProduct.tan(player, amt, deff);
		    return true;
		}
	    });
	    player.getDialogueInterpreter().sendInput(false, "Enter amount:");
	    return true;
	}
	if (button >= 124 && button <= 131) {
	    amount = player.getInventory().getAmount(new Item(def.getItem(), 1));
	}
	TanningProduct.tan(player, amount, def);
	return true;
    }

}
