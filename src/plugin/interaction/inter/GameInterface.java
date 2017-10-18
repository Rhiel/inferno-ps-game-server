package plugin.interaction.inter;

import org.arios.game.component.CloseEvent;
import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.content.global.tutorial.TutorialStage;
import org.arios.game.node.entity.combat.equipment.WeaponInterface;
import org.arios.game.node.entity.combat.equipment.WeaponInterface.WeaponInterfaces;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.info.Rights;
import org.arios.game.world.GameWorld;
import org.arios.game.world.map.RegionManager;
import org.arios.net.packet.context.InterfaceContext;
import org.arios.plugin.Plugin;

/**
 * Represents the component plugin used for the game interface.
 * @author 'Vexia
 * @version 1.0
 */
public final class GameInterface extends ComponentPlugin {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	ComponentDefinition.put(548, this);
	return this;
    }

    @Override
    public boolean handle(final Player player, Component component, int opcode, int button, int slot, int itemId) {
	switch (button) {
	case 28:
	case 38:
	case 54:
	case 52:
	case 40:
	case 39:
	case 53:
	case 55:
	case 51:
	case 56:
	case 35:
	case 36:
	case 37:
	case 57:
	case 34:
	case 91:
	    player.getInterfaceManager().setCurrentTabIndex(getTabIndex(button));
	    break;
	}
	int tut_stage = TutorialSession.getExtension(player).getStage();
	switch (button) {
	case 28:
	    player.getPulseManager().clear();
	    openReport(player);
	    break;
	case 38:
	    if (tut_stage == 1) {
		TutorialStage.load(player, 2, false);
	    }
	    break;
	case 54:
	    if (tut_stage == 5) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 6, false);
	    }
	    player.getInventory().refresh();
	    break;
	case 52:
	    if (tut_stage == 10) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 11, false);
	    }
	    break;
	case 40:
	    if (tut_stage == 21) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 22, false);
	    }
	    break;
	case 39:
	    if (tut_stage == 23) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 24, false);
	    }
	    break;
	case 53:
	    if (tut_stage == 27) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 28, false);
	    }
	    player.getConfigManager().set(1021, 0);
	    if (GameWorld.isEconomyWorld()) {
		player.getQuestRepository().update(player);
	    } else {
		player.getSavedData().getSpawnData().drawStatsTab(player);
	    }
	    break;
	case 55:
	    if (tut_stage == 45) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 46, false);
	    }
	    break;
	case 51:
	    if (tut_stage == 49) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 50, false);
	    } else {
		if (TutorialSession.getExtension(player).getStage() > TutorialSession.MAX_STAGE) {
		    if (player.getExtension(WeaponInterface.class) == WeaponInterfaces.STAFF) {
			final Component c = new Component(WeaponInterfaces.STAFF.getInterfaceId());
			c.getDefinition().setContext(new InterfaceContext(player, 548, 128, WeaponInterfaces.STAFF.getInterfaceId(), false));
			player.getInterfaceManager().openTab(c);
			final WeaponInterface inter = player.getExtension(WeaponInterface.class);
			inter.updateInterface();
		    }
		}
	    }
	    break;
	case 56:
	    if (tut_stage == 61) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 62, false);
	    }
	    break;
	case 34:
	    // TODO: familiar.
	    break;
	case 35:
	    if (tut_stage == 63) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 64, false);
	    }
	    break;
	case 36:
	    if (tut_stage == 64) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 65, false);
	    }
	    break;
	case 57:
	    if (tut_stage == 68) {
		player.getConfigManager().set(1021, 0);
		TutorialStage.load(player, 69, false);
		player.getDialogueInterpreter().open(946, RegionManager.getNpc(player, 946));
	    }
	    break;
	}
	return true;
    }

    /**
     * Method used to open the report interface.
     * @param player the player.
     */
    private void openReport(final Player player) {
	player.getInterfaceManager().open(new Component(553)).setCloseEvent(new CloseEvent() {
	    @Override
	    public boolean close(Player player, Component c) {
		player.getPacketDispatch().sendRunScript(80, "");
		player.getPacketDispatch().sendRunScript(137, "");
		return true;
	    }
	});
	player.getPacketDispatch().sendRunScript(508, "");
	if (player.getDetails().getRights() != Rights.REGULAR_PLAYER) {
	    for (int i = 0; i < 18; i++) {
		player.getPacketDispatch().sendInterfaceConfig(553, i, false);
	    }
	}
    }

    /**
     * Gets the tab index.
     * @param button The button id.
     * @return The tab index.
     */
    private static int getTabIndex(int button) {
	int tabIndex = button - 27;
	if (button == 91) {
	    tabIndex = 14;
	} else if (button > 40) {
	    tabIndex = button - 51;
	}
	return tabIndex;
    }
}