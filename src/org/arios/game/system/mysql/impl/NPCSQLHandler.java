package org.arios.game.system.mysql.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.arios.cache.def.impl.NPCDefinition;
import org.arios.game.system.SystemLogger;
import org.arios.game.system.mysql.SQLEntryHandler;
import org.arios.game.system.mysql.SQLManager;

/**
 * Handles the parsing of the NPC's configurations.
 * @author Vexia
 *
 */
public class NPCSQLHandler extends SQLEntryHandler<Object> {

    /**
     * Constructs a new {@Code NPCSQLHandler} {@Code Object}
     */
    public NPCSQLHandler() {
	super(null, "npcs", "", "");
    }

    @Override
    public void parse() throws SQLException {
	connection = getConnection();
	if (connection == null) {
	    SQLManager.close(connection);
	    return;
	}
	PreparedStatement statement = connection.prepareStatement("SELECT * FROM kratos_server.npcs");
	ResultSet set = statement.executeQuery();
	NPCDefinition definition = null;
	while (set.next()) {
	    definition = NPCDefinition.forId(set.getInt(0));
	    if (definition == null) {
		SystemLogger.error("Error in NPC SQL handler! NPC id " + set.getInt(0) + " has no definition.");
		continue;
	    }
	    for (int i = 0; i < set.getFetchSize(); i++) {
		set.getObject(i);
	    }
	}
    }

    @Override
    public void create() throws SQLException {
	
    }

    @Override
    public void save() throws SQLException {
	
    }

    @Override
    public Connection getConnection() {
	return SQLManager.getConnection();
    }

}
