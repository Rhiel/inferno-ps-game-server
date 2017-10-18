package org.arios.net.packet.out;

import org.arios.game.content.skill.Skills;
import org.arios.net.packet.IoBuffer;
import org.arios.net.packet.OutgoingPacket;
import org.arios.net.packet.context.SkillContext;

/**
 * Handles the update skill outgoing packet.
 * @author Emperor
 */
public final class SkillLevel implements OutgoingPacket<SkillContext> {

    @Override
    public void send(SkillContext context) {
	final IoBuffer buffer = new IoBuffer(239);
	Skills skills = context.getPlayer().getSkills();
	buffer.putIntA((int) skills.getExperience(context.getSkillId())).putS(context.getSkillId());
	if (context.getSkillId() == Skills.PRAYER) {
	    buffer.putA((int) Math.ceil(skills.getPrayerPoints()));
	} else if (context.getSkillId() == Skills.HITPOINTS) {
	    buffer.putA(skills.getLifepoints());
	} else {
	    buffer.putA(skills.getLevel(context.getSkillId(), true));
	}
	context.getPlayer().getDetails().getSession().write(buffer);
    }

}