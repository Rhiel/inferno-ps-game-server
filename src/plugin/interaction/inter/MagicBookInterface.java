package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.content.skill.free.magic.MagicSpell;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.SpellBookManager.SpellBook;
import org.arios.game.world.GameWorld;
import org.arios.plugin.Plugin;

/**
 * Represents the magic book interface handling of non-combat spells.
 *
 * @author 'Vexia
 * @version 1.0
 */
public final class MagicBookInterface extends ComponentPlugin {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ComponentDefinition.put(218, this);
        return this;
    }

    @Override
    public boolean handle(final Player player, Component component, int opcode, int button, int slot, int itemId) {
        if (GameWorld.getTicks() < player.getAttribute("magic:delay", -1)) {
            return true;
        }
        return MagicSpell.castSpell(player, SpellBook.forConfig(player.getSpellBookManager().getSpellBook()), button, player);
    }
}
