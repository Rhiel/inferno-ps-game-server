package org.arios.game.world.update.flag.npc;

import org.arios.game.node.entity.Entity;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.game.world.update.flag.context.HitMark;
import org.arios.net.packet.IoBuffer;

/**
 * The NPC's main hit update flag.
 * @author Emperor
 */
public final class NPCHitFlag extends UpdateFlag<HitMark> {

    /**
     * Constructs a new {@code NPCHitFlag} {@code Object}.
     *
     * @param context The hit mark.
     */
    public NPCHitFlag(HitMark context) {
        super(context);
    }

    @Override
    public void write(IoBuffer outgoing) {
        Entity e = context.getEntity();
        outgoing.putA(1); //Amount of hits.
        if (e != null) {
            outgoing.putSmart(32767);
        }
        int type = context.getType();
        if (type != 9) {
            if (context.getDamage() < 1) {
                type = 8;
            } else if (context.getDamage() >= e.getSkills().getLifepoints()) {
                type += 10;
            }
            outgoing.putSmart(type);
        } else {
            outgoing.putSmart(type);
        }
        outgoing.putSmart(context.getDamage());
        if (e != null) {
            outgoing.putSmart(19);//5
            outgoing.putSmart(context.getDamage());
        }
        outgoing.putSmart(0);
        outgoing.putA(0);
        outgoing.putSmart(0);
        outgoing.putSmart(0);
        outgoing.putSmart(0);
        outgoing.putA(0);
        outgoing.putC(0);
    }

    @Override
    public int data() {
        return maskData();
    }

    @Override
    public int ordinal() {
        return 6;
    }

    /**
     * Gets the mask data.
     *
     * @return The mask data.
     */
    public static int maskData() {
        return 0x40;
    }

}