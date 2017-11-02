package plugin.interaction.inter;

import org.arios.game.component.Component;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.component.ComponentPlugin;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.content.global.tutorial.TutorialStage;
import org.arios.game.node.entity.player.Player;
import org.arios.plugin.Plugin;

/**
 * @author 'Vexia
 */
public class SettingTabInterface extends ComponentPlugin {

	@Override
	public Plugin<Object> newInstance(Object arg) throws Throwable {
		ComponentDefinition.put(261, this);
		return this;
	}

	@Override
	public boolean handle(Player p, Component component, int opcode, int button, int slot, int itemId) {
		//p.getAudioManager().send("option_tab_button_select");
		switch (button) {
			case 15:
			case 16:
			case 17:
			case 18:// brightness
				int brightness = button - 15;
				p.getSettings().setBrightness(brightness);
				break;
			case 24:
			case 25:
			case 26:
			case 27:
			case 28:// music
				int volume = 28 - button;
				p.getSettings().setMusicVolume(volume);
				break;
			case 30:
			case 31:
			case 32:
			case 33:
			case 34:// sonund
				int volume1 = 34 - button;
				p.getSettings().setSoundEffectVolume(volume1);
				break;
			case 36:
			case 37:
			case 38:
			case 39:
			case 40://all sound
				int volume11 = 40 - button;
				p.getSettings().setAreaSoundVolume(volume11);
				break;
			case 57:// mouse
				p.getSettings().toggleMouseButton();
				break;
			case 42:// effects
				p.getSettings().toggleChatEffects();
				break;
			case 44:// private chat
				p.getSettings().toggleSplitPrivateChat();
				break;
			case 70:// aid
				p.getSettings().toggleAcceptAid();
				break;
			case 72:// run
				p.getSettings().toggleRun();
				break;
			case 75:// house
				p.getInterfaceManager().openSingleTab(new Component(370));
				break;
			case 21:
				p.getPacketDispatch().sendCS2Script(917, new Object[] {-1, -1});
				p.getInterfaceManager().open(new Component(60));
				break;
			case 63:
				p.getPacketDispatch().sendCS2Script(917, new Object[] { -1, -1 });
				p.getInterfaceManager().open(new Component(121));
				p.getPacketDispatch().sendAccessMask(2, 112, 121, 0, 13);
				break;
		}
		return true;
	}
}
