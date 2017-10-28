package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.content.dialogue.DialogueAction;
import org.arios.game.content.skill.Skills;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.RunScript;
import org.arios.game.world.GameWorld;
import org.arios.plugin.Plugin;

/**
 * Represents the plugin used to handle the skilling tab.
 * @author 'Vexia
 * @version 1.0
 */
public final class SkillTabInterface extends ComponentPlugin {

	@Override
	public Plugin<Object> newInstance(Object arg) throws Throwable {
		ComponentDefinition.put(320, this);
		return this;
	}

	@Override
	public boolean handle(Player p, Component component, int opcode, int button, int slot, int itemId) {
		final SkillConfig config = SkillConfig.forId(button);
		if (config == null) {
			return true;
		}
		if (!GameWorld.isEconomyWorld()) {
			p.getDialogueInterpreter().sendOptions("Select an Option", "Skill Menu", "Set Skill");
			p.getDialogueInterpreter().addAction(new DialogueAction() {

				@Override
				public void handle(Player player, int buttonId) {
					switch (buttonId) {
						case 1:
							player.getPulseManager().clear();
							player.getInterfaceManager().open(new Component(499));
							player.getConfigManager().set(965, config.getConfig());
							player.getAttributes().put("skillMenu", config.getConfig());
							break;
						case 2:
							if (player.inCombat() || player.getLocks().isInteractionLocked() || player.getSkullManager().isWilderness() || player.getAttribute("activity", null) != null) {
								player.getPacketDispatch().sendMessage("You can't do that right now.");
								return;
							}
							player.getDialogueInterpreter().sendInput(false, "Enter the level:");
							player.setAttribute("runscript", new RunScript() {

								@Override
								public boolean handle() {
									int val = (int) getValue();
									int slot = 0;
									if (val > 99) {
										val = 99;
									} else if (val < 1) {
										val = 1;
									}
									for (int i = 0; i < Skills.SKILL_NAME.length; i++) {
										if (Skills.SKILL_NAME[i].toLowerCase().startsWith(config.name().toLowerCase())) {
											slot = i;
											break;
										}
									}
									if ((slot == Skills.ATTACK || slot == Skills.DEFENCE || slot == Skills.STRENGTH || slot == Skills.HITPOINTS || slot == Skills.RANGE || slot == Skills.MAGIC) && player.getEquipment().itemCount() != 0) {
										player.sendMessage("You can't have equipment on when setting a level.");
										return true;
									}
									player.getSkills().setLevel(slot, val);
									player.getSkills().setStaticLevel(slot, val);
									player.getSkills().updateCombatLevel();
									player.getAppearance().sync();
									return true;
								}

							});
							break;
					}
				}

			});
			return true;
		}
		p.getPulseManager().clear();
		p.getInterfaceManager().open(new Component(214));
		p.getConfigManager().set(965, config.getConfig());
		p.getAttributes().put("skillMenu", config.getConfig());
		return true;
	}

	public enum SkillConfig {
		ATTACK(1, 1),
		STRENGTH(2, 2),
		DEFENCE(3, 5),
		RANGE(4, 3),
		PRAYER(5, 7),
		MAGIC(6, 4),
		RUNECRAFTING(7, 12),
		HITPOINTS(9, 6),
		AGILITY(10, 8),
		HERBLORE(11, 9),
		THIEVING(12, 10),
		CRAFTING(13, 11),
		FLETCHING(14, 19),
		SLAYER(15, 20),
		MINING(17, 13),
		SMITHING(18, 14),
		FISHING(19, 15),
		COOKING(20, 16),
		FIREMAKING(21, 17),
		WOODCUTTING(22, 18),
		FARMING(23, 21),
		CONSTRUCTION(8, 22),
		HUNTER(16, 23);

		/**
		 * Constructs a new {@code SkillConfig} {@code Object}.
		 * @param button the button.
		 * @param config the config.
		 */
		SkillConfig(int button, int config) {
			this.button = button;
			this.config = config;
		}

		/**
		 * Represents the button.
		 */
		private int button;

		/**
		 * Represents the config.
		 */
		private int config;

		/**
		 * Gets the skill config.
		 * @param id the id.
		 * @return the skill config.
		 */
		public static SkillConfig forId(int id) {
			for (SkillConfig config : SkillConfig.values()) {
				if (config.button == id)
					return config;
			}
			return null;
		}

		/**
		 * Gets the button.
		 * @return the button.
		 */
		public int getButton() {
			return button;
		}

		/**
		 * Gets the config.
		 * @return the config.
		 */
		public int getConfig() {
			return config;
		}
	}
}
