package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.content.global.Lamps;
import org.arios.game.content.skill.Skills;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.audio.Audio;
import org.arios.game.node.item.Item;
import org.arios.plugin.Plugin;

/**
 * Represents the experience lamp interface.
 * @author 'Vexia
 * @version 1.0
 */
public final class ExperienceLampInterface extends ComponentPlugin {

    /**
     * Represents the sound to send.
     */
    private static final Audio SOUND = new Audio(1270, 12, 1);

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	ComponentDefinition.put(134, this);
	return this;
    }

    @Override
    public boolean handle(Player player, Component component, int opcode, int button, int slot, int itemId) {
	Item lamp = player.getAttribute("lamp", null);
	if (lamp == null) {
	    return true;
	}
	if (button != 27) {
	    player.setAttribute("lamp:experience", button);
	}
	if (button == 11 && !player.getQuestRepository().isComplete("Drudic Ritual")) {
	    player.getPacketDispatch().sendMessage("You need to complete the Druidic Ritual quest to start that skill.");
	    return true;
	}
	if (button == 26 && !player.getQuestRepository().isComplete("Wolf Whistle")) {
	    player.getPacketDispatch().sendMessage("You need to complete the Wolf Whistle quest to start that skill.");
	    return true;
	}
	if (button == 14 && !player.getQuestRepository().isComplete("Rune Mysteries")) {
	    player.getPacketDispatch().sendMessage("You need to complete the Rune Mysteries quest to start that skill.");
	    return true;
	}
	if (button == 27) {
	    if (!player.getAttributes().containsKey("lamp:experience")) {
		player.getPacketDispatch().sendMessage("You need to pick the skill you wish to gain the experience in first.");
		return true;
	    }
	    final SkillInterface skillType = SkillInterface.forId((int) player.getAttribute("lamp:experience"));
	    if (skillType == null || skillType.button == 24) {
		return true;
	    }
	    if (skillType == SkillInterface.RUNECRAFTING && !player.getQuestRepository().isComplete("Rune Mysteries")) {
		player.getPacketDispatch().sendMessage("You need to complete the Rune Mysteries quest to start that skill.");
		return true;
	    }
	    if (!player.getInventory().containsItem(lamp)) {
		return true;
	    }
	    Lamps type = Lamps.forItem(lamp);
	    if (player.getSkills().getStaticLevel(skillType.skill) <= type.getLevelRequirement()) {
		player.sendMessage("You need a level in this skill which is greater than " + type.getLevelRequirement() + ".");
		return true;
	    }
	    player.getAudioManager().send(SOUND);
	    player.getInventory().remove(lamp);
	    player.getInterfaceManager().close();
	    int x = player.getSkills().getStaticLevel(skillType.skill);
	    int modifier = 10;
	    int experience = x * modifier;
	    if (type != null && type != Lamps.GENIE_LAMP) {
		experience = (int) type.getExp();
	    }
	    player.getSkills().addExperience(skillType.skill, experience, false);
	    player.getDialogueInterpreter().open(70099, new Object[] { "The lamp gives you " + experience + " " + Skills.SKILL_NAME[skillType.skill] + " experience." });
	}
	return true;
    }

    /**
     * Representa a skill to gain experience.
     * @author 'Vexia
     */
    public enum SkillInterface {
	ATTACK(3, Skills.ATTACK), STRENGTH(4, Skills.STRENGTH), RANGE(5, Skills.RANGE), MAGIC(6, Skills.MAGIC), DEFENCE(7, Skills.DEFENCE), CRAFTING(13, Skills.CRAFTING), HITPOINTS(8, Skills.HITPOINTS), PRAYER(9, Skills.PRAYER), AGILITY(10, Skills.AGILITY), HERBLORE(11, Skills.HERBLORE), THIEVING(12, Skills.THIEVING), FISHING(17, Skills.FISHING), RUNECRAFTING(14, Skills.RUNECRAFTING), SLAYER(22, Skills.SLAYER), FARMING(23, Skills.FARMING), MINING(15, Skills.MINING), SMITHING(16, Skills.SMITHING), HUNTER(25, Skills.HUNTER), SUMMONING(26, Skills.SUMMONING), COOKING(18, Skills.COOKING), FIREMAKING(19, Skills.FIREMAKING), WOODCUTTING(20, Skills.WOODCUTTING), FLETCHING(21, Skills.FLETCHING);

	/**
	 * Constructs a new {@code ExperienceLampInterface} {@code Object}.
	 * @param button the button.
	 * @param skill the skill.
	 */
	SkillInterface(int button, int skill) {
	    this.button = button;
	    this.skill = skill;
	}

	/**
	 * The button id.
	 */
	private final int button;

	/**
	 * The skill id.
	 */
	private final int skill;

	/**
	 * Gets the skill type for the button.
	 * @param id the id.
	 * @return the skill type.
	 */
	public static SkillInterface forId(int id) {
	    for (SkillInterface lamp : SkillInterface.values()) {
		if (lamp.button == id) {
		    return lamp;
		}
	    }
	    return null;
	}
    }
}