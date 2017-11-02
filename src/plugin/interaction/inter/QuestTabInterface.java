package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.content.global.quest.Quest;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.link.diary.AchievementDiary;
import org.arios.game.node.entity.player.link.diary.DiaryType;
import org.arios.game.world.GameWorld;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.AccessMaskContext;
import org.arios.net.packet.out.AccessMask;
import org.arios.plugin.Plugin;

/**
 * Handles the quest tab action buttons.
 *
 * @author Emperor
 * @author Vexia
 */
public class QuestTabInterface extends ComponentPlugin {

    @Override
    public Plugin<Object> newInstance(Object arg) throws Throwable {
        ComponentDefinition.put(399, this); //Quests
        ComponentDefinition.put(259, this); //Achievement diary
        ComponentDefinition.put(76, this); //Minigames
        ComponentDefinition.put(245, this); //Favour
        return this;
    }

    @Override
    public boolean handle(Player p, Component component, int opcode, int button, int slot, int itemId) {
        p.getPulseManager().clear();
        switch (component.getId()) {
            case 399://quest
                switch (button) {
                    case 1:
                        p.getAchievementDiaryManager().openTab();
                        return true;
                    case 2:
                        p.getInterfaceManager().openTab(new Component(76));
                        return true;
                    case 3:
                        p.getInterfaceManager().openTab(new Component(245));
                        return true;
                    case 7:
                    case 8:
                        Quest quest = p.getQuestRepository().getQuestIndex(button == 8 ? slot + 19 : slot);
                        if (quest != null) {
                            p.getInterfaceManager().open(new Component(275));
                            quest.update();
                            p.getDialogueInterpreter().close();
                            return true;
                        }
                        return true;
                }
                return false;
            case 259://diary
                switch (button) {
                    case 1:
                        p.getInterfaceManager().openTab(new Component(399));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 7, 399, 0, 18));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 8, 399, 0, 110));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 9, 399, 0, 12));
                        return true;
                    case 2:
                        p.getInterfaceManager().openTab(new Component(76));
                        return true;
                    case 3:
                        p.getInterfaceManager().openTab(new Component(245));
                        return true;
                    default:
                        AchievementDiary diary = p.getAchievementDiaryManager().getDiary(DiaryType.forChild(button));
                        if (diary != null) {
                            diary.open(p);
                        }
                        return true;
                }
            case 76:
                switch (button) {
                    case 3:
                        p.getInterfaceManager().openTab(new Component(399));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 7, 399, 0, 18));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 8, 399, 0, 110));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 9, 399, 0, 12));
                        return true;
                    case 4:
                        p.getAchievementDiaryManager().openTab();
                        return true;
                    case 5:
                        p.getInterfaceManager().openTab(new Component(245));
                        return true;
                }
                break;
            case 245:
                switch (button) {
                    case 2:
                        p.getInterfaceManager().openTab(new Component(399));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 7, 399, 0, 18));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 8, 399, 0, 110));
                        PacketRepository.send(AccessMask.class, new AccessMaskContext(p, 2, 9, 399, 0, 12));
                        return true;
                    case 3:
                        p.getAchievementDiaryManager().openTab();
                        return true;
                    case 4:
                        p.getInterfaceManager().openTab(new Component(76));
                        return true;
                    case 23:
                        p.getInterfaceManager().openComponent(243);
                        return true;
                }
                break;
        }
        return true;
    }

}
