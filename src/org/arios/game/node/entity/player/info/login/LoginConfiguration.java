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
import org.arios.net.packet.context.IPContext;
import org.arios.net.packet.context.InterfaceContext;
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
     * The weekly message.
     */
    private static WeeklyMessage WEEKLY_MESSAGE = WeeklyMessage.PLAYER_SCAMMING;

    /**
     * Represents the quest point items to remove.
     */
    private static final Item[] QUEST_ITEMS = new Item[]{new Item(9813), new Item(9814)};

    /**
     * The login plugins.
     */
    private static final List<Plugin<Object>> LOGIN_PLUGINS = new ArrayList<>();

    /**
     * The weekly message.
     */
    private static String weeklyMessage = "Welcome to " + GameWorld.getName() + ". For more information register at www.ariosrsps.com";

    /**
     * Constructs a new {@Code LoginConfiguration} {@Code Object}
     */
    public LoginConfiguration() {
        /**
         * empty.
         */
    }

    /**
     * Reads the weekly message.
     */
    public static void readWeeklyMessage() {
        Connection connection = SQLManager.getConnection();
        if (connection == null) {
            return;
        }
        try {
            ResultSet set = connection.createStatement().executeQuery("SELECT * FROM worlds WHERE world='" + GameWorld.getSettings().getWorldId() + "'");
            if (set == null || !set.next()) {
                SQLManager.close(connection);
                return;
            }
            int type = set.getInt("messageType");
            String message = set.getString("message");
            if (type > WeeklyMessage.values().length - 1) {
                SQLManager.close(connection);
                return;
            }
            WEEKLY_MESSAGE = WeeklyMessage.values()[type];
            weeklyMessage = message;
            SQLManager.close(connection);
        } catch (SQLException e) {
            e.printStackTrace();
            SQLManager.close(connection);
        }
    }

    /**
     * Configures the lobby login.
     *
     * @param player The player.
     */
    public static void configureLobby(Player player) {
        //if (!player.isArtificial() && TutorialSession.getExtension(player).getStage() >= TutorialSession.MAX_STAGE && player.getAttribute("login_type", LoginType.NORMAL_LOGIN) != LoginType.RECONNECT_TYPE) {
            sendLobbyScreen(player);
        //} else {
        //    configureGameWorld(player);
        //}
    }

    /**
     * Sends the lobby interface-related packets.
     *
     * @param player The player.
     */
    public static void sendLobbyScreen(Player player) {//how would that break. What did you change all i did was copy shit from osremake....
        player.updateSceneGraph(true);
        PacketRepository.send(IPEncoder.class, new IPContext(player, "127.0.0.1"));
      //  sendLobbyScreen(player);
        /*Repository.getLobbyPlayers().add(player);
		player.getPacketDispatch().sendString("Welcome to " + GameWorld.getName() + "", 378, 12);
		player.getPacketDispatch().sendString(lastLogin(player),  378, 13);
		final int messages = player.getDetails().getPortal().getMessages();
		if (messages > 1) {
			player.getPacketDispatch().sendString("                                                                                                                                                                              " +
					"You have <col=01DF01>" + messages +  " unread message</col> in your message centre.", 378, 15);
		} else {
			player.getPacketDispatch().sendString("                                                                                                                                                                              " +
					"You have " + messages +  " unread message in your message centre.", 378, 15);
		}
		player.getPacketDispatch().sendString("", 378, 16);
		if (player.isMember()) {
			player.getPacketDispatch().sendString("You have <col=01DF01>unlimited</col> days of " + GameWorld.getName() + " member credit remaining.",  378, 19);
		} else {
			player.getPacketDispatch().sendString("You are not a member. Choose to subscribe and you'll get loads of extra benefits and features.",  378, 19);
		}
		player.getPacketDispatch().sendString("Never tell anyone your password, even if they claim to work for " + GameWorld.getName() + "!", 378, 14);
		player.getBankPinManager().drawLoginMessage();
		player.getPacketDispatch().sendString(weeklyMessage, WEEKLY_MESSAGE.getComponent(), WEEKLY_MESSAGE.getChild());
		player.getInterfaceManager().openWindowsPane(new Component(549));
		player.getInterfaceManager().setOpened(new Component(378));
		PacketRepository.send(Interface.class, new InterfaceContext(player, 549, 2, 378, true));
		PacketRepository.send(Interface.class, new InterfaceContext(player, 549, 3, WEEKLY_MESSAGE.getComponent(), true));*/
        Repository.getLobbyPlayers().add(player);

        player.getInterfaceManager().openWindowsPane(new Component(165));//this is 30

        player.getInterfaceManager().setOpened(new Component(378));

        player.getPacketDispatch().sendInterface(165, 1, 162, true);
        player.getPacketDispatch().sendInterface(165, 23, 163, true);
        player.getPacketDispatch().sendInterface(165, 24, 160, true);
        player.getPacketDispatch().sendInterface(165, 4, 122, true);//xp counter
        player.getPacketDispatch().sendInterface(165, 29, 378, false);
        player.getPacketDispatch().sendInterface(165, 28, 50, false);
        player.getPacketDispatch().sendInterface(165, 9, 320, true);
        player.getPacketDispatch().sendInterface(165, 10, 399, true);
        player.getPacketDispatch().sendInterface(165, 11, 149, true);
        player.getPacketDispatch().sendInterface(165, 12, 387, true);
        player.getPacketDispatch().sendInterface(165, 13, 541, true);
        player.getPacketDispatch().sendInterface(165, 14, 218, true);
        player.getPacketDispatch().sendInterface(165, 16, 429, true);
        player.getPacketDispatch().sendInterface(165, 17, 432, true);
        player.getPacketDispatch().sendInterface(165, 18, 182, true);
        player.getPacketDispatch().sendInterface(165, 19, 261, true);
        player.getPacketDispatch().sendInterface(165, 20, 216, true);
        player.getPacketDispatch().sendInterface(165, 21, 239, true);
        player.getPacketDispatch().sendInterface(165, 15, 7, true);
        player.getPacketDispatch().sendInterface(165, 8, 593, true);
        /*
        player.getPacketDispatch().sendString("Never tell anyone your password, even if they claim to work for Jagex!", 378, 14);
        player.getPacketDispatch().sendString("You have 0 unread messages in your message centre.", 378, 15);
        player.getPacketDispatch().sendString("You are not a member. Subscribe to access extra skills, areas and quests, and much<br>more besides.", 378, 18);
        player.getPacketDispatch().sendString("A membership subscription grants access to the members-only features of both versions of RuneScape.", 378, 20);
        player.getPacketDispatch().sendString("Keep your account secure.", 378, 21);
        player.getPacketDispatch().sendString("You last logged in <col=ff0000>earlier today<col=000000>.", 378, 13);
        player.getPacketDispatch().sendString("You do not have a Bank PIN. Please visit a bank if you would like one.", 378, 16);
        player.getPacketDispatch().sendString("Organise your teleport scrolls in the new <col=6f007f>Master Scroll Book</col> available from treasure trails. Also you can now recolour <col=003fbf>rock golems</col> with lovekite, elemental or daeyalt ore!", 50, 3);



        player.getPacketDispatch().sendCS2Script(233, new Object[] { 3276804, 33179, 0, 0, 468, 1897, 0, 392, -1 });
        player.getPacketDispatch().sendCS2Script(233, new Object[] { 3276805, 33194, 0, 56, 54, 74, 0, 660, -1 });

        player.getPacketDispatch().sendInterfaceSettings(399, 7, 0, 18, 2);
        player.getPacketDispatch().sendInterfaceSettings(399, 8, 0, 110, 2);
        player.getPacketDispatch().sendInterfaceSettings(399, 9, 0, 11, 2);
        player.getPacketDispatch().sendInterfaceSettings(261, 83, 1, 4, 2);
        player.getPacketDispatch().sendInterfaceSettings(261, 84, 1, 4, 2);
        player.getPacketDispatch().sendInterfaceSettings(216, 1, 0, 46, 2);
        player.getPacketDispatch().sendInterfaceSettings(239, 1, 0, 535, 2);*/

        //sendVarpReset();
		/*sendVarp(player, 0, 11);
		sendVarp(player, 18, 1);
		sendVarp(player, 19, 1);
		sendVarp(player, 20, 1375610679);
		sendVarp(player, 21, -541066386);
		sendVarp(player, 22, 937410018);
		sendVarp(player, 23, 216488002);
		sendVarp(player, 24, -2145895626);
		sendVarp(player, 25, 1374816446);
		sendVarp(player, 29, 2);
		sendVarp(player, 32, 3);
		sendVarp(player, 43, 1);
		sendVarp(player, 62, 6);
		sendVarp(player, 65, 10);*/
        //sendVarp(player, 84, 3072);
        //sendVarpbit(4102, 0);
		/*sendVarp(player, 101, 104);
		sendVarp(player, 107, 5);
		sendVarp(player, 111, 9);
		sendVarp(player, 144, 100);
		sendVarp(player, 147, 6);
		sendVarp(player, 150, 160);
		sendVarp(player, 160, 2);*/
       /* sendVarp(player, 166, player.getSettings().getBrightness());//brightness
        //sendVarp(player, 167, 0);
        sendVarp(player, 168, player.getSettings().getMusicVolume());//music volume
        sendVarp(player, 169, player.getSettings().getAreaSoundVolume());//sound volume
        sendVarp(player, 170, player.getSettings().isSingleMouseButton() ? 1 : 0);//mouse buttons
        sendVarp(player, 171, player.getSettings().isDisableChatEffects() ? 1 : 0);//chat effects
		*//*sendVarp(player, 173, 0);
		sendVarp(player, 176, 1);
		sendVarp(player, 178, 3);
		sendVarp(player, 180, 6);
		sendVarp(player, 222, 5522531);
		sendVarp(player, 279, 63488);
		sendVarp(player, 281, 1000);*//*
        sendVarp(player, 287, player.getSettings().isSplitPrivateChat() ? 1 : 0);//split private
		*//*sendVarp(player, 298, 96);
		sendVarp(player, 300, 1000);
		sendVarp(player, 302, 61);
		sendVarp(player, 304, 4000000);
		sendVarp(player, 307, 110);
		sendVarp(player, 311, -1811944496);
		sendVarp(player, 313, 511);
		sendVarp(player, 314, 80);
		sendVarp(player, 317, 50);
		sendVarp(player, 317, 63);
		sendVarp(player, 351, 33519626);
		sendVarp(player, 465, 511);
		sendVarp(player, 802, 511);
		sendVarp(player, 1085, 700);
		//sendVarp(player, 313, 8720);
		sendVarp(player, 346, 7654418);
		//sendVarp(player, 347, 10);
		sendVarp(player, 365, 10);
		sendVarp(player, 372, 23554999);
		sendVarp(player, 399, 8);
		sendVarp(player, 406, 20);
		sendVarp(player, 414, 263040);
		sendVarp(player, 427, 3);
		sendVarp(player, 429, 12);
		sendVarp(player, 437, 478826);
		sendVarp(player, 447, -1);
		sendVarp(player, 464, 1073799168);
		sendVarp(player, 486, 1073741824);
		sendVarp(player, 520, 1);
		sendVarp(player, 553, -2147483648);
		sendVarp(player, 598, -2113923070);
		sendVarp(player, 602, -2457347);
		sendVarp(player, 661, 268);
		sendVarp(player, 662, 8585338);
		sendVarp(player, 671, 20971610);
		sendVarp(player, 673, 2);
		sendVarp(player, 678, 2051);
		sendVarp(player, 679, 495);
		sendVarp(player, 680, 24828637);
		sendVarp(player, 721, 67111040);
		sendVarp(player, 728, 7);
		sendVarp(player, 738, 268435489);
		sendVarp(player, 788, 4280);
		sendVarp(player, 802, 15);
		sendVarp(player, 810, 33554432);
		sendVarp(player, 843, 14);
		sendVarp(player, 849, -1);
		sendVarp(player, 850, -1);
		sendVarp(player, 851, -1);
		sendVarp(player, 852, -1);
		sendVarp(player, 853, -1);
		sendVarp(player, 854, -1);
		sendVarp(player, 855, -1);
		sendVarp(player, 856, -1);
		sendVarp(player, 867, -2133832696);*//*
        sendVarp(player, 872, player.getSettings().getSoundEffectVolume());//sound effect volume
		*//*sendVarp(player, 904, 246);
		sendVarp(player, 906, 1536);
		sendVarp(player, 911, -8388608);
		sendVarp(player, 913, 4194304);
		sendVarp(player, 939, 240);
		sendVarp(player, 949, 4194304);
		sendVarp(player, 993, 1396736);
		sendVarp(player, 1009, 549453824);
		sendVarp(player, 1010, 2048);
		sendVarp(player, 1017, 8192);
		sendVarp(player, 1045, 1074273280);
		sendVarp(player, 1046, 805306368);
		sendVarp(player, 1050, 4096);
		sendVarp(player, 1052, 14);
		//sendVarp(player, 1055, 1088);//time message?
		sendVarp(player, 1065, -1);
		sendVarp(player, 1067, -1650982912);
		sendVarp(player, 1074, 0);//profanity filter
		sendVarp(player, 1075, -1);
		sendVarp(player, 1107, 0);//when to show attack player
		sendVarp(player, 1111, 65540);
		sendVarp(player, 1112, 1);
		sendVarp(player, 1151, -1);
		sendVarp(player, 1224, 172395585);
		sendVarp(player, 1225, 379887846);
		sendVarp(player, 1226, 12);
		sendVarp(player, 1227, 1107296385);
		sendVarp(player, 1233, 575042);
		sendVarp(player, 1236, 16418);
		sendVarp(player, 1255, 1210421);
		sendVarp(player, 1257, 737627);
		sendVarp(player, 1260, 37224);*//*
        sendVarp(player, 1306, 1);//when to show attack npc
        sendVarp(player, 26, player.getSettings().getPublicChatSetting());
        sendVarp(player, 1054, player.getSettings().getClanChatSetting());*/

        ///player.getCommunication().sync(player);
    }

    private static void sendVarp(Player player, int id, int value) {
        player.getConfigManager().set(id, value);
    }

    /**
     * Configures the game world.
     *
     * @param player The player.
     */
    public static void configureGameWorld(final Player player) {
        player.getConfigManager().reset();
        sendGameConfiguration(player);
        Repository.getLobbyPlayers().remove(player);
        player.setPlaying(true);
        UpdateSequence.getRenderablePlayers().add(player);
        RegionManager.move(player);
        player.getMusicPlayer().init();
        player.getUpdateMasks().register(new AppearanceFlag(player));
        player.getPlayerFlags().setUpdateSceneGraph(true);
        player.getStateManager().init();
    }

    /**
     * Sends the game configuration packets.
     *
     * @param player The player to send to.
     */
    public static void sendGameConfiguration(final Player player) {
        //this is why
        player.getInterfaceManager().openWindowsPane(new Component(548));
        player.getInterfaceManager().openChatbox(137);
        player.getInterfaceManager().openDefaultTabs();
        welcome(player);
        config(player);
        conditions(player);
        player.getCommunication().sync(player);
    }

    /**
     * Method used to welcome the player.
     *
     * @param player the player.
     */
    public static final void welcome(final Player player) {
        player.getPacketDispatch().sendMessage("Welcome to " + GameWorld.getName() + ".");
        //player.getPacketDispatch().sendMessage("Please remember to ::vote in order to gain credits to spend in our in-game ::shop.");
        //player.getPacketDispatch().sendMessage("You may also spend credits in our online store by typing ::store.");
        if (GameWorld.getSettings().isDevMode()) {
            player.sendMessage("** You are playing on a <col=FF0000>developer</col> world. **");
            player.sendMessage("These worlds will restart at any time and randomly to apply updates and changes.");
        }
        if (!GameWorld.getSettings().isEconomy()) {
            player.getPacketDispatch().sendMessage("<col=FF0000>You are currently playing on Arios's spawn PK world.");
        }
        if (player.getDetails().getPortal().isMuted()) {
            player.getPacketDispatch().sendMessage("You are muted.");
            player.getPacketDispatch().sendMessage("To prevent further mutes please read the rules.");
        }
        if (SystemManager.getSystemConfig().getConfig("dxp", false)) {
            player.sendMessage("<col=CC6600>There is currently a double XP weekend active.");
        }
        if (GlobalEvents.isRunning) {
            GlobalEvents.sendSoloMessage(player, GlobalEvents.getActive().getMessage());
            GlobalEvents.sendSoloMessage(player, "This event will remain active for the next " + GlobalEvents.getActive().getTimer() / 100 + " minutes.");
        }
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
        player.getFamiliarManager().login();
        player.getInterfaceManager().openDefaultTabs();
        player.getGrandExchange().init();
        player.getPacketDispatch().sendString("Friends List - World " + GameWorld.getSettings().getWorldId(), 550, 3);
        player.getConfigManager().init();
        player.getAntiMacroHandler().init();
        player.getQuestRepository().update(player);
        player.getGraveManager().update();
        player.getInterfaceManager().close();
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
        if (GameWorld.isEconomyWorld()) {
            TutorialSession.extend(player);
            if (!TutorialSession.getExtension(player).finished()) {
                GameWorld.submit(new Pulse(1, player) {
                    @Override
                    public boolean pulse() {
                        TutorialSession.getExtension(player).init();
                        return true;
                    }
                });
            }
        }
        //Temp
        if (player.getBank().contains(995, 50000000) || player.getInventory().contains(995, 50000000) && !player.isAdmin()) {
            System.out.println(player.getUsername() + " has 50m+ coins in their bank/inventory.");
        }
        if (player.getAttribute("fc_wave", -1) > -1) {
            ActivityManager.start(player, "fight caves", true);
        }
        if (player.getAttribute("falconry", false)) {
            ActivityManager.start(player, "falconry", true);
        }
        player.getConfigManager().set(678, 5);// RFD
        if (player.getSavedData().getQuestData().getDragonSlayerAttribute("repaired")) {
            player.getConfigManager().set(177, 1967876);
        }
        if (player.getSavedData().getGlobalData().getLootShareDelay() < System.currentTimeMillis() && player.getSavedData().getGlobalData().getLootShareDelay() != 0L) {
            player.getGlobalData().setLootSharePoints((int) (player.getGlobalData().getLootSharePoints() - player.getGlobalData().getLootSharePoints() * 0.10));
        } else {
            player.getSavedData().getGlobalData().setLootShareDelay(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1));
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
     * Calculates the last login and returns the message to display on the login
     * screen.
     *
     * @param player The player.
     * @return The message to display.
     */
    private static String lastLogin(Player player) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        long time = player.getDetails().getLastLogin();
        long diffDays = -1;
        if (time != -1) {
            long currentTime = dateFormat.getCalendar().getTime().getTime();
            diffDays = (currentTime - time) / (24 * 60 * 60 * 1000);
        }
        player.getDetails().setLastLogin(dateFormat.getCalendar().getTime().getTime());
        if (diffDays < 0) {
            return "Welcome to " + GameWorld.getName() + "!";
        }
        if (diffDays == 0) {
            return "You last logged in <col=ff0000>earlier today.";
        }
        if (diffDays == 1) {
            return "You last logged in <col=ff0000> yesterday.";
        }
        if (diffDays >= 2) {
            return "You last logged in <col=ff0000> " + diffDays + " days ago."; // <col=000000>
            // from:
            // "+player.getDetails().getIp() + "
        }
        return null;
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

    /**
     * Represents a weekly message.
     *
     * @author 'Vexia
     */
    public enum WeeklyMessage {
        MOVING_COGS(16, 2, "Welcome to " + GameWorld.getName() + ". For more information register at www.ariosrsps.com"), QUESTION_MARKS(17, 1, ""), DRAMA_FACE(18, 1, "Welcome to " + GameWorld.getName() + ". For more information register at www.ariosrsps.com"), BANK_PIN_VAULT(19, 1, ""), BAN_PIN_QUESTION_MARK(20, 1, ""), PLAYER_SCAMMING(21, 1, "Welcome to " + GameWorld.getName() + ". For more information register at www.ariosrsps.com"), BANK_PIN_KEY(22, 1, ""), CHRISTMAS_PRESENT(23, 1, ""), KILLCOUNT(24, 1, ""), BETA(18, 1, "Welcome to " + GameWorld.getName() + ".<br>Beta stage: #2");

        /**
         * Represents the child id.
         */
        private final int child;

        /**
         * The component id.
         */
        private final int component;

        /**
         * The message of the component.
         */
        private final String[] message;

        /**
         * Constructs a new {@code WeeklyMessage} {@code Object}.
         *
         * @param component the component.
         * @param message   the message.
         */
        WeeklyMessage(int component, int child, String... message) {
            this.component = component;
            this.child = child;
            this.message = message;
        }

        /**
         * Method used to get the message from the component.
         *
         * @param id the id.
         * @return the value.
         */
        public WeeklyMessage forId(int id) {
            for (WeeklyMessage message : WeeklyMessage.values()) {
                if (message.getComponent() == id) {
                    return message;
                }
            }
            return null;
        }

        /**
         * Gets the component.
         *
         * @return The component.
         */
        public int getComponent() {
            return component;
        }

        /**
         * Gets the message.
         *
         * @return The message.
         */
        public String[] getMessage() {
            return message;
        }

        /**
         * Gets the child.
         *
         * @return The child.
         */
        public int getChild() {
            return child;
        }

    }

    /**
     * reads the weekly message.
     */
    static {
        readWeeklyMessage();
    }
}