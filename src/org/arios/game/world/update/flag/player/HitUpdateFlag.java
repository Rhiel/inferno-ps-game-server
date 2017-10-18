package org.arios.game.world.update.flag.player;

import org.arios.game.node.entity.Entity;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.game.world.update.flag.context.HitMark;
import org.arios.net.packet.IoBuffer;

/**
 * The main hit update flag.
 * @author Emperor
 */
public final class HitUpdateFlag extends UpdateFlag<HitMark> {

    /**
     * Constructs a new {@code HitUpdateFlag} {@code Object}.
     * @param context The hit mark.
     */
    public HitUpdateFlag(HitMark context) {
	super(context);
    }

    @Override
    public void write(IoBuffer buffer) {
	Entity e = context.getEntity();
	int ratio = e.getSkills().getLifepoints() * 30 / e.getSkills().getMaximumLifepoints();
	buffer.putC(context.getDamage()).putA(context.getType()).put(ratio);
    }

    @Override
    public int data() {
	return maskData();
    }

    @Override
    public int ordinal() {
	return 2;
    }

    /**
     * Gets the mask data.
     * @return The mask data.
     */
    public static int maskData() {
	return 0x80;
    }

}