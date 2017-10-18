package plugin.tutorial;

import org.arios.game.content.dialogue.DialoguePlugin;
import org.arios.game.content.dialogue.book.Book;
import org.arios.game.content.dialogue.book.BookLine;
import org.arios.game.content.dialogue.book.Page;
import org.arios.game.content.dialogue.book.PageSet;
import org.arios.game.node.entity.player.Player;

/**
 * Handles the introductory after-tutorial book.
 * @author Splinter
 */
public final class TutorialBook extends Book {

    /**
     * Represents the book id
     */
    public static int ID = 387454;

    /**
     * Represents the array of pages for this book.
     */
    private static final PageSet[] PAGES = new PageSet[] { new PageSet(new Page(new BookLine("Starting off", 87), new BookLine("", 88), new BookLine("    It is a good idea", 89), new BookLine("to raid the two strongholds", 90), new BookLine("in Barbarian Village in order", 91), new BookLine("to gain a ludicrous amount of", 92), new BookLine("coins to start with. Also", 93), new BookLine("Consider collecting skilling", 94), new BookLine("materials to sell in bulk", 95), new BookLine("on the Grand Exchange.", 96), new BookLine("Raw meat, dropped from", 97)), new Page(new BookLine("cows, is useful in the", 98), new BookLine("Summoning skill and can", 99), new BookLine("be easily sold for coins.", 100), new BookLine(" ", 101), new BookLine("    Credits/Double XP", 102), new BookLine("Credits are our way", 103), new BookLine("of thanking you for voting.", 104), new BookLine("You may spend credits", 105), new BookLine("in the online store at:", 106), new BookLine("www.ariosrsps.com/shop or", 107), new BookLine("by using our in-game ::shop", 108))), new PageSet(new Page(new BookLine("    Suggestions", 87), new BookLine("Please suggest new features", 88), new BookLine("you'd like to see via", 89), new BookLine("the forums located at", 90), new BookLine("www.ariosrsps.com.", 91), new BookLine("We wholeheartedly look over", 92), new BookLine("and consider all suggestions.", 93))),

    };

    /**
     * Constructs a new {@code ShieldofArravBook} {@code Object}.
     */
    public TutorialBook(final Player player) {
	super(player, "Tutorial Book", 1856, PAGES);
    }

    /**
     * Constructs a new {@code ShieldofArravBook} {@code Object}.
     */
    public TutorialBook() {
	/**
	 * empty.
	 */
    }

    @Override
    public void finish() {
    }

    @Override
    public void display(Page[] set) {
	player.lock();
	player.getInterfaceManager().open(getInterface());
	for (int i = 87; i < 112; i++) {
	    player.getPacketDispatch().sendString("", getInterface().getId(), i);
	}
	player.getPacketDispatch().sendString(getName(), getInterface().getId(), 38);
	player.getPacketDispatch().sendString("", getInterface().getId(), 109);
	player.getPacketDispatch().sendString("", getInterface().getId(), 110);
	for (Page page : set) {
	    for (BookLine line : page.getLines()) {
		player.getPacketDispatch().sendString(line.getMessage(), getInterface().getId(), line.getChild());
	    }
	}
	player.getPacketDispatch().sendInterfaceConfig(getInterface().getId(), 84, index < 1 ? true : false);
	boolean lastPage = index == sets.length - 1;
	player.getPacketDispatch().sendInterfaceConfig(getInterface().getId(), 86, lastPage ? true : false);
	if (lastPage) {
	    finish();
	}
	player.unlock();
    }

    @Override
    public DialoguePlugin newInstance(Player player) {
	return new TutorialBook(player);
    }

    @Override
    public int[] getIds() {
	return new int[] { ID };
    }
}
