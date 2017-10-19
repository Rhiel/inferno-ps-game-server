package org.arios.game.node.entity.player.link;

import org.arios.game.node.entity.player.Player;

public class InterfaceSetManager {

    public static void sendSets(Player player, int windowMode) {
        switch(windowMode) {
            case 1:
                player.getPacketDispatch().sendInterfaceSet(161, 29, 548, 23);
                player.getPacketDispatch().sendInterfaceSet(161, 13, 548, 20);
                player.getPacketDispatch().sendInterfaceSet(161, 3, 548, 13);
                player.getPacketDispatch().sendInterfaceSet(161, 6, 548, 15);
                player.getPacketDispatch().sendInterfaceSet(161, 66, 548, 63);
                player.getPacketDispatch().sendInterfaceSet(161, 68, 548, 65);
                player.getPacketDispatch().sendInterfaceSet(161, 69, 548, 66);
                player.getPacketDispatch().sendInterfaceSet(161, 70, 548, 67);
                player.getPacketDispatch().sendInterfaceSet(161, 71, 548, 68);
                player.getPacketDispatch().sendInterfaceSet(161, 72, 548, 69);
                player.getPacketDispatch().sendInterfaceSet(161, 73, 548, 70);
                player.getPacketDispatch().sendInterfaceSet(161, 74, 548, 71);
                player.getPacketDispatch().sendInterfaceSet(161, 75, 548, 72);
                player.getPacketDispatch().sendInterfaceSet(161, 76, 548, 73);
                player.getPacketDispatch().sendInterfaceSet(161, 77, 548, 74);
                player.getPacketDispatch().sendInterfaceSet(161, 78, 548, 75);
                player.getPacketDispatch().sendInterfaceSet(161, 79, 548, 76);
                player.getPacketDispatch().sendInterfaceSet(161, 80, 548, 77);
                player.getPacketDispatch().sendInterfaceSet(161, 81, 548, 78);
                player.getPacketDispatch().sendInterfaceSet(161, 4, 548, 14);
                player.getPacketDispatch().sendInterfaceSet(161, 9, 548, 18);
                player.getPacketDispatch().sendInterfaceSet(161, 28, 548, 10);
                player.getPacketDispatch().sendInterfaceSet(161, 7, 548, 16);
                player.getPacketDispatch().sendInterfaceSet(161, 8, 548, 17);
                player.getPacketDispatch().sendInterfaceSet(161, 14, 548, 21);
                break;
            case 2:
                player.getPacketDispatch().sendInterfaceSet(548, 23, 161, 29);
                player.getPacketDispatch().sendInterfaceSet(548, 20, 161, 13);
                player.getPacketDispatch().sendInterfaceSet(548, 13, 161, 3);
                player.getPacketDispatch().sendInterfaceSet(548, 15, 161, 6);
                player.getPacketDispatch().sendInterfaceSet(548, 63, 161, 66);
                player.getPacketDispatch().sendInterfaceSet(548, 65, 161, 68);
                player.getPacketDispatch().sendInterfaceSet(548, 66, 161, 69);
                player.getPacketDispatch().sendInterfaceSet(548, 67, 161, 70);
                player.getPacketDispatch().sendInterfaceSet(548, 68, 161, 71);
                player.getPacketDispatch().sendInterfaceSet(548, 69, 161, 72);
                player.getPacketDispatch().sendInterfaceSet(548, 70, 161, 73);
                player.getPacketDispatch().sendInterfaceSet(548, 71, 161, 74);
                player.getPacketDispatch().sendInterfaceSet(548, 72, 161, 75);
                player.getPacketDispatch().sendInterfaceSet(548, 73, 161, 76);
                player.getPacketDispatch().sendInterfaceSet(548, 74, 161, 77);
                player.getPacketDispatch().sendInterfaceSet(548, 75, 161, 78);
                player.getPacketDispatch().sendInterfaceSet(548, 76, 161, 79);
                player.getPacketDispatch().sendInterfaceSet(548, 77, 161, 80);
                player.getPacketDispatch().sendInterfaceSet(548, 78, 161, 81);
                player.getPacketDispatch().sendInterfaceSet(548, 14, 161, 4);
                player.getPacketDispatch().sendInterfaceSet(548, 18, 161, 9);
                player.getPacketDispatch().sendInterfaceSet(548, 10, 161, 28);
                player.getPacketDispatch().sendInterfaceSet(548, 16, 161, 7);
                player.getPacketDispatch().sendInterfaceSet(548, 17, 161, 8);
                player.getPacketDispatch().sendInterfaceSet(548, 21, 161, 14);
                break;
            case 3://lobby to fixed gameframe
                player.getPacketDispatch().sendInterfaceSet(165, 1, 548, 23);
                player.getPacketDispatch().sendInterfaceSet(165, 6, 548, 20);
                player.getPacketDispatch().sendInterfaceSet(165, 2, 548, 13);
                player.getPacketDispatch().sendInterfaceSet(165, 3, 548, 15);
                player.getPacketDispatch().sendInterfaceSet(165, 7, 548, 63);
                player.getPacketDispatch().sendInterfaceSet(165, 8, 548, 65);
                player.getPacketDispatch().sendInterfaceSet(165, 9, 548, 66);
                player.getPacketDispatch().sendInterfaceSet(165, 10, 548, 67);
                player.getPacketDispatch().sendInterfaceSet(165, 11, 548, 68);
                player.getPacketDispatch().sendInterfaceSet(165, 12, 548, 69);
                player.getPacketDispatch().sendInterfaceSet(165, 13, 548, 70);
                player.getPacketDispatch().sendInterfaceSet(165, 14, 548, 71);
                player.getPacketDispatch().sendInterfaceSet(165, 15, 548, 72);
                player.getPacketDispatch().sendInterfaceSet(165, 16, 548, 73);
                player.getPacketDispatch().sendInterfaceSet(165, 17, 548, 74);
                player.getPacketDispatch().sendInterfaceSet(165, 18, 548, 75);
                player.getPacketDispatch().sendInterfaceSet(165, 19, 548, 76);
                player.getPacketDispatch().sendInterfaceSet(165, 20, 548, 77);
                player.getPacketDispatch().sendInterfaceSet(165, 21, 548, 78);
                player.getPacketDispatch().sendInterfaceSet(165, 22, 548, 14);
                player.getPacketDispatch().sendInterfaceSet(165, 23, 548, 18);
                player.getPacketDispatch().sendInterfaceSet(165, 24, 548, 10);
                player.getPacketDispatch().sendInterfaceSet(165, 4, 548, 16);
                player.getPacketDispatch().sendInterfaceSet(165, 5, 548, 17);
                player.getPacketDispatch().sendInterfaceSet(165, 30, 548, 21);
                break;
            case 4://lobby to resizable gameframe
                player.getPacketDispatch().sendInterfaceSet(165, 1, 161, 29);
                player.getPacketDispatch().sendInterfaceSet(165, 6, 161, 13);
                player.getPacketDispatch().sendInterfaceSet(165, 2, 161, 3);
                player.getPacketDispatch().sendInterfaceSet(165, 3, 161, 6);
                player.getPacketDispatch().sendInterfaceSet(165, 7, 161, 66);
                player.getPacketDispatch().sendInterfaceSet(165, 8, 161, 68);
                player.getPacketDispatch().sendInterfaceSet(165, 9, 161, 69);
                player.getPacketDispatch().sendInterfaceSet(165, 10, 161, 70);
                player.getPacketDispatch().sendInterfaceSet(165, 11, 161, 71);
                player.getPacketDispatch().sendInterfaceSet(165, 12, 161, 72);
                player.getPacketDispatch().sendInterfaceSet(165, 13, 161, 73);
                player.getPacketDispatch().sendInterfaceSet(165, 14, 161, 74);
                player.getPacketDispatch().sendInterfaceSet(165, 15, 161, 75);
                player.getPacketDispatch().sendInterfaceSet(165, 16, 161, 76);
                player.getPacketDispatch().sendInterfaceSet(165, 17, 161, 77);
                player.getPacketDispatch().sendInterfaceSet(165, 18, 161, 78);
                player.getPacketDispatch().sendInterfaceSet(165, 19, 161, 79);
                player.getPacketDispatch().sendInterfaceSet(165, 20, 161, 80);
                player.getPacketDispatch().sendInterfaceSet(165, 21, 161, 81);
                player.getPacketDispatch().sendInterfaceSet(165, 22, 161, 4);
                player.getPacketDispatch().sendInterfaceSet(165, 23, 161, 9);
                player.getPacketDispatch().sendInterfaceSet(165, 24, 161, 28);
                player.getPacketDispatch().sendInterfaceSet(165, 4, 161, 7);
                player.getPacketDispatch().sendInterfaceSet(165, 5, 161, 8);
                player.getPacketDispatch().sendInterfaceSet(165, 30, 161, 14);
        }
    }

}
