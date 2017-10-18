package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.container.impl.EquipmentContainer;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.content.global.tutorial.TutorialStage;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.EmoteData;
import org.arios.game.node.entity.player.link.diary.DiaryType;
import org.arios.game.node.item.Item;
import org.arios.game.world.update.flag.context.Animation;
import org.arios.plugin.Plugin;

/**
 * Represents the emote tab interface
 * @author Emperor
 * @version 1.0
 */
public final class EmoteTabInterface extends ComponentPlugin {

    /**
     * Represents the skillcape info.
     */
    private static final int SKILLCAPE_INFO[][] = { { 9747, 9748, 823, 4959 }, { 9750, 9751, 828, 4981 }, { 9753, 9754, 824, 4961 }, { 9756, 9757, 832, 4973 }, { 9759, 9760, 829, 4979 }, { 9762, 9763, 813, 4939 }, { 9765, 9766, 817, 4947 }, { 9768, 9769, 833, 4971 }, { 9771, 9772, 830, 4977 }, { 9774, 9775, 835, 4969 }, { 9777, 9778, 826, 4965 }, { 9780, 9781, 818, 4949 }, { 9783, 9784, 812, 4937 }, { 9786, 9787, 827, 4967 }, { 9789, 9790, 820, 4953 }, { 9792, 9793, 814, 4941 }, { 9795, 9796, 815, 4943 }, { 9798, 9799, 819, 4951 }, { 9801, 9802, 821, 4955 }, { 9804, 9805, 831, 4975 }, { 9807, 9808, 822, 4957 }, { 9810, 9811, 825, 4963 }, { 12169, 12170, 1515, 8525 }, { 9813, -1, 816, 4945 }, { 9948, 9949, 907, 5158 } };

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
	ComponentDefinition.put(464, this);
	return this;
    }

    @Override
    public boolean handle(Player player, Component component, int opcode, int button, int slot, int itemId) {
	if (player.getLocks().isLocked("emote")) {
	    player.getPacketDispatch().sendMessage("You're already doing an emote!");
	    return true;
	}
	if (player.getProperties().getCombatPulse().isAttacking() || player.inCombat()) {
	    player.getPacketDispatch().sendMessage("You can't perform an emote while being in combat.");
	    return true;
	}
	player.getPulseManager().clear();
	EmoteData emoteData = player.getEmotes();
	if (emoteData != null) {
	    int emoteId = button - 2;
	    if (emoteId >= 0 && !emoteData.getUnlocked()[emoteId] && emoteId != 37) {
		String message = EmoteData.LOCKED_MESSAGE[emoteId];
		if (message == null) {
		    message = "You can't use this emote.";
		}
		player.getDialogueInterpreter().sendDialogue(message);
		return true;
	    }
	}
	if (TutorialSession.getExtension(player).getStage() == 24) {
	    TutorialStage.load(player, 25, false);
	}
	if (!player.getAchievementDiaryManager().getDiary(DiaryType.VARROCK).isComplete(1, 3) && (button >= 30 && button <= 33)) {
	    if (!player.getAttribute("emote-" + button, false)) {
		player.setAttribute("emote-" + button, true);
	    }
	    boolean good = true;
	    int b = 33;
	    for (int i = 0; i < 4; i++) {
		if (i != 0) {
		    b = i == 1 ? 31 : i == 2 ? 32 : 30;
		}
		if (!player.getAttribute("emote-" + b, false)) {
		    good = false;
		    break;
		}
	    }
	    if (!player.getAchievementDiaryManager().getDiary(DiaryType.VARROCK).isComplete(1, 3)) {
		player.getAchievementDiaryManager().getDiary(DiaryType.VARROCK).updateTask(player, 1, 3, good);
	    }
	}
	Item item = player.getEquipment().get(EquipmentContainer.SLOT_WEAPON);
	if (item != null) {
	    switch (item.getId()) {
	    case 4084:
		player.getPacketDispatch().sendAnimation(1483);
		return true;
	    }
	}
	Animation animation = null;
	switch (opcode) {
	case 180:
	case 230:
	    switch (button) {
	    case 2:
		animation = Animation.create(855);
		break;
	    case 3:
		animation = Animation.create(856);
		break;
	    case 4:
		Item legs = player.getEquipment().get(EquipmentContainer.SLOT_LEGS);
		if (legs != null && legs.getId() == 10396) {
		    animation = Animation.create(5312);
		    break;
		}
		animation = Animation.create(858);
		break;
	    case 5:
		Item hat = player.getEquipment().get(EquipmentContainer.SLOT_HAT);
		if (hat != null && hat.getId() == 10392) {
		    animation = Animation.create(5315);
		    break;
		}
		animation = Animation.create(859);
		break;
	    case 6:
		animation = Animation.create(857);
		break;
	    case 7:
		animation = Animation.create(863);
		break;
	    case 8:
		animation = Animation.create(2113);
		break;
	    case 9:
		animation = Animation.create(862);
		break;
	    case 10:
		animation = Animation.create(864);
		break;
	    case 11:
		animation = Animation.create(861);
		break;
	    case 12:
		animation = Animation.create(2109);
		break;
	    case 13:
		hat = player.getEquipment().get(EquipmentContainer.SLOT_HAT);
		if (hat != null && hat.getId() == 10398) {
		    animation = Animation.create(5313);
		    break;
		}
		animation = Animation.create(2111);
		break;
	    case 14:
		legs = player.getEquipment().get(EquipmentContainer.SLOT_LEGS);
		player.setAttribute("emote_end", (long) (System.currentTimeMillis() + 4000));
		if (legs != null && legs.getId() == 10394) {
		    animation = Animation.create(5316);
		    break;
		}
		animation = Animation.create(866);
		break;
	    case 15:
		animation = Animation.create(2106);
		break;
	    case 16:
		animation = Animation.create(2107);
		break;
	    case 17:
		animation = Animation.create(2108);
		break;
	    case 18:
		animation = Animation.create(860);
		break;
	    case 19:
		player.getPacketDispatch().sendGraphic(574);
		animation = Animation.create(1368);
		break;
	    case 20:
		animation = Animation.create(2105);
		break;
	    case 21:
		animation = Animation.create(2110);
		break;
	    case 22:
		animation = Animation.create(865);
		break;
	    case 23:
		animation = Animation.create(2112);
		break;
	    case 30:
		animation = Animation.create(4275);
		break;
	    case 31:
		animation = Animation.create(4278);
		break;
	    case 32:
		Item head = player.getEquipment().get(EquipmentContainer.SLOT_HAT);
		Item wings = player.getEquipment().get(EquipmentContainer.SLOT_CHEST);
		legs = player.getEquipment().get(EquipmentContainer.SLOT_LEGS);
		Item feet = player.getEquipment().get(EquipmentContainer.SLOT_FEET);
		if (head != null && wings != null && legs != null && feet != null) {
		    if (head.getId() == 11021 && wings.getId() == 11020 && legs.getId() == 11022 && feet.getId() == 11019) {
			animation = Animation.create(3859);
			break;
		    }
		}
		animation = Animation.create(4280);
		break;
	    case 33:
		player.getPacketDispatch().sendGraphic(712);
		animation = Animation.create(4276);
		break;
	    case 34:
		animation = Animation.create(3544);
		break;
	    case 35:
		animation = Animation.create(3543);
		break;
	    case 36:
		player.getPacketDispatch().sendGraphic(1244);
		animation = Animation.create(7272);
		break;
	    case 37:
		animation = Animation.create(2836);
		break;
	    case 38:
		animation = Animation.create(6111);
		break;
	    case 39:
		handleSkillCape(player);
		return true;
	    case 40:
		animation = Animation.create(7531);
		break;
	    case 41:
		player.getPacketDispatch().sendGraphic(1537);
		animation = Animation.create(2414);
		break;
	    case 42:
		player.getPacketDispatch().sendGraphic(1553);
		animation = Animation.create(8770);
		break;
	    }
	    break;
	default:
	    return false;
	}
	if (animation != null) {
	    player.getAnimator().forceAnimation(animation);
	    player.getLocks().lock("emote", animation.getDuration());
	}
	return true;
    }

    /**
     * Method used to handle a skillcape.
     * @param player the player.
     */
    private void handleSkillCape(Player player) {
	Item cape = player.getEquipment().get(EquipmentContainer.SLOT_CAPE);
	if (cape == null) {
	    player.getPacketDispatch().sendMessage("You need to be wearing a skillcape in order to perform this emote.");
	    return;
	}
	int capeId = cape.getId();
	for (int i = 0; i < SKILLCAPE_INFO.length; i++) {
	    if (capeId == SKILLCAPE_INFO[i][0] || capeId == SKILLCAPE_INFO[i][1]) {
		player.getPacketDispatch().sendGraphic(SKILLCAPE_INFO[i][2]);
		player.getPacketDispatch().sendAnimation(SKILLCAPE_INFO[i][3]);
		int duration = Animation.create(SKILLCAPE_INFO[i][3]).getDuration();
		player.getLocks().lock("emote", duration);
		player.getLocks().lock(duration);
		return;
	    }
	}
	player.getPacketDispatch().sendMessage("You need to be wearing a skillcape in order to perform this emote.");
    }
}
