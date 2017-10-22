package org.arios.game.world.update.flag.player;

import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.appearance.Appearance;
import org.arios.game.node.entity.player.link.appearance.BodyPart;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.net.packet.IoBuffer;
import org.arios.tools.StringUtils;

/**
 * Handles the appearance update flag.
 *
 * @author Emperor
 */
public final class AppearanceFlag extends UpdateFlag<Player> {

    /**
     * Constructs a new {@code AppearanceFlag} {@code Object}.
     *
     * @param player The player.
     */
    public AppearanceFlag(Player player) {
        super(player);
    }

    @Override
    public void write(IoBuffer buffer) {
        Appearance appearance = context.getAppearance();
        appearance.prepareBodyData(context);
        IoBuffer block = new IoBuffer();
        int settings = appearance.getGender().toByte();
        if (context.size() > 1) {
            settings |= (context.size() - 1) << 3;
        }
        block.put(settings); //settings hash.
        block.put(appearance.getSkullIcon()); //Skull icon
        block.put(appearance.getHeadIcon()); //Head icon
        int npcId = appearance.getNpcId();
        if (npcId == -1) {
            int[] parts = appearance.getBodyParts();
            for (int i = 0; i < 12; i++) {
                int value = parts[i];
                if (value == 0) {
                    block.put(0);
                } else {
                    block.putShort(value);
                }
            }
        } else {
            block.put(255).put(255).putShort(npcId);
        }
        final BodyPart[] colors = new BodyPart[] {appearance.getHair(), appearance.getTorso(), appearance.getLegs(), appearance.getFeet(), appearance.getSkin()};
        for (int i = 0; i < colors.length; i++) {//colours
            block.put(colors[i].getColor());
        }
        block.putShort(appearance.getStandAnimation());
        block.putShort(appearance.getStandTurnAnimation());
        block.putShort(appearance.getWalkAnimation());
        block.putShort(appearance.getTurn180());
        block.putShort(appearance.getTurn90cw());
        block.putShort(appearance.getTurn90ccw());
        block.putShort(appearance.getRunAnimation());
        block.putString(context.getName(true));
        block.put(context.getProperties().getCombatLevel());
        block.putShort(context.getSkills().getTotalLevel()); //TODO CHECK THIS SHORT AND CLEAN UP ABOVE!
        block.put(0);//is hidden
        buffer.putA(block.toByteBuffer().position());
        buffer.putA(block);
    }

    @Override
    public int data() {
        return getData();
    }

    @Override
    public int ordinal() {
        return getOrdinal();
    }

    /**
     * Gets the ordinal for this flag.
     *
     * @return The flag ordinal.
     */
    public static int getOrdinal() {
        return 2;
    }

    /**
     * Gets the mask data.
     *
     * @return The mask data.
     */
    public static int getData() {
        return 0x2;
    }
}