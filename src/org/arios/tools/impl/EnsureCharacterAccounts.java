package org.arios.tools.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Map.Entry;

import org.arios.ServerConstants;
import org.arios.game.node.entity.player.Player;
import org.arios.game.node.entity.player.info.PlayerDetails;
import org.arios.game.system.communication.CommunicationInfo;
import org.arios.game.system.communication.Contact;
import org.arios.game.system.mysql.SQLManager;
import org.arios.game.system.task.TaskExecutor;
import org.arios.game.world.GameWorld;
import org.arios.parser.player.PlayerParser;

/**
 * Ensures the character accounts data.
 * @author Emperor
 */
public final class EnsureCharacterAccounts {

    static int goodCount = 0;

    /**
     * The main method.
     * @param args The arguments cast on runtime.
     * @throws Throwable When an exception occurs.
     */
    public static void main(String... args) throws Throwable {
	GameWorld.prompt(false);
	final File backupDir = new File("./invalid_chars/");
	int totalCount = new File(ServerConstants.PLAYER_SAVE_PATH).listFiles().length;

	for (final File file : new File(ServerConstants.PLAYER_SAVE_PATH).listFiles()) {
	    if (!file.getName().endsWith(".arios")) {
		totalCount--;
		continue;
	    }
	    TaskExecutor.executeSQL(new Runnable() {

		@Override
		public void run() {
		    try {
			PlayerDetails details = new PlayerDetails(file.getName().replaceAll(".arios", ""), "unknown", null);
			details.parse();
			Player player = new Player(details);
			PlayerParser.parse(player);
			Connection connection = SQLManager.getConnection();
			PreparedStatement st = connection.prepareStatement("UPDATE members SET contacts = ?, blocked = ?, clanName = ?, currentClan = ?, clanReqs = ?, chatSettings = ? WHERE username = ?");
			String contacts = "";
			String blocked = "";
			CommunicationInfo info = player.getDetails().getCommunication();
			for (int i = 0; i < info.getBlocked().size(); i++) {
			    blocked += (i == 0 ? "" : ",") + info.getBlocked().get(i);
			}
			int count = 0;
			for (Entry<String, Contact> entry : info.getContacts().entrySet()) {
			    contacts += "{" + entry.getKey() + "," + entry.getValue().getRank().ordinal() + "}" + (count == info.getContacts().size()- 1 ? "" : "~");
			    count++;
			}
			st.setString(1, contacts);
			st.setString(2, blocked);
			st.setString(3, info.getClanName());
			st.setString(4, info.getCurrentClan());
			st.setString(5, info.getJoinRequirement().ordinal() + "," + info.getMessageRequirement().ordinal() + "," + info.getKickRequirement().ordinal() + "," + info.getLootRequirement().ordinal());
			st.setString(6, player.getSettings().getPublicChatSetting() + "," + player.getSettings().getPrivateChatSetting() + "," + player.getSettings().getTradeSetting());
			st.setString(7, player.getName());
			st.executeUpdate();
			SQLManager.close(connection);
			PlayerParser.dump(player, "./530/");
			goodCount++;
			System.out.println("Ensured " + file.getName().replaceAll(".arios", "") + "'s account data!");
		    } catch (Throwable t) {
			t.printStackTrace();
			System.out.println("Error ensuring " + file.getName().replaceAll(".arios", "") + "'s account data!");
			try {
			    if (!backupDir.isDirectory()) {
				backupDir.mkdir();
			    }
			    copyFile(file, new File("./invalid_chars/" + file.getName()));
			    file.delete();
			} catch (Throwable e) {
			    e.printStackTrace();
			}
		    }
		}
	    }

		    );
	}
	System.out.println("Conversion of 498 -> 530 accounts completed, " + goodCount + "/" + totalCount + " accounts converted!");
    }

    /**
     * Copies a file.
     * @param in The file to be copied.
     * @param out The file to copy to.
     */
    private static void copyFile(File in, File out) {
	try (FileChannel channel = new FileInputStream(in).getChannel()) {
	    try (FileChannel output = new FileOutputStream(out).getChannel()) {
		channel.transferTo(0, channel.size(), output);
		channel.close();
		output.close();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}