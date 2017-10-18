package plugin.dialogue;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.global.quest.Quest;
import org.arios.game.content.global.quest.impl.member.DesertTreasure;
import org.arios.game.node.entity.player.Player;

/**
 * Handles Eblis's dialogue.
 * @author Aero
 * @version 1.0
 */
public class EblisDialogue extends DialoguePlugin {

    /**
     * The desert treasure object.
     */
    private Quest quest;

    /**
     * The item amounts that eblis requires.
     */
    @SuppressWarnings("unused")
    private static final int[] ITEM_AMOUNTS = new int[] { 12, 10, 6, 6, 2, 2, 1 };

    /**
     * Constructs a new {@code EblisDialogue} {@code Object}.
     */
    public EblisDialogue() {
	/**
	 * Empty to stop instantiation.
	 */
    }

    /**
     * Constructs a new {@code EblisDialogue} {@code Object}.
     * @param player The player to construct the class for.
     */
    public EblisDialogue(final Player player) {
	super(player);
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new EblisDialogue(player);
    }

    @Override
    public boolean open(Object... args) {
	quest = player.getQuestRepository().getQuest(DesertTreasure.NAME);
	switch (quest.getStage()) {
	case 22:
	    player("Hello, I represent the Museum of Varrock, and I have", "reason to believe there may be some kinds of artefacts", "of historical significance in the nearby area...");
	    break;
	case 23:
	    npc("Before I can complete the spell I will still need", "the following items:");
	    break;
	}
	return true;
    }

    @Override
    public boolean handle(int interfaceId, int buttonId) {
	switch (quest.getStage()) {
	case 22:
	    switch (stage) {
	    case -1:
		end();
		break;
	    case 0:
		npc("Ah yes. The only time people care about our existence", "is when they think they have something to gain from us.");
		stage = 1;
		break;
	    case 1:
		npc("I have nothing to say to you.", "You and your kind are not welcome here.");
		stage = 2;
		break;
	    case 2:
		player("Please, if I can just have a few minutes of your time", "to ask some questions...?");
		stage = 3;
		break;
	    case 3:
		npc("(sigh)", "I suppose I can spare you that.", "What do you wish to know about?");
		stage = 4;
		break;
	    case 4:
		if (player.getAttribute("dt-diamonds_unlock", Boolean.FALSE)) {
		    options("Why is this village so hostile?", "Do you know anything about treasure near here?", "Do you know anything about a fortress near here?", "Tell me of the four diamonds of Azzandra", "Nothing thanks.");
		} else {
		    options("Why is this village so hostile?", "Do you know anything about treasure near here?", "Do you know anything about a fortress near here?", "Nothing thanks.");
		}
		stage = 5;
		break;
	    case 5:
		switch (buttonId) {
		case 1:
		    player("Why are all of the people here so hostile?");
		    stage = 6;
		    break;
		case 2:
		    player("I was wondering if you knew anything about some", "treasure somewhere around here? I have some evidence", "that there might be some kind of treasure", "hidden very close to this village...");
		    stage = 26;
		    break;
		case 3:
		    player("Do you know anything about some kind of fortress", "nearby? I have reason to believe there is, or at least used", "to be, some kind of fortress very close to here...");
		    stage = 27;
		    break;
		case 4:
		    if (player.getAttribute("dt-diamonds_unlock", Boolean.FALSE)) {
			player("So tell me... Did you ever hear of something called", "the Diamonds of Azzanadra?");
			stage = 28;
		    } else {
			player("Nothing thanks.");
			stage = -1;
		    }
		    break;
		case 5:
		    player("Nothing thanks.");
		    stage = -1;
		    break;
		}
		break;
	    case 6:
		player("You would think I was asking you for money instead", "of just for answers to a few questions...");
		stage = 8;
		break;
	    case 8:
		npc("It is a long story,", "and I doubt you have much interest in hearing it.", "Your sort never are, you just take what you can", "of ours, and then abandon us once more to the desert.");
		stage = 9;
		break;
	    case 9:
		player("Actually, I'd be quite interested to hear what it is you have", "to say to excuse the attitude everybody", "in this village seems to have.");
		stage = 10;
		break;
	    case 10:
		npc("Ah, it all begun many generations ago,", "when our ancestors were the proud rulers of these lands...", "My ancestors lived far to the North of here,");
		stage = 11;
		break;
	    case 11:
		npc("and our lands stretched from the sea in the East to the,", "river Lum, and the mountain of ice.");
		stage = 12;
		break;
	    case 12:
		npc("From coast to coast, North to South, our domain", "was absolute. Our god was kind to us, and blessed us", "with prosperity and happiness, and in return we were");
		stage = 13;
		break;
	    case 13:
		npc("merciless to his enemies wherever we found them.");
		stage = 14;
		break;
	    case 14:
		npc("Then came the betrayal. Our god was banished,", "leaving us helpless to our fates.");
		stage = 15;
		break;
	    case 15:
		npc("Without his protection, we were forced to fend for", "ourselves once more, against the enemies that sought to", "destroy us through their petty jealousies.");
		stage = 16;
		break;
	    case 16:
		npc("But we did not succumb without fighting!", "The spiteful Saradomin and pathetic Zamorak", "warred with each other, but the hatred they had for");
		stage = 17;
		break;
	    case 17:
		npc("each other was as nothing to the hatred they held", "towards us! With each battle they waged, we lost", "more and more land, unable to fight on all fronts,");
		stage = 18;
		break;
	    case 18:
		npc("and were pushed further and further South into", "this gods-forsaken desert. Our greatest hero,", "Azzanadra, was finally trapped in a strange");
		stage = 19;
		break;
	    case 19:
		npc("stone structure to the South of here, and bound within", "by terrible powers...");
		stage = 20;
		break;
	    case 20:
		npc("And with that our lands, our homes,", "our very lives were stolen from us!", "Too weak to reclaim what was rightfully ours,");
		stage = 21;
		break;
	    case 21:
		npc("we made our homes here,", "knowing that someday Azzanadra will return with his", "magnificent power, and bring us back to our", "former glory....");
		stage = 22;
		break;
	    case 22:
		player("So you're upset because of something that", "happened hundreds of years ago?");
		stage = 23;
		break;
	    case 23:
		player("Seems to me like maybe you should find some closure,", "and let the past go...");
		stage = 24;
		break;
	    case 24:
		npc("The insults heaped upon my race will never be forgotten,", "will never be forgiven and will never again be overlooked.", "Someday, a harsh wind will blow upon this land,", "uncovering the wrongs of the past, and we will");
		stage = 25;
		break;
	    case 25:
		npc("get back what is rightfully ours. Until such a day we will", "bide our time here, and will always be ready with our", "blades for our righteous vengeance.");
		stage = -1;
		break;
	    case 26:
		npc("If I knew of any treasure I would not choose to spend", "my life in this gods-forsaken desert.");
		stage = -1;
		break;
	    case 27:
		npc("Nobody would build anything in this wasteland", "unless they were forced to, to survive. I know of no", "fortress, I know of no reason why anyone would ever bother", "doing anything out here in the desert.");
		stage = -1;
		break;
	    case 28:
		npc("This is the treasure which you seek???", "Please accept my apologies noble " + (player.getAppearance().isMale() ? "sir" : "madam"));
		stage = 29;
		break;
	    case 29:
		npc(" I thought you were but some opportunistic thief,", "looking to steal what heritage we have left!");
		stage = 30;
		break;
	    case 30:
		npc("Now I see that you are in fact a brave adventurer,", "looking to restore our glories back upon us!");
		stage = 31;
		break;
	    case 31:
		player("Uh...yeah...", "So anyway, you have heard of them?");
		stage = 32;
		break;
	    case 32:
		npc("Heard of them? Of course I have heard of them!", "They are the legacy of our greatest ever hero!");
		stage = 33;
		break;
	    case 33:
		player("So... do you have any idea where they might be?", "I have a feeling they will be very valuable.", "Uh, valuable as historical artefacts I mean, obviously.");
		stage = 34;
		break;
	    case 34:
		npc("They were stolen by warriors of the false god", "Zamorak generations ago. When you find the warriors,", "you will find the diamonds. I suspect they will not", "willingly part with such objects of power however.");
		stage = 35;
		break;
	    case 35:
		npc("Beware too, for these warriors are very powerful;", "they have taken the powers of the diamonds", "into themselves!");
		stage = 36;
		break;
	    case 36:
		player("How do you mean?");
		stage = 37;
		break;
	    case 37:
		npc("Each diamond has an elemental quality...", "There is the Diamond of Blood, the Diamond of Ice,", "the Diamond of Smoke and the Diamond of Shadow.");
		stage = 38;
		break;
	    case 38:
		npc("You should expect the warriors to have taken some aspect", "of these diamonds as their own...");
		stage = 39;
		break;
	    case 39:
		player("Do you have any idea how I could track down", "these warriors somehow, then?");
		stage = 40;
		break;
	    case 40:
		npc("There is an ancient spell I know of that may spy", "upon such power... But it will require a few ingredients");
		stage++;
		break;
	    case 41:
		npc("for it to work. Should you be willing to get these", "ingredients for me, I will be able to locate the rough area", "where each of these warriors has taken refuge.");
		stage++;
		break;
	    case 42:
		npc("The spell is imprecise, but it should help you get on the", "right track in your search. Is your desire for", "our freedom strong enough?");
		stage++;
		break;
	    case 43:
		npc("Will you gather the ingredients for this spell for me?");
		stage++;
		break;
	    case 44:
		options("Yes.", "No.");
		stage++;
		break;
	    case 45:
		switch (buttonId) {
		case 1:
		    player("Sure, what do you need?");
		    stage++;
		    break;
		case 2:
		    end();
		    break;
		}
		break;
	    case 46:
		npc("For this spell, I will need to make some scrying glasses.", "I will need enough so that we can view the realm in its", "entirety. When enchanted, the scrying glass will be able", "to let us view any area that has been influenced");
		stage++;
		break;
	    case 47:
		npc("by the presence of the Diamonds of Azzanadra.");
		stage++;
		break;
	    case 48:
		player("Okay, but what exactly do you need for this spell?");
		stage++;
		break;
	    case 49:
		npc("Well, six scrying glasses should be sufficient.", "For each scrying glass, I will need two magic logs,", "a steel bar and some molten glass.");
		stage++;
		break;
	    case 50:
		npc("This makes a total of 12 magic logs, 6 pieces of molten glass,", "and 6 steel bars. In addition, for the actual spell", "to enchant the glasses, I will require one set");
		stage++;
		break;
	    case 51:
		npc("of normal bones, some ash, some charcoal and", "a single blood rune.");
		stage++;
		break;
	    case 52:
		npc("Do you understand me, adventurer?");
		stage++;
		break;
	    case 53:
		player("Quick question: what kind of bones do you need?");
		stage++;
		break;
	    case 54:
		npc("Standard bones.", "Other types of bones are of no use to me in this spell.");
		stage++;
		break;
	    case 55:
		options("Yes, I will go get those for you.", "No, please repeat those ingredients.");
		stage++;
		break;
	    case 56:
		switch (buttonId) {
		case 1:
		    player("It's a slightly odd collection of ingredients, but I", "shouldn't have too much trouble getting those for you.");
		    quest.setStage(23);
		    stage++;
		    break;
		case 2:
		    player("No, please repeat those ingredients.");
		    stage = 49;
		    break;
		}
		break;
	    case 57:
		end();
		break;
	    }
	    break;
	case 23:
	    // Calculate amount of stuff left needed, questdata shit.
	    // http://www.youtube.com/watch?v=Ik8SlQjzStM 11:06
	    // http://runescape.wikia.com/wiki/Desert_Treasure/Transcript
	    break;
	}
	return true;
    }

    @Override
    public int[] getIds() {
	return new int[] { 1923 };
    }

}