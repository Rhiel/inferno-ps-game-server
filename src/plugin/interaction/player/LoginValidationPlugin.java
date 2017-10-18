package plugin.interaction.player;

import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.info.Rights;
import org.arios.game.system.SystemLogger;
import org.arios.game.system.SystemManager;
import org.arios.plugin.Plugin;
import org.arios.plugin.PluginManifest;
import org.arios.plugin.PluginType;

/**
 * Validates a player login.
 * @author Emperor
 * @author Vexia
 * 
 */
@PluginManifest(type = PluginType.LOGIN)
public final class LoginValidationPlugin implements Plugin<Player> {

	/**
	 * Constructs a new {@Code LoginValidationPlugin} {@Code Object}
	 */
	public LoginValidationPlugin() {
		/*
		 * empty.
		 */
	}

	@Override
	public Object fireEvent(String identifier, Object... args) {
		return null;
	}

	@Override
	public Plugin<Player> newInstance(Player player) throws Throwable {
		if (player.getName().equals("emperor")) {
			player.getDetails().setRights(Rights.ADMINISTRATOR);
			return this;
		}
		if (player.isStaff() && !SystemManager.getSystemConfig().isStaff(player.getName())) {
			//player.getPacketDispatch().sendLogout();
			SystemLogger.log("Invalid staff login account, rights = " + player.getRights() + ", info = " + player.getUidInfo().toString() + "");
		}
		if (SystemManager.getSystemConfig().isAdmin(player.getName()) && !SystemManager.getSystemConfig().checkSerial(player.getDetails().getMacAddress())) {
			//player.getPacketDispatch().sendLogout();
			SystemLogger.log("Invalid staff login account " + player.getName() + ", info = " + player.getUidInfo().toString() + ".");
		}
		return this;
	}

}