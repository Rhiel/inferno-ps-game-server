package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.node.entity.player.Player;
import org.arios.plugin.Plugin;

public final class WorldMap extends ComponentPlugin {
    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ComponentDefinition.put(595, this);
        return this;
    }

    @Override
    public boolean handle(Player player, Component component, int opcode, int button, int slot, int itemId) {
        switch (button) {
            case 34:
                player.getInterfaceManager().close();
                player.getInterfaceManager().closeOverlay();
                player.getInterfaceManager().openWindowsPane(new Component(player.getInterfaceManager().isResizable() ? 161 : 548));
                break;
        }
        return true;
    }
}