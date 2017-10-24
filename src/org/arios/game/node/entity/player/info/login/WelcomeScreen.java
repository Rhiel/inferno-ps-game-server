package org.arios.game.node.entity.player.info.login;

import org.arios.game.component.Component;
import org.arios.game.node.entity.player.Player;
import org.arios.game.world.GameWorld;
import org.arios.game.world.repository.Repository;
import org.arios.net.Constants;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.CameraContext;
import org.arios.net.packet.context.IPContext;
import org.arios.net.packet.out.CameraViewPacket;
import org.arios.net.packet.out.IPEncoder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class WelcomeScreen {

    /**
     * Configures the lobby login.
     *
     * @param player The player.
     */
    public static void configureLobby(Player player) {
        player.updateSceneGraph(true);
        PacketRepository.send(CameraViewPacket.class, new CameraContext(player, CameraContext.CameraType.SET, 0, 0, 0, 0,0));
        PacketRepository.send(IPEncoder.class, new IPContext(player, "127.0.0.1"));
        sendLobbyScreen(player);
    }

    /**
     * Calculates the last login and returns the message to display on the login screen.
     *
     * @param player The player.
     * @return The message to display.
     */
    public static String lastLogin(Player player) {
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
            return "You last logged in <col=ff0000> " + diffDays + " days ago."; //<col=000000> from: "+player.getDetails().getIp() + "
        }
        return null;
    }

    /**
     * Sends the lobby interface-related packets.
     *
     * @param player The player.
     */
    public static void sendLobbyScreen(Player player) {

        Repository.getLobbyPlayers().add(player);

        player.getInterfaceManager().openWindowsPane(new Component(165));

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

        player.getPacketDispatch().sendString("Never tell anyone your password, even if they claim to work for Jagex!", 378, 14);
        player.getPacketDispatch().sendString("You have 0 unread messages in your message centre.", 378, 15);
        player.getPacketDispatch().sendString("You are not a member. Subscribe to access extra skills, areas and quests, and much<br>more besides.", 378, 18);
        player.getPacketDispatch().sendString("A membership subscription grants access to the members-only features of both versions of RuneScape.", 378, 20);
        player.getPacketDispatch().sendString("Keep your account secure.", 378, 21);
        player.getPacketDispatch().sendString("You last logged in <col=ff0000>earlier today<col=000000>.", 378, 13);
        player.getPacketDispatch().sendString("You do not have a Bank PIN. Please visit a bank if you would like one.", 378, 16);
        player.getPacketDispatch().sendString("Organise your teleport scrolls in the new <col=6f007f>Master Scroll Book</col> available from treasure trails. Also you can now recolour <col=003fbf>rock golems</col> with lovekite, elemental or daeyalt ore!", 50, 3);


        player.getPacketDispatch().sendCS2Script(233, new Object[]{3276804, 33179, 0, 0, 468, 1897, 0, 392, -1});
        player.getPacketDispatch().sendCS2Script(233, new Object[]{3276805, 33194, 0, 56, 54, 74, 0, 660, -1});

        player.getPacketDispatch().sendCS2Script(1080, new Object[] { });

        player.getPacketDispatch().sendInterfaceSettings(399, 7, 0, 18, 2);
        player.getPacketDispatch().sendInterfaceSettings(399, 8, 0, 110, 2);
        player.getPacketDispatch().sendInterfaceSettings(399, 9, 0, 11, 2);
        player.getPacketDispatch().sendInterfaceSettings(261, 83, 1, 4, 2);
        player.getPacketDispatch().sendInterfaceSettings(261, 84, 1, 4, 2);
        player.getPacketDispatch().sendInterfaceSettings(216, 1, 0, 46, 2);
        player.getPacketDispatch().sendInterfaceSettings(239, 1, 0, 535, 2);

        player.getConfigManager().set(18, 1);
        player.getConfigManager().set(20, 131072);
        player.getConfigManager().set(21, 67141632);
        player.getConfigManager().set(22, 33554432);
        player.getConfigManager().set(23, 2097216);
        player.getConfigManager().set(43, 1);
        player.getConfigManager().set(101, 0);
        player.getConfigManager().set(153, -1);
        player.getConfigManager().set(166, 2);
        player.getConfigManager().set(167, 0);
        player.getConfigManager().set(168, 4);
        player.getConfigManager().set(169, 4);
        player.getConfigManager().set(170, 0);
        player.getConfigManager().set(171, 0);
        player.getConfigManager().set(173, 1);
        player.getConfigManager().set(281, 1000);
        player.getConfigManager().set(284, 60001);
        player.getConfigManager().set(287, 0);
        player.getConfigManager().set(300, 1000);
        player.getConfigManager().set(406, 20);
        player.getConfigManager().set(447, -1);
        player.getConfigManager().set(449, 2097152);
        player.getConfigManager().set(486, 1073741824);
        player.getConfigManager().set(520, 1);
        player.getConfigManager().set(553, -2147483648);
        player.getConfigManager().set(788, 128);
        player.getConfigManager().set(810, 33554432);
        player.getConfigManager().set(849, -1);
        player.getConfigManager().set(850, -1);
        player.getConfigManager().set(851, -1);
        player.getConfigManager().set(852, -1);
        player.getConfigManager().set(853, -1);
        player.getConfigManager().set(854, -1);
        player.getConfigManager().set(855, -1);
        player.getConfigManager().set(856, -1);
        player.getConfigManager().set(872, 4);
        player.getConfigManager().set(904, 253);
        player.getConfigManager().set(913, 4194304);
        player.getConfigManager().set(1010, 2048);
        player.getConfigManager().set(1017, 8192);
        player.getConfigManager().set(1050, 4096);
        player.getConfigManager().set(1065, -1);
        player.getConfigManager().set(1067, -1302855680);
        player.getConfigManager().set(1074, 0);
        player.getConfigManager().set(1075, -1);
        player.getConfigManager().set(1107, 0);
        player.getConfigManager().set(1151, -1);
        player.getConfigManager().set(1224, 172395585);
        player.getConfigManager().set(1225, 379887846);
        player.getConfigManager().set(1226, 12);
        player.getConfigManager().set(1306, 0);
        player.getConfigManager().set(1427, -1);

        player.sendMessage("Welcome to Inferno #155!");

    }

    private static void sendVarp(Player player, int id, int value) {
        player.getConfigManager().set(id, value);
    }

}