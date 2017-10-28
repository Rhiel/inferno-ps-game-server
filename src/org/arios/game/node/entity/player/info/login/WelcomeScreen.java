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
        player.getPacketDispatch().sendCS2Script(2014, new Object[] { 0, 0, 0, 0, 0, 0 });
        player.getPacketDispatch().sendCS2Script(2015, new Object[] { 0 });

        player.getPacketDispatch().sendInterfaceSettings(399, 7, 0, 18, 2);
        player.getPacketDispatch().sendInterfaceSettings(399, 8, 0, 110, 2);
        player.getPacketDispatch().sendInterfaceSettings(399, 9, 0, 11, 2);
        player.getPacketDispatch().sendInterfaceSettings(261, 83, 1, 4, 2);
        player.getPacketDispatch().sendInterfaceSettings(261, 84, 1, 4, 2);
        player.getPacketDispatch().sendInterfaceSettings(216, 1, 0, 46, 2);
        player.getPacketDispatch().sendInterfaceSettings(239, 1, 0, 535, 2);

        player.getConfigManager().set(0, 11);
        player.getConfigManager().set(5, 10);
        player.getConfigManager().set(10, 8);
        player.getConfigManager().set(11, 5);
        player.getConfigManager().set(12, 16);
        player.getConfigManager().set(14, 7);
        player.getConfigManager().set(17, 15);
        player.getConfigManager().set(18, 1);
        player.getConfigManager().set(19, 1);
        player.getConfigManager().set(20, -67141633);
        player.getConfigManager().set(21, -1);
        player.getConfigManager().set(22, -2097153);
        player.getConfigManager().set(23, -1879048198);
        player.getConfigManager().set(24, -1350565889);
        player.getConfigManager().set(25, -134758401);
        player.getConfigManager().set(26, 80);
        player.getConfigManager().set(29, 2);
        player.getConfigManager().set(30, 80);
        player.getConfigManager().set(31, 100);
        player.getConfigManager().set(32, 3);
        player.getConfigManager().set(43, 1);
        player.getConfigManager().set(60, 2);
        player.getConfigManager().set(62, 6);
        player.getConfigManager().set(63, 6);
        player.getConfigManager().set(65, 10);
        player.getConfigManager().set(67, 3);
        player.getConfigManager().set(68, 16);
        player.getConfigManager().set(71, 4);
        player.getConfigManager().set(76, 6);
        player.getConfigManager().set(80, 4);
        player.getConfigManager().set(84, 335544576);
        player.getConfigManager().set(101, 160);
        player.getConfigManager().set(107, 5);
        player.getConfigManager().set(111, 9);
        player.getConfigManager().set(116, 15);
        player.getConfigManager().set(122, 7);
        player.getConfigManager().set(130, 4);
        player.getConfigManager().set(131, 9);
        player.getConfigManager().set(135, 994);
        player.getConfigManager().set(139, 1);
        player.getConfigManager().set(144, 100);
        player.getConfigManager().set(145, 7);
        player.getConfigManager().set(147, 6);
        player.getConfigManager().set(148, 11);
        player.getConfigManager().set(150, 160);
        player.getConfigManager().set(153, -1);
        player.getConfigManager().set(160, 2);
        player.getConfigManager().set(161, 10);
        player.getConfigManager().set(162, 15465470);
        player.getConfigManager().set(165, 29);
        player.getConfigManager().set(166, 4);
        player.getConfigManager().set(167, 0);
        player.getConfigManager().set(168, 4);
        player.getConfigManager().set(169, 4);
        player.getConfigManager().set(170, 0);
        player.getConfigManager().set(171, 0);
        player.getConfigManager().set(173, 1);
        player.getConfigManager().set(175, 13);
        player.getConfigManager().set(176, 10);
        player.getConfigManager().set(177, 8257540);
        player.getConfigManager().set(178, 3);
        player.getConfigManager().set(179, 21);
        player.getConfigManager().set(180, 6);
        player.getConfigManager().set(188, 15);
        player.getConfigManager().set(192, 2);
        player.getConfigManager().set(197, 30);
        player.getConfigManager().set(222, 22434819);
        player.getConfigManager().set(223, 9);
        player.getConfigManager().set(226, 7);
        player.getConfigManager().set(267, 8);
        player.getConfigManager().set(273, 110);
        player.getConfigManager().set(279, 30721);
        player.getConfigManager().set(281, 1000);
        player.getConfigManager().set(284, 60001);
        player.getConfigManager().set(287, 1);
        player.getConfigManager().set(293, 65);
        player.getConfigManager().set(298, 667909350);
        player.getConfigManager().set(300, 1000);
        player.getConfigManager().set(302, 61);
        player.getConfigManager().set(304, 5800000);
        player.getConfigManager().set(307, 110);
        player.getConfigManager().set(311, -1115684912);
        player.getConfigManager().set(314, 80);
        player.getConfigManager().set(317, 50);
        player.getConfigManager().set(318, 63);
        player.getConfigManager().set(328, 15);
        player.getConfigManager().set(346, 536725211);
        player.getConfigManager().set(347, 10);
        player.getConfigManager().set(351, 33519626);
        player.getConfigManager().set(365, 10);
        player.getConfigManager().set(372, 90663863);
        player.getConfigManager().set(387, 110);
        player.getConfigManager().set(388, 7340032);
        player.getConfigManager().set(399, 8);
        player.getConfigManager().set(408, -2017034328);
        player.getConfigManager().set(414, -1427112062);
        player.getConfigManager().set(416, 285);
        player.getConfigManager().set(417, 1182793726);
        player.getConfigManager().set(423, -805668538);
        player.getConfigManager().set(425, 9);
        player.getConfigManager().set(427, 1);
        player.getConfigManager().set(435, 693532);
        player.getConfigManager().set(436, 352383887);
        player.getConfigManager().set(437, 478826);
        player.getConfigManager().set(440, 1540111);
        player.getConfigManager().set(441, -971504863);
        player.getConfigManager().set(442, -449958233);
        player.getConfigManager().set(443, 72548604);
        player.getConfigManager().set(445, 26);
        player.getConfigManager().set(446, 48);
        player.getConfigManager().set(447, -1);
        player.getConfigManager().set(449, 2179072);
        player.getConfigManager().set(452, 12077056);
        player.getConfigManager().set(453, 2357823);
        player.getConfigManager().set(464, 1090059540);
        player.getConfigManager().set(465, 21651468);
        player.getConfigManager().set(466, 256);
        player.getConfigManager().set(477, 85201888);
        player.getConfigManager().set(482, 386990514);
        player.getConfigManager().set(486, 1073741875);
        player.getConfigManager().set(491, 1075732480);
        player.getConfigManager().set(492, 4);
        player.getConfigManager().set(496, 5734402);
        player.getConfigManager().set(498, 67108864);
        player.getConfigManager().set(520, 14373);
        player.getConfigManager().set(534, 153391689);
        player.getConfigManager().set(553, -2147483648);
        player.getConfigManager().set(598, 137742);
        player.getConfigManager().set(602, -11922947);
        player.getConfigManager().set(616, 129084);
        player.getConfigManager().set(661, 5);
        player.getConfigManager().set(662, 12285067);
        player.getConfigManager().set(667, 42);
        player.getConfigManager().set(671, 20971610);
        player.getConfigManager().set(673, 2);
        player.getConfigManager().set(678, 4210692);
        player.getConfigManager().set(679, -677293824);
        player.getConfigManager().set(680, -1585779987);
        player.getConfigManager().set(681, 60);
        player.getConfigManager().set(683, 20590);
        player.getConfigManager().set(684, 1621579088);
        player.getConfigManager().set(685, 1275068466);
        player.getConfigManager().set(700, 1);
        player.getConfigManager().set(704, -269833476);
        player.getConfigManager().set(705, 1044338094);
        player.getConfigManager().set(709, -2139095027);
        player.getConfigManager().set(710, 31781);
        player.getConfigManager().set(711, 286329840);
        player.getConfigManager().set(712, 20);
        player.getConfigManager().set(720, 5);
        player.getConfigManager().set(721, 67156223);
        player.getConfigManager().set(728, 6);
        player.getConfigManager().set(738, 268437697);
        player.getConfigManager().set(788, 4280);
        player.getConfigManager().set(794, 74643469);
        player.getConfigManager().set(810, 33554432);
        player.getConfigManager().set(823, 190);
        player.getConfigManager().set(824, 512);
        player.getConfigManager().set(842, 1);
        player.getConfigManager().set(843, 20);
        player.getConfigManager().set(849, -1);
        player.getConfigManager().set(850, -1);
        player.getConfigManager().set(851, -1);
        player.getConfigManager().set(852, -1);
        player.getConfigManager().set(853, -1);
        player.getConfigManager().set(854, -1);
        player.getConfigManager().set(855, -1);
        player.getConfigManager().set(856, -1);
        player.getConfigManager().set(867, 1);
        player.getConfigManager().set(872, 4);
        player.getConfigManager().set(904, 253);
        player.getConfigManager().set(906, 1895826464);
        player.getConfigManager().set(907, 3212);
        player.getConfigManager().set(913, 4194304);
        player.getConfigManager().set(939, 240);
        player.getConfigManager().set(946, 268435456);
        player.getConfigManager().set(947, 30793);
        player.getConfigManager().set(948, -1326964213);
        player.getConfigManager().set(949, 4194304);
        player.getConfigManager().set(997, 240472075);
        player.getConfigManager().set(1000, 1342182053);
        player.getConfigManager().set(1001, 1073741832);
        player.getConfigManager().set(1002, 536870914);
        player.getConfigManager().set(1009, 32244224);
        player.getConfigManager().set(1010, 2048);
        player.getConfigManager().set(1011, 3);
        player.getConfigManager().set(1017, 8192);
        player.getConfigManager().set(1045, 1882726400);
        player.getConfigManager().set(1046, -268435454);
        player.getConfigManager().set(1047, 7168);
        player.getConfigManager().set(1049, 1114);
        player.getConfigManager().set(1050, 4113);
        player.getConfigManager().set(1055, 147972);
        player.getConfigManager().set(1058, 736640);
        player.getConfigManager().set(1060, 691625);
        player.getConfigManager().set(1065, -1);
        player.getConfigManager().set(1066, 1579941888);
        player.getConfigManager().set(1067, -1296039936);
        player.getConfigManager().set(1074, 0);
        player.getConfigManager().set(1075, -1);
        player.getConfigManager().set(1102, 40);
        player.getConfigManager().set(1103, 106);
        player.getConfigManager().set(1105, 17);
        player.getConfigManager().set(1107, 1);
        player.getConfigManager().set(1111, 8);
        player.getConfigManager().set(1112, 1);
        player.getConfigManager().set(1117, 58720256);
        player.getConfigManager().set(1132, 50400);
        player.getConfigManager().set(1133, 1);
        player.getConfigManager().set(1135, 2);
        player.getConfigManager().set(1137, 24);
        player.getConfigManager().set(1138, 167);
        player.getConfigManager().set(1151, -1);
        player.getConfigManager().set(1176, 277348352);
        player.getConfigManager().set(1178, 4325376);
        player.getConfigManager().set(1180, 8692288);
        player.getConfigManager().set(1182, 536879104);
        player.getConfigManager().set(1183, 4104);
        player.getConfigManager().set(1186, 1111490946);
        player.getConfigManager().set(1187, 8);
        player.getConfigManager().set(1188, 1019);
        player.getConfigManager().set(1192, 4196740);
        player.getConfigManager().set(1194, 1216512);
        player.getConfigManager().set(1197, 8);
        player.getConfigManager().set(1198, 1080100864);
        player.getConfigManager().set(1200, 4);
        player.getConfigManager().set(1224, -2044690427);
        player.getConfigManager().set(1225, 379887844);
        player.getConfigManager().set(1226, 6156);
        player.getConfigManager().set(1227, 384);
        player.getConfigManager().set(1240, 9449);
        player.getConfigManager().set(1243, 37501);
        player.getConfigManager().set(1264, 61512);
        player.getConfigManager().set(1267, 136594);
        player.getConfigManager().set(1306, 1);
        player.getConfigManager().set(1317, -2147483648);
        player.getConfigManager().set(1338, 18796);
        player.getConfigManager().set(1354, 1);
        player.getConfigManager().set(1373, 524289);
        player.getConfigManager().set(1374, 522);
        player.getConfigManager().set(1375, 155665);
        player.getConfigManager().set(1380, 517);
        player.getConfigManager().set(1427, -1);
        player.getConfigManager().set(1429, 8);
        player.getConfigManager().set(1535, 134221703);
        player.getConfigManager().set(1570, 1);
        player.getConfigManager().set(1586, 1);


        /*player.getConfigManager().set(18, 1);
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
        player.getConfigManager().set(1427, -1);*/

        player.sendMessage("Welcome to Inferno #155!");

    }

    private static void sendVarp(Player player, int id, int value) {
        player.getConfigManager().set(id, value);
    }

}