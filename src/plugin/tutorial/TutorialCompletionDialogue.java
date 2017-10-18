package plugin.tutorial;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.dialogue.FacialExpression;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.content.global.tutorial.TutorialStage;
import org.arios.game.node.entity.npc.NPC;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.info.portal.Perks;
import org.arios.game.node.entity.player.link.HintIconManager;
import org.arios.game.node.entity.player.link.IronmanMode;
import org.arios.game.node.item.GroundItemManager;
import org.arios.game.node.item.Item;
import org.arios.game.world.GameWorld;
import org.arios.game.world.map.Location;
import org.arios.net.amsc.MSPacketRepository;

/**
 * Handles the tutorial completition dialogue (skippy, magic instructor)
 * @author Vexia
 * @author Splinter
 */
public class TutorialCompletionDialogue extends DialoguePlugin {

	/**
	 * The starter pack of items.
	 */
    	private static final Item[] STARTER_PACK = new Item[] { new Item(995, 5000), new Item(1351, 1), new Item(590, 1), new Item(303, 1), new Item(380, 12), new Item(1925, 1), new Item(1931, 1), new Item(1265, 1), new Item(1205, 1), new Item(1277, 1), new Item(1171, 1), new Item(841, 1), new Item(882, 50), new Item(556, 50), new Item(558, 50), new Item(555, 50), new Item(557, 50), new Item(559, 50), new Item(8007, 3), new Item(8010, 3), new Item(4447, 1), new Item(2679, 1), new Item(1856, 1) };
    
	/**
	 * Represents the rune items.
	 */
	private static final Item[] RUNES = new Item[] { new Item(556, 5), new Item(558, 5) };

	/**
	 * If ironman is enabled.
	 */
	private static final boolean IRONMAN = true;

	/**
	 * Constructs a new {@code TutorialCompletionDialogue} {@code Object}
	 */
	public TutorialCompletionDialogue() {
		/*
		 * empty.
		 */
	}

	/**
	 * Constructs a new {@code TutorialCompletionDialogue} {@code Object}
	 * @param player the player.
	 */
	public TutorialCompletionDialogue(Player player) {
		super(player);
	}

	@Override
	public DialoguePlugin newInstance(Player player) {
		return new TutorialCompletionDialogue(player);
	}

	@Override
	public boolean open(Object... args) {
		npc = (NPC) args[0];
		if (npc == null) {
			return true;
		}
		if (npc.getId() == 946) {
			switch (TutorialSession.getExtension(player).getStage()) {
			case 67:
				interpreter.sendDialogues(player, null, "Hello.");
				stage = 0;
				return true;
			case 69:
				interpreter.sendDialogues(946, null, "Good. This is a list of your spells. Currently you can", "only cast one offensive spell called Wind Strike. Let's", "try it out on one of those chickens.");
				stage = 0;
				return true;
			case 70:
				if (!player.getInventory().contains(556, 1) && !player.getInventory().contains(558, 1)) {
					if (player.getInventory().hasSpaceFor(RUNES[0]) && player.getInventory().hasSpaceFor(RUNES[1])) {
						interpreter.sendDoubleItemMessage(RUNES[0], RUNES[1], "Terrova gives you five <col=08088A>air runes</col> and five <col=08088A>mind runes</col>!");
						player.getInventory().add(RUNES);
						stage = 3;
					} else {
						GroundItemManager.create(RUNES, player.getLocation(), player);
						stage = 3;
					}
				} else {
					end();
					TutorialStage.load(player, 70, false);
				}
				return true;
			case 71:
				interpreter.sendDialogues(946, null, "Well you're all finished here now. I'll give you a", "reasonable number of runes when you leave.");
				stage = -2;
				return true;
			}
		} else {
			interpreter.sendDialogues(npc, FacialExpression.NORMAL, "Hey. Do you wanna skip the Tutorial?", "I can send you straight to the mainland, easy.");
			stage = 0;
		}
		return true;
	}

	@Override
	public boolean handle(int interfaceId, int buttonId) {
		if (npc.getId() == 2796 || TutorialSession.getExtension(player).getStage() >= 71) {
			switch (stage) {
			case -2:
				interpreter.sendOptions("Do you want to go to the mainland?", "Yes.", "No.");
				stage = -1;
				break;
			case -1:
				switch (buttonId) {
				case 1:
					interpreter.sendDialogues(946, null, "When you get to the mainland you will find yourself in", "the town of Lumbridge. If you want some ideas on", "where to go next talk to my friend the Lumbridge", "Guide. You can't miss him; he's holding a big staff with");
					stage = 13;
					break;
				case 2:
					end();
					TutorialStage.load(player, 71, false);
					break;
				}
				break;
			case 0:
				interpreter.sendDialogues(npc, FacialExpression.NORMAL, "If I do, you won't be able to come back here", "afterwards. It's a one-way trip. What do you say?");
				stage = 1;
				break;
			case 1:
				interpreter.sendOptions("What would you like to say?", "Leave Tutorial Island.", "Can I decide later?", "I'll stay here for the Tutorial.");
				stage = 2;
				break;
			case 2:
				switch (buttonId) {
				case 1:
					interpreter.sendDialogues(player, FacialExpression.NORMAL, "Send me to the mainland now.");
					stage = 15;
					break;
				case 2:
					interpreter.sendDialogues(player, FacialExpression.NORMAL, "Can I decide later?");
					stage = 30;
					break;
				case 3:
					interpreter.sendDialogues(player, FacialExpression.NORMAL, "I'll stay here for the Tutorial.");
					stage = 40;
					break;
				}
				break;
			case 30:
				interpreter.sendDialogues(npc, FacialExpression.NORMAL, "Sure. You'll find me all over this land.", "Ask me again any time you like.");
				stage = 31;
				break;
			case 31:
				interpreter.sendOptions("What would you like to say?", "Send me to Lumbridge now.", "Can I decide later?", "I'll stay here for the Tutorial.");
				stage = 2;
				break;
			case 40:
				interpreter.sendDialogues(npc, FacialExpression.NORMAL, "Good choice. Let me know if you change your mind.");
				stage = 99;
				break;
			case 22:
				interpreter.sendOptions("What would you like to say?", "Send me to the mainland now.", "Who are you?", "Can I decide later?", "I'll stay here for the Tutorial.");
				stage = 2;
				break;
			case 15:
				interpreter.sendDialogues(npc, FacialExpression.NORMAL, "I can do that for ye. But first I must ask a few", "questions.");
				stage = 500;
				if (!IRONMAN) {
					stage = 1200;
				}
				break;
			case 500:
				npc("The first thing for you to do is to decide", "if you would like an Ironman account.");
				stage++;
				break;
			case 501:
				player.removeAttribute("ironMode");
				player.removeAttribute("ironPermanent");
				options("Yes, please.", "What is an Ironman account?", "No, thanks.");
				stage++;
				break;
			case 502:
				switch (buttonId) {
				case 1:
					player("Yes, please.");
					stage = 506;
					break;
				case 2:
					player("What is an Ironman account?");
					stage = 530;
					break;
				case 3:
					player("No, thanks.");
					stage++;
					break;
				}
				break;
			case 503:
				npc("Are you sure you don't want an Ironman", "account?");
				stage++;
				break;
			case 504:
				options("Yes I am sure.", "No, let me decide.");
				stage++;
				break;
			case 505:
				player(buttonId == 1 ? "Yes I am sure." : "No, let me decide.");
				stage = buttonId == 1 ? 1200 : 500;
				break;
			case 506:
				interpreter.sendOptions("Select a Mode", "Standard", "<col=FF0000>Ultimate</col>", "Go back.");
				stage++;
				break;
			case 507:
				switch (buttonId) {
				case 1:
				case 2:
					npc("You have chosen the: " + (buttonId == 1 ? "Standard" : "<col=FF0000>Ultimate</col>") + " mode.");
					stage = 508;
					player.setAttribute("ironMode", IronmanMode.values()[buttonId]);
					break;
				case 3:
					npc("The last thing for you to do is to decide", "if you would like an Ironman account mode?");
					stage = 501;
					break;
				}
				break;
			case 508:
				interpreter.sendOptions("Are you sure?", "Yes.", "No.");
				stage++;
				break;
			case 509:
				switch (buttonId) {
				case 1:
					player("Yes, I am sure.");
					stage++;
					break;
				case 2:
					player("No, I want to change it.");
					stage = 506;
					break;
				}
				break;
			case 510:
				npc("You have the ability to remove the Ironman restrictions", "once you get to the mainland, however, you can only", "do this once. You can also make this permanent and", "never have the ability to remove the Ironman restriction.");
				stage++;
				break;
			case 511:
				npc("Would you like this mode to be permanent?");
				stage++;
				break;
			case 512:
				options("Yes, please.", "No, thanks.");
				stage++;
				break;
			case 513:
				player.setAttribute("ironPermanent", buttonId == 1);
				npc("Are you sure?");
				stage++;
				break;
			case 514:
				options("Yes.", "No.");
				stage++;
				break;
			case 515:
				player(buttonId == 1 ? "Yes." : "No.");
				stage = buttonId == 1 ? 516 : 511;
				break;
			case 516:
				player.getIronmanManager().setPermanent(player.getAttribute("ironPermanent", false));
				player.getIronmanManager().setMode(player.getAttribute("ironMode", IronmanMode.NONE));
				MSPacketRepository.sendInfoUpdate(player);
				npc("Congratulations, you have setup your Ironman account.", "You will travel into the mainland now.");
				stage = 1200;
				break;
			case 530:
				npc("An Ironman account is a style of playing where players", "are completely self-sufficient.");
				stage++;
				break;
			case 531:
				npc("A standard Ironman does not receieve items or", "assistance from other players. They cannot trade, stake,", "receieve PK loot, scavenge dropped items, nor player", "certain minigames.");
				stage++;
				break;
			case 532:
				npc("In addition to the standard Ironman rules. An", "Ultimate Ironman cannot use banks, nor retain any", "items on death in dangerous areas.");
				stage = 501;
				break;
			case 1200:
			    npc("Now you need to select your inital spawn location.", "Where would you like me to take you?");
			    stage = 1201;
			    break;
			case 1201:
			    interpreter.sendOptions("Select an Option", "Ardougne", "Camelot", "The Grand Exchange", "Falador", "Lumbridge");
			    stage = 1202;
			    break;
			case 1202:
			    npc("Alright, I'll send you there.", "Enjoy your time playing "+GameWorld.getName()+"!");
			    player.setAttribute("initialspawnlocation", buttonId);
			    stage = 520;
			    break;
			case 520:
				player.removeAttribute("tut-island");
				player.getConfigManager().set(1021, 0);
				player.getProperties().setTeleportLocation(getSpawnLocation(player.getAttribute("initialspawnlocation", 0)));
				player.removeAttribute("initialspawnlocation");
				TutorialSession.getExtension(player).setStage(72);
				player.getInterfaceManager().closeOverlay();
				player.getInventory().clear();
				player.getEquipment().clear();
				player.getBank().clear();
				player.getBank().add(new Item(995, 25));
				player.getInterfaceManager().openDefaultTabs();
				player.getInventory().add(STARTER_PACK);
				interpreter.sendDialogue("Welcome to "+GameWorld.getName()+"!", "If you have any questions or are stuck, don't hesitate to", "contact an in-game staff member.");
				player.getPacketDispatch().sendMessage("Welcome to " + GameWorld.getName() + ".");
				player.getPacketDispatch().sendMessage("You have been given a free Double XP voucher to use in the Arios in-game ::shop.");
				player.getPacketDispatch().sendMessage("Simply type ::shop and click 'Double Experience (2 hours)' to activate it.");
				player.getPacketDispatch().sendMessage("Earn more perks by ::voting for credits. Log in to our website with your");
				player.getPacketDispatch().sendMessage("in-game details and vote for credits to buy perks!");
				player.getInventory().add(new Item(player.getAppearance().isMale() ? 11818 : 11820));
				player.getDetails().getShop().addPerk(Perks.DOUBLE_XP);  
				player.unlock();
				TutorialSession.getExtension(player).setStage(TutorialSession.MAX_STAGE + 1);
				stage = 7;
				if (player.getIronmanManager().isIronman() && player.getSettings().isAcceptAid()) {
					player.getSettings().toggleAcceptAid();
				}
				MSPacketRepository.sendInfoUpdate(player);
				int slot = player.getAttribute("tut-island:hi_slot", -1);
				if (slot < 0 || slot >= HintIconManager.MAXIMUM_SIZE) {
					break;
				}
				player.removeAttribute("tut-island:hi_slot");
				HintIconManager.removeHintIcon(player, slot);
				break;
			case 7:
				end();
				break;
			case 99:
				end();
				TutorialStage.load(player, TutorialSession.getExtension(player).getStage(), false);
				break;
			}
			return true;
		}
		switch (TutorialSession.getExtension(player).getStage()) {
		case 67:
			switch (stage) {
			case 0:
				interpreter.sendDialogues(946, null, "Good day, newcomer. My name is Terrova. I'm here", "to tell you about Magic. Let's start by opening your", "spell list.");
				stage = 1;
				break;
			case 1:
				end();
				TutorialStage.load(player, 68, false);
				break;
			}
			break;
		case 69:
			switch (stage) {
			case 0:
				if (!player.getInventory().contains(556, 1) && !player.getInventory().contains(558, 1)) {
					if (player.getInventory().freeSlots() > 2) {
						interpreter.sendDoubleItemMessage(RUNES[0], RUNES[1], "Terrova gives you five <col=08088A>air runes</col> and five <col=08088A>mind runes</col>!");
						player.getInventory().add(RUNES[0], RUNES[1]);
						stage = 3;
					}
				} else {
					end();
					TutorialStage.load(player, 70, false);
				}
				break;
			case 3:
				end();
				TutorialStage.load(player, 70, false);
				break;
			}
			break;
		case 70:
			switch (stage) {
			case 0:
				break;
			case 3:
				end();
				TutorialStage.load(player, 70, false);
				break;
			}
			break;
		}
		return true;
	}

	/**
	 * Get the initial spawn location based on button clicked.
	 * @param buttonId clicked
	 * @return the location
	 */
	private Location getSpawnLocation(int buttonId) {
	    switch (buttonId){
	    case 1:
		return Location.create(2663, 3305, 0);
	    case 2:
		return Location.create(2730, 3485, 0);
	    case 3:
		return Location.create(3164, 3467, 0);
	    case 4:
		return Location.create(2965, 3380, 0);
	    case 5:
		return Location.create(3233, 3230, 0);
	    }
	    return null;
	}

	@Override
	public int[] getIds() {
		return new int[] {/* skippy */2796, /* magic instructor */946 };
	}

}
