package org.arios.game.world.update.flag.player;

import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.game.world.update.flag.context.HitMark;
import org.arios.net.packet.IoBuffer;

/**
 * The supportive hit update flag.
 * @author Emperor
 */
public final class HitUpdateFlag1 extends UpdateFlag<HitMark> {

    /**
     * Constructs a new {@code HitUpdateFlag1} {@code Object}.
     * @param context The hit mark.
     */
    public HitUpdateFlag1(HitMark context) {
	super(context);
    }

    @Override
    public void write(IoBuffer buffer) {
	buffer.put(context.getDamage()).put(context.getType());
    }

    @Override
    public int data() {
	return maskData();
    }

    @Override
    public int ordinal() {
	return 8;
    }

    /**
     * Gets the mask data.
     * @return The mask data.
     */
    public static int maskData() {
	return 0x200;
    }

}