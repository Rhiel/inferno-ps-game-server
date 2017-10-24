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

	//Summoning was 34.
	private static final int ATTACK_TAB = 47; // was 51
	private static final int SKILL_TAB = 48; // was 52
	private static final int QUEST_TAB = 49; // was 53
	private static final int INVENTORY_TAB = 50; // was 54
	private static final int EQUIPMENT_TAB = 51; // was 55
	private static final int PRAYER_TAB = 52; // was 56
	private static final int MAGIC_TAB = 53; // was 57
	private static final int CLAN_TAB = 30; //was 35
	private static final int FRIENDS_TAB = 31; //was 35
	private static final int IGNORES_TAB = 32; //was 36
	private static final int LOGOUT_TAB = 33;
	private static final int OPTIONS_TAB = 34;
	private static final int EMOTES_TAB = 35; //was 39
	private static final int MUSIC_TAB = 36; //was 40
	private static final int XP_ORB = 1;
	private static final int HEALTH_ORB = -5;
	private static final int PRAYER_ORB = 14;
	private static final int RUN_ORB = 22;
	private static final int WORLD_ORB = 29;

	@Override
	public Plugin<Object> newInstance(Object arg) throws Throwable {
		ComponentDefinition.put(160, this);
		ComponentDefinition.put(548, this);
		ComponentDefinition.put(161, this);
		ComponentDefinition.put(162, this);
		return this;
	}

	@Override
	public boolean handle(final Player player, Component component, int opcode, int button, int slot, int itemId) {
		if(component.getId() == 162) {//chat
			switch (button) {
				case 26:
					openReport(player);
					return true;
			}
		}
		if(component.getId() == 160) {//orbs
			switch (button) {
				case WORLD_ORB:
					switch (opcode) {
						case 215://548 window
							player.getInterfaceManager().openOverlay(new Component(595));//same 595
							break;
						case 85://fullscreen
							player.getInterfaceManager().close();
							player.getInterfaceManager().openWindowsPane(new Component(165));
							player.getPacketDispatch().sendInterface(165, 30, 595, true);
							player.getInterfaceManager().setOpened(new Component(595));
							player.getPacketDispatch().sendInterface(165, 28, 594, false);
							break;
					}
					player.getPacketDispatch().sendInterfaceSettings(595, 17, 0, 4, 2);//same for fullscreen
					player.getPacketDispatch().sendCS2Script(1749, new Object[]{player.getLocation().toPositionPacked()});//player location in some hash
					return true;

				case RUN_ORB:
					player.getSettings().toggleRun();
					return true;
			}
		}
		switch (button) {
			case ATTACK_TAB:
			case SKILL_TAB:
			case QUEST_TAB:
			case INVENTORY_TAB:
			case EQUIPMENT_TAB:
			case PRAYER_TAB:
			case MAGIC_TAB:
			case CLAN_TAB:
			case FRIENDS_TAB:
			case IGNORES_TAB:
			case LOGOUT_TAB:
			case OPTIONS_TAB:
			case EMOTES_TAB:
			case MUSIC_TAB:
				player.getInterfaceManager().setCurrentTabIndex(getTabIndex(button));
				break;
		}
		switch (button) {
			case OPTIONS_TAB:
				break;
			case INVENTORY_TAB:
				player.getInventory().refresh();
				break;
			case SKILL_TAB:
				break;
			case MUSIC_TAB:
				break;
			case EMOTES_TAB:
				break;
			case QUEST_TAB:
				player.getQuestRepository().update(player);
				break;
			case EQUIPMENT_TAB:
				break;
			case ATTACK_TAB:
				if (player.getExtension(WeaponInterface.class) == WeaponInterfaces.STAFF) {
                    /*final Component c = new Component(593);
                    c.getDefinition().setContext(new InterfaceContext(player, 548, InterfaceManager.ATTACK_SLOT, 593, false));
                    player.getInterfaceManager().openTab(c);*/
					final WeaponInterface inter = player.getExtension(WeaponInterface.class);
					inter.updateInterface();
				}
				break;
			case PRAYER_TAB:
				break;
			case FRIENDS_TAB:
				break;
			case IGNORES_TAB:
				break;
			case MAGIC_TAB:
				break;
		}
		return true;
	}

	/**
	 * Method used to open the report interface.
	 *
	 * @param player the player.
	 */
	private void openReport(final Player player) {
		player.getPacketDispatch().sendCS2Script(917, new Object[] {-1, -1});
		player.getInterfaceManager().open(new Component(553)).setCloseEvent(new CloseEvent() {
			@Override
			public boolean close(Player player, Component c) {
				player.getPacketDispatch().sendRunScript(1105, "");
				return true;
			}
		});
		player.getPacketDispatch().sendCS2Script(1104, new Object[] {1});
        /*if (player.getDetails().getRights() != Rights.REGULAR_PLAYER) {
            for (int i = 0; i < 18; i++) {
                player.getPacketDispatch().sendInterfaceConfig(553, i, false);
            }
        }*/
	}

	/**
	 * Gets the tab index.
	 *
	 * @param button The button id.
	 * @return The tab index.
	 * TODO: ME
	 */
	private static int getTabIndex(int button) {
		return button >= 47 ? (button - 47) : (button - 23);
	}
}
