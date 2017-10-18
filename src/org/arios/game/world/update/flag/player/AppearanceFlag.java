package org.arios.game.world.update.flag.player;

import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.appearance.Appearance;
import org.arios.game.node.entity.player.link.appearance.BodyPart;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.net.packet.IoBuffer;
import org.arios.tools.StringUtils;

/**
 * Handles the appearance update flag.
 * @author Emperor
 */
public final class AppearanceFlag extends UpdateFlag<Player> {

    /**
     * Constructs a new {@code AppearanceFlag} {@code Object}.
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
	block.put(settings); // settings hash.
	block.put(appearance.getSkullIcon()); // Skull icon
	block.put(appearance.getHeadIcon()); // Head icon
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
	    block.putShort(-1);
	    block.putShort(npcId);
	}
	final BodyPart[] colors = new BodyPart[] { appearance.getHair(), appearance.getTorso(), appearance.getLegs(), appearance.getFeet(), appearance.getSkin() };
	for (int i = 0; i < colors.length; i++) {// colours
	    block.put(colors[i].getColor());
	}
	block.putShort(appearance.getStandAnimation());
	block.putShort(appearance.getStandTurnAnimation());
	block.putShort(appearance.getWalkAnimation());
	block.putShort(appearance.getTurn180());
	block.putShort(appearance.getTurn90cw());
	block.putShort(appearance.getTurn90ccw());
	block.putShort(appearance.getRunAnimation());
	block.putLong(StringUtils.stringToLong(context.getName(true)));
	boolean title = context.getIronmanManager().isIronman();
	block.put((byte) (title ? 1 : 0));
	if (title) {
	    // String name =
	    // context.getSavedData().getSpawnData().getTitle().getName();
	    // String color =
	    // context.getSavedData().getSpawnData().getTitle().getTitleColor();
	    block.putLong(StringUtils.stringToLong("" + (context.getIronmanManager().getMode().getIcon() - 1)));
	    // block.putLong(StringUtils.stringToLong(color));
	}
	block.put(context.getProperties().getCurrentCombatLevel());
	if (context.getSkullManager().isWilderness() && !context.getFamiliarManager().isUsingSummoning()) {
	    block.put(context.getFamiliarManager().getSummoningCombatLevel());
	} else {
	    block.put(0);
	}
	buffer.put(block.toByteBuffer().position());
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
     * @return The flag ordinal.
     */
    public static int getOrdinal() {
	return 9;
    }

    /**
     * Gets the mask data.
     * @return The mask data.
     */
    public static int getData() {
	return 0x2;
    }
}