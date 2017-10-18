package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.RunScript;
import org.arios.game.system.communication.ClanRepository;
import org.arios.game.system.communication.ClanRank;
import org.arios.net.amsc.MSPacketRepository;
import org.arios.net.amsc.WorldCommunicator;
import org.arios.plugin.Plugin;
import org.arios.tools.StringUtils;

/**
 * Represents the plugin used to handle the clan interfaces.
 * @author Vexia
 */
public final class ClanInterfacePlugin extends ComponentPlugin {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	ComponentDefinition.put(590, this);
	ComponentDefinition.put(589, this);
	return this;
    }

    @Override
    public boolean handle(Player player, Component component, int opcode, int button, int slot, int itemId) {
	switch (component.getId()) {
	case 589:
	    switch (button) {
	    case 9:
		if (player.getInterfaceManager().getComponent(590) != null) {
		    player.getPacketDispatch().sendMessage("Please close the interface you have open before using 'Clan Setup'");
		    return true;
		}
		ClanRepository.openSettings(player);
		return true;
	    case 14:
		player.getDetails().getCommunication().toggleLootshare(player);
		return true;
	    }
	    break;
	case 590:
	    final ClanRepository clan = ClanRepository.get(player.getName(), true);
	    switch (button) {
	    case 31:
		clan.setJoinRequirement(getRank(opcode));
		player.getDetails().getCommunication().setJoinRequirement(clan.getJoinRequirement());
		MSPacketRepository.setClanSetting(player, 0, clan.getJoinRequirement());
		player.getPacketDispatch().sendString(clan.getJoinRequirement().getInfo(), 590, 31);
		break;
	    case 32:
		clan.setMessageRequirement(getRank(opcode));
		player.getDetails().getCommunication().setMessageRequirement(clan.getMessageRequirement());
		MSPacketRepository.setClanSetting(player, 1, clan.getMessageRequirement());
		player.getPacketDispatch().sendString(clan.getMessageRequirement().getInfo(), 590, 32);
		break;
	    case 33:
		clan.setKickRequirement(getRank(opcode));
		player.getDetails().getCommunication().setKickRequirement(clan.getKickRequirement());
		MSPacketRepository.setClanSetting(player, 2, clan.getKickRequirement());
		player.getPacketDispatch().sendString(clan.getKickRequirement().getInfo(), 590, 33);
		clan.update();
		break;
	    case 34:
		if (opcode == 230) {
		    clan.setLootRequirement(ClanRank.ARIOS_MOD);
		} else {
		    clan.setLootRequirement(getRank(opcode));
		}
		player.getDetails().getCommunication().setLootRequirement(clan.getLootRequirement());
		MSPacketRepository.setClanSetting(player, 3, clan.getLootRequirement());
		player.getPacketDispatch().sendString(clan.getLootRequirement().getInfo(), 590, 34);
		break;
	    case 30:
		switch (opcode) {
		case 205:
		    clan.setName("Chat disabled");
		    player.getCommunication().setClanName("");
		    player.getPacketDispatch().sendString(clan.getName(), 590, 30);
		    if (WorldCommunicator.isEnabled()) {
			MSPacketRepository.sendClanRename(player, "");
			break;
		    }
		    clan.clean(true);
		    break;
		default:
		    player.setAttribute("runscript", new RunScript() {
			@Override
			public boolean handle() {
			    String name = StringUtils.formatDisplayName((String) value);
			    player.getPacketDispatch().sendString(name, 590, 30);
			    if (WorldCommunicator.isEnabled()) {
				MSPacketRepository.sendClanRename(player, name);
				clan.setName(name);
				return true;
			    }
			    if (clan.getName().equals("Chat disabled")) {
				player.getPacketDispatch().sendMessage("Your clan channel has now been enabled!");
				player.getPacketDispatch().sendMessage("Join your channel by clicking 'Join Chat' and typing: " + player.getName(true, true));
			    }
			    clan.setName(name);
			    player.getCommunication().setClanName(name);
			    clan.update();
			    return true;
			}
		    });
		    player.getDialogueInterpreter().sendInput(true, "Enter clan prefix:");
		    break;
		}
		break;
	    }
	    break;
	}
	return true;
    }

    /**
     * Gets the value to set.
     * @param opcode the opcode.
     * @return the value.
     */
    public static ClanRank getRank(int opcode) {
	switch (opcode) {
	case 230:
	    return ClanRank.NONE;
	case 205:
	    return ClanRank.FRIEND;
	case 127:
	    return ClanRank.RECRUIT;
	case 211:
	    return ClanRank.CORPORAL;
	case 203:
	    return ClanRank.SERGEANT;
	case 39:
	    return ClanRank.LIEUTENANT;
	case 187:
	    return ClanRank.CAPTAIN;
	case 156:
	    return ClanRank.GENERAL;
	case 128:
	    return ClanRank.OWNER;
	}
	return ClanRank.NONE;
    }

}
