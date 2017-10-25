package org.arios.game.node.entity.player.info.login;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.arios.game.component.Component;
import org.arios.game.content.activity.ActivityManager;
import org.arios.game.content.global.GlobalEvents;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.InterfaceSetManager;
import org.arios.game.node.entity.player.link.SpellBookManager;
import org.arios.game.node.item.Item;
import org.arios.game.system.SystemManager;
import org.arios.game.system.mysql.SQLManager;
import org.arios.game.system.task.Pulse;
import org.arios.game.world.GameWorld;
import org.arios.game.world.map.RegionManager;
import org.arios.game.world.repository.Repository;
import org.arios.game.world.update.UpdateSequence;
import org.arios.game.world.update.flag.player.AppearanceFlag;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.CameraContext;
import org.arios.net.packet.context.IPContext;
import org.arios.net.packet.context.InterfaceContext;
import org.arios.net.packet.out.CameraViewPacket;
import org.arios.net.packet.out.CloseInterface;
import org.arios.net.packet.out.IPEncoder;
import org.arios.net.packet.out.Interface;
import org.arios.plugin.Plugin;

/**
 * Sends the login configuration packets.
 *
 * @author Emperor
 */
public final class LoginConfiguration {

    /**
     * Represents the quest point items to remove.
     */
    private static final Item[] QUEST_ITEMS = new Item[]{new Item(9813), new Item(9814)};

    /**
     * The login plugins.
     */
    private static final List<Plugin<Object>> LOGIN_PLUGINS = new ArrayList<>();

    /**
     * Constructs a new {@Code LoginConfiguration} {@Code Object}
     */
    public LoginConfiguration() {
        /**
         * empty.
         */
    }

    /**
     * Configures the game world.
     *
     * @param player The player.
     */
    public static void configureGameWorld(final Player player) {
        //player.getInterfaceManager().close();
        player.getConfigManager().reset();
        sendGameConfiguration(player);
        Repository.getLobbyPlayers().remove(player);
        player.setPlaying(true);
        UpdateSequence.getRenderablePlayers().add(player);
        RegionManager.move(player);
//		player.getMusicPlayer().init();
        player.getUpdateMasks().register(new AppearanceFlag(player));
        player.getPlayerFlags().setUpdateSceneGraph(true);
        player.getStateManager().init();
        player.getInventory().add(new Item(5733));
        player.toggleDebug();
    }

    /**
     * Sends the game configuration packets.
     *
     * @param player The player to send to.
     */
    public static void sendGameConfiguration(final Player player) {
        PacketRepository.send(CloseInterface.class, new InterfaceContext(player, 165, 29, 29, 378, false));
        PacketRepository.send(CloseInterface.class, new InterfaceContext(player, 165, 28, 28, 50, false));
        player.getInterfaceManager().openWindowsPane(new Component(player.getInterfaceManager().isResizable() ? 161 : 548));
        InterfaceSetManager.sendSets(player, player.getDetails().getClientInfo().getWindowMode() + 2);
        player.getInterfaceManager().openRootChatbox();
        //player.getPacketDispatch().sendCS2Script(1080, new Object[] { "jj" });
        //player.getInterfaceManager().openDefaultTabs();
        config(player);
        conditions(player);
        //player.getMusicManager().configure();
        player.getCommunication().sync(player);
    }

    /**
     * Method used to welcome the player.
     *
     * @param player the player.
     */
    public static final void sendWelcomeMessage(final Player player) {
        player.getPacketDispatch().sendMessage("Welcome to " + GameWorld.getName() + ".");
        if (!GameWorld.getSettings().isEconomy()) {
            player.getPacketDispatch().sendMessage("<col=FF0000>You are currently playing on RuneScape's spawn PK world.");
        }
        if (player.getDetails().getPortal().isMuted()) {
           // player.getPacketDispatch().sendMessage(player.getDetails().getPortal().getMute().toString());
            player.getPacketDispatch().sendMessage("You will be un-muted within 24 hours.");
            player.getPacketDispatch().sendMessage("To prevent further mutes please read the rules.");
        }
		/*if (player.getDetails().hasDisplayName()) {
			player.getPacketDispatch().sendMessage("<col=FF0000>Display names are temporarily disabled.");
		}*/
    }

    /**
     * Method used to configure all possible settings for the player.
     *
     * @param player the player.
     */
    public static final void config(final Player player) {
        player.getInventory().refresh();
        player.getEquipment().refresh();
        player.getSkills().refresh();
        player.getSkills().configure();
        player.getSettings().update();
        player.getInteraction().setDefault();
        player.getPacketDispatch().sendRunEnergy();
        player.getEmotes().refreshListConfigs();
        player.getInterfaceManager().openDefaultTabs();
        player.getSpellBookManager().update(player);
        //player.getPacketDispatch().sendString("Friends List - World " + GameWorld.getSettings().getWorldId(), 550, 2);
        if (player.getAttributes().containsKey("spell:swap")) {
            player.getSpellBookManager().setSpellBook(SpellBookManager.SpellBook.LUNAR);
        }
        player.getConfigManager().init();
        player.getAntiMacroHandler().init();
        player.getQuestRepository().update(player);
        //player.getMonitor().getActivityMonitor().monitor(player);
    }

    /**
     * Method used to check for all possible conditions on login.
     *
     * @param player the player.
     */
    public static final void conditions(final Player player) {
        player.unlock();
        if (player.isArtificial()) {
            return;
        }
        if (player.getAttribute("fc_wave", -1) > -1) {
            ActivityManager.start(player, "fight caves", true);
        }
        if (player.getAttribute("falconry", false)) {
            ActivityManager.start(player, "falconry", true);
        }
        //player.getConfigManager().set(678, 5);//RFD
        if (player.getSavedData().getQuestData().getDragonSlayerAttribute("repaired")) {
            player.getConfigManager().set(177, 1967876);
        }
        checkQuestPointsItems(player);
        for (Plugin<Object> plugin : LOGIN_PLUGINS) {
            try {
                plugin.newInstance(player);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method used to check for the quest point cape items.
     *
     * @param player the player.
     */
    private static void checkQuestPointsItems(final Player player) {
        if (!player.getQuestRepository().hasCompletedAll() && player.getEquipment().contains(9813, 1) || player.getEquipment().contains(9814, 1)) {
            for (Item i : QUEST_ITEMS) {
                if (player.getEquipment().remove(i)) {
                    player.getDialogueInterpreter().sendItemMessage(i, "As you no longer have completed all the quests, your " + i.getName() + " unequips itself to your " + (player.getInventory().freeSlots() < 1 ? "bank" : "inventory") + "!");
                    if (player.getInventory().freeSlots() < 1) {
                        player.getBank().add(i);
                    } else {
                        player.getInventory().add(i);
                    }
                }
            }
        }
    }

    /**
     * Gets the loginPlugins.
     *
     * @return The loginPlugins.
     */
    public static List<Plugin<Object>> getLoginPlugins() {
        return LOGIN_PLUGINS;
    }
}