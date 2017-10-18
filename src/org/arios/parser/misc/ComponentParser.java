package org.arios.parser.misc;

import java.nio.ByteBuffer;

import org.arios.cache.Cache;
import org.arios.cache.ServerStore;
import org.arios.game.component.ComponentDefinition;
import org.arios.parser.Parser;

/**
 * The component definitions parser.
 * @author Emperor
 */
public final class ComponentParser implements Parser {

    @Override
    public boolean parse() throws Throwable {
	ByteBuffer buf = ServerStore.getArchive("component_config");
	for (int id = 0; id < Cache.getInterfaceDefinitionsSize(); id++) {
	    ComponentDefinition.getDefinitions().put(id, ComponentDefinition.parse(buf));
	}
	return true;
    }

}