package plugin.quest.dtreasure;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.global.quest.Quest;
import org.arios.game.content.global.quest.impl.member.DesertTreasure;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.item.Item;

/**
 * Handles the Archaeologist dialogue.
 * @author Aero
 * @version 1.0
 */
public final class ArchaeologistDialogue extends DialoguePlugin {

    /**
     * The desert treasure quest object.
     */
    private Quest quest;

    /**
     * The etchings.
     */
    private static final Item ETCHINGS = new Item(4654, 1);

    /**
     * The translation.
     */
    private static final Item TRANSLATION = new Item(4655, 1);

    /**
     * Constructs a new {@code ArchaeologistDialogue} {@code Object}.
     */
    public ArchaeologistDialogue() {
	/**
	 * Empty to stop instantiation.
	 */
    }

    /**
     * Constructs a new {@code ArchaeologistDialogue} {@code Object}.
     * @param player The player to construct the class for.
     */
    public ArchaeologistDialogue(final Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new ArchaeologistDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
	quest = player.getQuestRepository().getQuest(DesertTreasure.NAME);
	player("Hello there.");
	return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
	if (!player.getQuestRepository().isStarted(DesertTreasure.NAME) || player.getQuestRepository().isComplete(DesertTreasure.NAME)) {
	    switch (stage) {
	    case 0:
		npc("Howdy stranger. What brings you out to these parts?");
		stage = 1;
		break;
	    case 1:
		options("What are you doing here?", "Do you have any quests?", "Who are you?", "Nothing really.");
		stage = 2;
		break;
	    case 2:
		switch (buttonId) {
		case 1:// TODO: Need to go on RS.
		    end();
		    break;
		case 2:
		    player("Do you have any quests?", "Call it a hunch but you look like the type of man who", "might...");
		    stage = 3;
		    break;
		case 3:// TOOD: Need to go on RS.
		    end();
		    break;
		case 4:// TODO: Need to go on RS.
		    end();
		    break;
		}
		break;
	    case 3:
		boolean canStart = true;// (quest.hasRequirements() &&
					// !player.getQuestRepository().isComplete(DesertTreasure.NAME));
					// TODO: Simply enable.
		if (canStart) {
		    npc("Well, it's funny you should say that.", "I'm not sure if I would really call it a quest as such,", "but I found this ancient stone table in one of my", "excavations, and it would realy help me out if you");
		    stage = 4;
		} else {
		    npc("Nothing that you could handle at the moment adventurer.");
		    stage = 1;
		}
		break;
	    }
	}
	switch (quest.getStage()) {
	case 0:
	    switch (stage) {
	    case 4:
		npc("could go and take it back to the digsite for me", "and get it examined.");
		stage = 5;
		break;
	    case 5:
		npc("It's very old, and I don't recognise any of the", "inscriptions on it.");
		stage = 6;
		break;
	    case 6:
		options("Yes, I'll help you.", "No thanks, I don't want to help.");
		stage = 7;
		break;
	    case 7:
		if (buttonId == 1) {
		    player("Sure, I was heading that way anyway.", "Any particular person at the digsite", "you want me to talk to?");
		    stage = 8;
		} else {
		    player("No thanks, I don't want to help.");
		    end();
		}
		break;
	    case 8:
		npc("His name's Terry Balando. Give it to nobody but him.", "I'm sorry I can't entrust you with the actual tablet I", "found it is far too valuable to give away, but I took", "these etchings they should be enough for him to make");
		stage = 9;
		break;
	    case 9:
		npc("a preliminary translation on.", "Come back and let me know what he says, I would hate", "to waste my time excavating anything that isn't worth", "my time as a world famous archaeologist.");
		stage = 10;
		break;
	    case 10:
		player.getInventory().add(ETCHINGS);
		quest.start();
		end();
		break;
	    }
	    break;
	case 20:
	    switch (stage) {
	    case -1:
		end();
		break;
	    case 0:
		npc("So what did Terry Balando say about those etchings?", "Did he give you a translation for me?");
		stage = 1;
		break;
	    case 1:
		if (player.getInventory().containsItem(TRANSLATION)) {
		    player("Yeah he did.", "I have it here in this book.");
		    stage = 2;
		} else {
		    player("Yeah he did.", "I will return with it once I have it with me.");
		    stage = -1;
		}
		break;
	    case 2:
		npc("Did you take a read of this?", "It will do you good to understand how this profression", "works. here, have a read.");
		stage = 3;
		break;
	    case 3:
		options("Read book", "Don't read book");
		stage = 4;
		break;
	    case 4:
		switch (buttonId) {
		case 1:
		    // TODO: Open up shit book interface
		    // (Translation).
		    break;
		case 2:
		    player("Yeah, I did.", "Kind of boring really.");
		    stage = 5;
		    break;
		}
		break;
	    case 5:
		npc("Excellent.", "Just give me a moment to read this, and talk to me", "again in a second.");
		stage = 6;
		break;
	    case 6:
		quest.setStage(21);
		end();
		break;
	    }
	    break;
	case 21:
	    switch (stage) {
	    case 0:
		npc("Hmmm. Interesting.", "It seems to me like there's some kind of treasure", "hidden out in the desert.");
		stage = 1;
		break;
	    case 1:
		npc("So what do you say? Fancy being a treasure hunter", "like me?");
		stage = 2;
		break;
	    case 2:
		player("Uh... don't you mean archaeologist?");
		stage = 3;
		break;
	    case 3:
		npc("Yeees... An archaeologist...");
		stage = 4;
		break;
	    case 4:
		npc("It's all this hot sun getting to me I think.");
		stage = 5;
		break;
	    case 5:
		npc("Well anyway, let's just assume there is a treasure", "hidden in the dessert somewhere, and let's just say for", "the sake of argument that maybe if I found a big stash", "of gold and treasure I wouldn't necessarily just hand it");
		stage = 6;
		break;
	    case 6:
		npc("hand it all over to the Museum of Varrock.");
		stage = 7;
		break;
	    case 7:
		npc("Let's also say, purely hypothetically, that if there were", "such a big stash of treasure and someone were to help", "me find it, then that hypothetical person might be", "entitled to, oh, let's say and purely for the sake of");
		stage = 8;
		break;
	    case 8:
		npc("argument a thirty percent split...");
		stage = 9;
		break;
	    case 9:
		player("Fifty percent.");
		stage = 10;
		break;
	    case 10:
		npc("That's right!");
		stage = 11;
		break;
	    case 11:
		npc("A purely for the sake of argument fifty-fifty split on", "this hypothetical treasure, should it exist....");
		stage = 12;
		break;
	    case 12:
		npc("Do you see where I'm going going with this?");
		stage = 13;
		break;
	    case 13:
		player("You want me to help you find some treasure for a fifty", "percent share, as long as I don't tell anybody, and", "your reputation as an esteemed archaeologist with the", "Museum of Varrock remains intact, and nobody");
		stage = 14;
		break;
	    case 14:
		player("discovers you're just a treasure hunter?");
		stage = 15;
		break;
	    case 15:
		npc("Uh... yes, but the way you said it makes it sound like", "I'm doing something wrong...");
		stage = 16;
		break;
	    case 16:
		npc("So what do you say?", "Partners?");
		stage = 17;
		break;
	    case 17:
		options("Help him", "Don't help him");
		stage = 18;
		break;
	    case 18:
		switch (buttonId) {
		case 1:
		    player("Well...", "I guess nobody is really going to lose out on anything,", "and we don't even know if there is any treasure...");
		    stage = 19;
		    break;
		case 2:
		    end();
		    break;
		}
		break;
	    case 19:
		player("Aw, go on then. Count me in.");
		stage = 20;
		break;
	    case 20:
		npc("Good " + (player.getAppearance().isMale() ? "boy!" : "girl!") + " Well if we split up we'll be able to find", "treasure quicker.");
		stage = 21;
		break;
	    case 21:
		npc("I'll continue searching around ehre in this Bedabin", "Camp, you head due South to the Bandit Village. If", "either of us find anything, we'll come find each other", "and say what, okay?");
		stage = 22;
		break;
	    case 22:
		npc("You head due South, see what you can find out about", "this tablet.");
		stage = 23;
		break;
	    case 23:
		quest.setStage(22);
		end();
		break;
	    }
	    break;
	case 22:
	    switch (stage) {

	    }
	    break;
	}
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 1918 };
    }

}