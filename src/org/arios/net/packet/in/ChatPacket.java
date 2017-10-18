package org.arios.net.packet.in;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.arios.game.node.entity.player.Player;
import org.arios.game.system.monitor.PlayerMonitor;
import org.arios.game.system.task.Pulse;
import org.arios.game.world.GameWorld;
import org.arios.game.world.update.flag.context.ChatMessage;
import org.arios.game.world.update.flag.player.ChatFlag;
import org.arios.net.amsc.MSPacketRepository;
import org.arios.net.amsc.WorldCommunicator;
import org.arios.net.packet.IncomingPacket;
import org.arios.net.packet.IoBuffer;
import org.arios.tools.StringUtils;

/**
 * Represents the incoming chat packet.
 * @author Emperor
 */
public class ChatPacket implements IncomingPacket {

    /**
     * Represents the chat entry logger.
     */
    private static final ChatEntryLogger ENTRY_LOGGER = new ChatEntryLogger();

    @Override
    public void decode(final Player player, int opcode, IoBuffer buffer) {
	try {
	    final int effects = buffer.getShort();
	    final int numChars = buffer.getSmart();
	    final String message = StringUtils.decryptPlayerChat(buffer, numChars);
	    if (player.getDetails().getPortal().isMuted()) {
		player.getPacketDispatch().sendMessage("You have been " + (player.getDetails().getPortal().isPermMute() ? "permanently" : "temporarily") + " muted due to breaking a rule.");
		return;
	    }
	    ENTRY_LOGGER.log(player, message);
	    if (message.startsWith("/") && player.getCommunication().getClan() != null) {
		StringBuilder sb = new StringBuilder(message);
		sb.append(" => ").append(player.getName()).append(" (owned by ").append(player.getCommunication().getClan().getOwner()).append(")");
		String m = sb.toString();
		player.getMonitor().log(m.replace(m.charAt(0), ' ').trim(), PlayerMonitor.CLAN_CHAT_LOG);
		if (WorldCommunicator.isEnabled()) {
		    MSPacketRepository.sendClanMessage(player, message.substring(1));
		} else {
		    player.getCommunication().getClan().message(player, message.substring(1));
		}
		return;
	    }
	    player.getMonitor().log(message, PlayerMonitor.PUBLIC_CHAT_LOG);
	    GameWorld.submit(new Pulse(1, player) {
		@Override
		public boolean pulse() {
		    player.getUpdateMasks().register(new ChatFlag(new ChatMessage(player, message, effects, numChars)));
		    return true;
		}
	    });
	} catch (Throwable t) {
	    t.printStackTrace();
	}
    }

    /**
     * Gets the chat entry logger.
     * @return the logger.
     */
    public static ChatEntryLogger getChatEntryLogger() {
	return ENTRY_LOGGER;
    }

    /**
     * Represents a class used to log chat entrys.
     * @author 'Vexia
     */
    public static final class ChatEntryLogger {

	/**
	 * Represents the date format to use.
	 */
	private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Represents the list of chat entrys.
	 */
	private static final List<ChatEntry> ENTRYS = new ArrayList<>();

	/**
	 * Represents the time between updates.
	 */
	private static final int UPDATE_TIME = 300000;

	/**
	 * Represents the last update occurence.
	 */
	private static long lastUpdate;

	/**
	 * Constructs a new {@code ChatEntryLogger} {@code Object}.
	 */
	public ChatEntryLogger() {
	    /**
	     * empty.
	     */
	}

	/**
	 * Method used to update the logger and clears all entrys from 60
	 * seconds ago.
	 */
	public void update() {
	    Iterator<ChatEntry> it = ENTRYS.iterator();
	    ChatEntry entry;
	    while (it.hasNext()) {
		entry = it.next();
		if (entry.getTimeStamp().getTime() < lastUpdate) {
		    it.remove();
		}
	    }
	    setUpdated();
	}

	/**
	 * Method used to log a chat message.
	 * @param player the player.
	 * @param message the message.
	 */
	public void log(final Player player, final String message) {
	    player.getSavedData().getGlobalData().setChatPing(System.currentTimeMillis() + UPDATE_TIME);
	    ENTRYS.add(ChatEntry.create(player, message));
	    if (needsUpdate()) {
		update();
	    }
	}

	/**
	 * Gets the chat entrys of a player.
	 * @param player the player.
	 * @return the chat entrys.
	 */
	public List<ChatEntry> getEntrys(final Player player) {
	    List<ChatEntry> entrys = new ArrayList<>();
	    for (ChatEntry entry : ENTRYS) {
		if (entry.getName().equals(player.getUsername())) {
		    entrys.add(entry);
		}
	    }
	    return entrys;
	}

	/**
	 * Gets the organized chat entrys.
	 * @param first the first.
	 * @param second the second.
	 * @return the list of them organized.
	 */
	public List<ChatEntry> getOrganized(List<ChatEntry> first, List<ChatEntry> second) {
	    List<ChatEntry> organized = new ArrayList<>();
	    organized.addAll(first);
	    organized.addAll(second);
	    Collections.sort(organized, new Comparator<ChatEntry>() {
		public int compare(ChatEntry m1, ChatEntry m2) {
		    return m1.getTimeStamp().compareTo(m2.getTimeStamp());
		}
	    });
	    return organized;
	}

	/**
	 * A wrapper method for getting the organized entrys.
	 * @param player the player.
	 * @param other the other.
	 * @return the entrys.
	 */
	public List<ChatEntry> getOrganized(final Player player, final Player other) {
	    return getOrganized(getEntrys(player), getEntrys(other));
	}

	/**
	 * Checks if the logger needs an update.
	 * @return {@code True} if so.
	 */
	public boolean needsUpdate() {
	    return lastUpdate < System.currentTimeMillis();
	}

	/**
	 * Sets the last update to two minutes ahead.
	 */
	public void setUpdated() {
	    ChatEntryLogger.lastUpdate = System.currentTimeMillis() + UPDATE_TIME;
	}

	/**
	 * Gets the entrys.
	 * @return The entrys.
	 */
	public static List<ChatEntry> getEntrys() {
	    return ENTRYS;
	}

	/**
	 * static block to set timezone.
	 */
	static {
	    DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("America/Toronto"));
	}

	/**
	 * Represents a chat entry.
	 * @author 'Vexia
	 * @version 1.0
	 */
	public static final class ChatEntry {

	    /**
	     * Represents the timestamp of the entry.
	     */
	    private final Date timeStamp;

	    /**
	     * Represents the name of the player.
	     */
	    private final String name;

	    /**
	     * Represents the message of the entry.
	     */
	    private final String message;

	    /**
	     * Constructs a new {@code ChatEntry} {@code Object}.
	     * @param timeStamp the timeStamp.
	     * @param name the name.
	     * @param message the message.
	     */
	    public ChatEntry(final Date timeStamp, final String name, final String message) {
		this.timeStamp = timeStamp;
		this.name = name;
		this.message = message;
	    }

	    /**
	     * Creates a chat entry.
	     * @param player the player.
	     * @param string the string.
	     * @return the entry.
	     */
	    public static ChatEntry create(final Player player, final String string) {
		return new ChatEntry(new Date(), player.getUsername(), string);
	    }

	    @Override
	    public String toString() {
		return "[" + DATE_FORMAT.format(timeStamp) + "]" + "[" + name + "] " + message;
	    }

	    /**
	     * Gets the timeStamp.
	     * @return The timeStamp.
	     */
	    public Date getTimeStamp() {
		return timeStamp;
	    }

	    /**
	     * Gets the message.
	     * @return The message.
	     */
	    public String getMessage() {
		return message;
	    }

	    /**
	     * Gets the name.
	     * @return The name.
	     */
	    public String getName() {
		return name;
	    }

	}
    }
}