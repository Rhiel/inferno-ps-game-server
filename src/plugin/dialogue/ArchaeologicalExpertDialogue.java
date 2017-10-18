package plugin.dialogue;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.global.quest.Quest;
import org.arios.game.content.global.quest.impl.member.DesertTreasure;
import org.arios.game.content.global.quest.impl.member.Digsite;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;

/**
 * Handles the archaeologicals expert dialogue.
 * @author Aero
 * @version 1.0
 */
public class ArchaeologicalExpertDialogue extends DialoguePlugin {

    /**
     * The desert treasure quest object.
     */
    private Quest quest;

    /**
     * The etchings.
     */
    private static final Item etchings = new Item(4654, 1);

    /**
     * The translation.
     */
    private static final Item translation = new Item(4655, 1);

    /**
     * Constructs a new {@code ArchaeologicalExpertDialogue} {@code Object}.
     */
    public ArchaeologicalExpertDialogue() {
	/**
	 * Empty to stop instantiation.
	 */
    }

    /**
     * Constructs a new {@code ArchaeologicalExpertDialogue} {@code Object}.
     * @param player The player to construct the class for.
     */
    public ArchaeologicalExpertDialogue(final Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new ArchaeologicalExpertDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
	quest = player.getQuestRepository().getQuest(DesertTreasure.NAME);
	String[] options = new String[] { "Ask about the Desert Treasure Quest.", "Ask about the Digsite Quest." };
	boolean desertTreasure = player.getQuestRepository().isStarted(DesertTreasure.NAME), digsite = player.getQuestRepository().isStarted(Digsite.NAME);
	if (desertTreasure || digsite) {
	    options(options);
	    return true;
	}
	return false;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
	switch (quest.getStage()) {
	case 10:
	    switch (stage) {
	    case -1:
		end();
		break;
	    case 0:
		player("Hello, are you Terry Balando?");
		stage = 1;
		break;
	    case 1:
		npc("That's right, who wants to know...?");
		stage = 2;
		break;
	    case 2:
		npc("Ah yes, I recognise you! You're the " + (player.getAppearance().isMale() ? "lad" : "lass") + " who found that", "strange artefact about Zaros for the museum, aren't", "you? What can I do for you now?");
		stage = 3;
		break;
	    case 3:
		player("That's right.", "I was in the desert down by the Bedabin Camp, and I", "found an archaeologist who asked me to deliver this to", "you.");
		stage = 4;
		break;
	    case 4:
		npc("You spoke to the legendary Asgarnia Smith???", "Quickly, let me see what he had to give you! He is", "always at the forefront of archaeological breakthroughs!");
		stage = 5;
		break;
	    case 5:
		if (player.getInventory().containsItem(etchings)) {
		    player.getInventory().remove(etchings);
		    player("So what does the inscription say? Anything interesting?");
		    stage = 6;
		} else {
		    player("I don't seem to have it on me, I will return", "once I have it.");
		    stage = -1;
		}
		break;
	    case 6:
		npc("This... this is fascinating!", "these cunieforms seem to predate even the settlement", "we are excavating here...", "Yes, yes, this is more interesting indeed!");
		stage = 7;
		break;
	    case 7:
		player("Can you translate for me niggawh?");
		stage = 8;
		break;
	    case 8:
		npc("Well, I am not familliar with this particular language, but", "the similarities inherent in the pictographs seem to show", "a prevalent trend towards a syllabary consistent with", "the phonemes we have discovered in this excavation!");
		stage = 9;
		break;
	    case 9:
		player("Um... So, can you translate it for me or not?");
		stage = 10;
		break;
	    case 10:
		npc("Well, unfortunately this is the only example of this", "particular language I have ever seen, but I might be", "able to make a rough translation, of sorts...");
		stage = 11;
		break;
	    case 11:
		npc("It might be slightly obscure on the finest details, but it", "should be good enough to understand the rough", "meaning of what was originally written.", "Please, just wait a moment, I will write up what I can");
		stage = 12;
		break;
	    case 12:
		npc("translate into a journal for you.", "Then you can take it back to Asgarnia, I think he will", "be extremely interested in the translation!");
		stage = 13;
		quest.setStage(20);
		break;
	    case 13:
		end();
		break;
	    }
	    break;
	case 20:
	    switch (stage) {
	    case 0:
		npc("There you go, that book contains the sum of my", "translating ability.", "If you would be so kind as to take that back to", "Asgarnia, I think it will reassure him that he is on the");
		stage = 1;
		player.getInventory().add(translation);
		break;
	    case 1:
		npc("right track for a find of great archaeological importance!");
		stage = 2;
		break;
	    case 2:
		player("Wow! you write really quickly don't you?");
		stage = 3;
		break;
	    case 3:
		npc("What can I say? It's a skill I picked up through my", "many years of taking field notes!");
		stage = 4;
		break;
	    case 4:
		end();
		break;
	    }
	    break;
	}
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 619 };
    }

}