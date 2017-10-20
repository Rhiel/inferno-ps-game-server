package org.arios.game.world.update;

import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.arios.game.node.entity.Entity;
import org.arios.game.node.entity.combat.ImpactHandler;
import org.arios.game.node.entity.combat.ImpactHandler.Impact;
import org.arios.game.node.entity.impl.Animator;
import org.arios.game.node.entity.player.Player;
import org.arios.game.world.update.flag.UpdateFlag;
import org.arios.game.world.update.flag.context.HitMark;
import org.arios.game.world.update.flag.npc.NPCHitFlag;
import org.arios.game.world.update.flag.npc.NPCHitFlag1;
import org.arios.game.world.update.flag.player.AppearanceFlag;
import org.arios.game.world.update.flag.player.HitUpdateFlag;
import org.arios.game.world.update.flag.player.HitUpdateFlag1;
import org.arios.net.packet.IoBuffer;

/**
 * Holds an entity's update masks.
 *
 * @author Emperor
 */
public final class UpdateMasks {

    /**
     * The mask data.
     */
    private int maskData = 0;

    /**
     * The update masks array.
     */
    public PriorityQueue<UpdateFlag> flagQueue = new PriorityQueue<UpdateFlag>();

    /**
     * A queue holding all update flags
     */
    private final List<UpdateFlag> queuedUpdates = new LinkedList<UpdateFlag>();

    public boolean isAnimating;

    /**
     * If the update masks are being updated.
     */
    private boolean updating;

    /**
     * The current animation priority.
     */
    private Animator.Priority animationPriority;

    /**
     * Registers an update flag.
     *
     * @param updateFlag The update flag.
     */
    public void register(UpdateFlag updateFlag) {
        if (updating) {
            queuedUpdates.add(updateFlag);
            return;
        }
        if ((maskData & updateFlag.data()) != 0) {
            flagQueue.remove(updateFlag);
        }
        maskData |= updateFlag.data();
        flagQueue.add(updateFlag);
    }

    /**
     * Prepares the outgoing packet for updating.
     *
     * @param e The entity who's using this update mask instance.
     */
    public void prepare(Entity e) {
        if (e.isPlayer()) {
            if (e.asPlayer().getAppearance() != null)
                e.asPlayer().getAppearance().prepareBodyData(e.asPlayer());
            /*if (e.getWalkingQueue().getWalkDir() != -1 || e.getWalkingQueue().getRunDir() != -1) {
				register(new MovementUpdate(e.asPlayer()));
			}*/
        }
		/*ImpactHandler handler = e.getImpactHandler();
		if (handler.getImpactQueue().size() > 0) {
			e.getUpdateMasks().register(new HitUpdate(e));
		}*/
        updating = true;
    }

    public void finish() {
        animationPriority = Animator.Priority.LOW;
        maskData = 0;
        flagQueue.clear();
        updating = false;
        isAnimating = false;
        for (UpdateFlag flag : queuedUpdates) {
            register(flag);
        }
        queuedUpdates.clear();
    }

    public boolean isUpdateRequired() {
        return maskData != 0;
    }


/**
 * Writes the flags.
 * @param p The player.
 * @param e The entity to update.
 * @param buffer The buffer to write on.
 */
	/*public void write(Player p, Entity e, IoBuffer buffer) {
		int maskData = this.maskData;
		if (maskData >= 0x100 && e instanceof Player) {
			maskData |= 0x2; //TODO CHECK THIS
			buffer.put(maskData).put(maskData >> 8);
		} else {
			buffer.put(maskData);
		}
		for (int i = 0; i < masks.size(); i++) {
			UpdateFlag flag = (UpdateFlag) masks.toArray()[i];
			if (flag != null) {
				flag.writeDynamic(buffer, p);
			}
		}
	}*/

/**
 * Writes the update masks on synchronization.
 * @param p The player.
 * @param e The entity to update.
 * @param buffer The buffer to write on.
 * @param appearance If the appearance mask should be written.
 */
	/*public void writeSynced(Player p, Entity e, IoBuffer buffer, boolean appearance) {
		int maskData = this.maskData;
		int synced = this.syncedMask;
		if (!appearance && (synced & AppearanceFlag.getData()) != 0) {
			synced &= ~AppearanceFlag.getData();
		}
		maskData |= synced;
		if (maskData >= 0x100 && e instanceof Player) {
			maskData |= 0x2; //TODO CHECK THIS
			buffer.put(maskData).put(maskData >> 8);
		} else {
			buffer.put(maskData);
		}
		for (int i = 0; i < masks.size(); i++) {
			UpdateFlag flag = (UpdateFlag) masks.toArray()[i];
			if (flag == null) {
				flag = syncedMasks[i];
				if (!appearance && flag instanceof AppearanceFlag) {
					continue;
				}
			}
			if (flag != null) {
				flag.writeDynamic(buffer, p);
			}
		}
	}*/

/**
 * Adds the dynamic update flags.
 * @param entity The entity.
 */
	/*public void prepare(Entity entity) {
		ImpactHandler handler = entity.getImpactHandler();
		for (int i = 0; i < 2; i++) {
			if (handler.getImpactQueue().peek() == null) {
				break;
			}
			Impact impact = handler.getImpactQueue().poll();
			registerHitUpdate(entity, impact, i == 1);
		}
		updating.set(true);
	}*/

/**
 * Registers the hit update for the given {@link Impact}.
 * @param e The entity.
 * @param impact The impact to update.
 * @param secondary If the hit update is secondary.
 */
	/*private HitMark registerHitUpdate(Entity e, Impact impact, boolean secondary) {
		boolean player = e instanceof Player;
		HitMark mark = new HitMark(impact.getAmount(), impact.getType().ordinal(), e);
		if (player) {
			register(secondary ? new HitUpdateFlag1(mark) : new HitUpdateFlag(mark));
		} else {
			register(secondary ? new NPCHitFlag1(mark) : new NPCHitFlag(mark));
		}
		return mark;
	}*/

/**
 * Resets the update masks.
 */
	/*public void reset() {
		masks.clear();
		maskData = 0;
		updating.set(false);
	}*/

/**
 * Gets the appearanceStamp.
 * @return The appearanceStamp.
 */
	/*public long getAppearanceStamp() {
		return appearanceStamp;
	}*/

/**
 * Sets the appearanceStamp.
 * @param appearanceStamp The appearanceStamp to set.
 */
	/*public void setAppearanceStamp(long appearanceStamp) {
		this.appearanceStamp = appearanceStamp;
	}
*/
/**
 * Checks if an update is required.
 * @return {@code True} if so.
 */
	/*public boolean isUpdateRequired() {
		return maskData != 0;
	}
*/
/**
 * Checks if synced update masks have been registered.
 * @return {@code True} if so.
 */
	/*public boolean hasSynced() {
		return syncedMask != 0;
	}*/
}