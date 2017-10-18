package org.arios.tools.impl;

import java.nio.ByteBuffer;

import org.arios.ServerConstants;
import org.arios.cache.Cache;
import org.arios.cache.ServerStore;
import org.arios.cache.misc.buffer.ByteBufferUtils;
import org.arios.game.component.ComponentDefinition;
import org.arios.game.world.GameWorld;
import org.arios.net.packet.context.AccessMaskContext;
import org.arios.net.packet.context.ConfigContext;
import org.arios.net.packet.context.InterfaceContext;
import org.arios.net.packet.context.RunScriptContext;

/**
 * Handles the component definitions editing.
 * @author Emperor
 */
public final class ComponentDefinitionsEditor {

    public static final int MAIN_SLOT = 77;
    public static final int CHATBOX_SLOT = 120;
    public static final int SINGLE_TAB_SLOT = 126;

    /**
     * The main method.
     * @param args The arguments.
     * @throws Throwable When an exception occurs.
     */
    public static final void main(String... args) throws Throwable {
	GameWorld.prompt(false);
	System.out.println("Loaded components.");
	ComponentDefinition def = ComponentDefinition.forId(667);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 667, false));
	def = ComponentDefinition.forId(13);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 13, false));
	def = ComponentDefinition.forId(102);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 102, false));
	def = ComponentDefinition.forId(12);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 12, false));
	def.setAccessMask(new AccessMaskContext(null, 1026, 89, 12, 0, 536));
	def = ComponentDefinition.forId(259);
	def.setContext(new InterfaceContext(null, 548, 130, 259, true));

	// grand exchange//7, 752, 7, 38
	def = ComponentDefinition.forId(389); // 120
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 389, false));
	def.setCs2ScriptContext(new RunScriptContext(null, 570, "s", new Object[] { "Grand Exchange Item Search" }));

	def = ComponentDefinition.forId(15); // child 126 is for single tabs.
	def.setContext(new InterfaceContext(null, 548, SINGLE_TAB_SLOT, 15, false)); // True
	def.setAccessMask(new AccessMaskContext(null, 1026, 0, 15, 0, 28));

	def = ComponentDefinition.forId(336);
	def.setContext(new InterfaceContext(null, 548, 131, 336, true)); // True
	def.setCs2ScriptContext(new RunScriptContext(null, 150, "IviiiIsssssssss", "", "", "", "", "", "", "", "", "Wear", -1, 0, 7, 4, 98, 22020096));
	def.setAccessMask(new AccessMaskContext(null, 1278, 0, 336, 0, 28));

	// Wildy interface
	def = ComponentDefinition.forId(380);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 380, true));
	// Fight pits interface
	def = ComponentDefinition.forId(373);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 373, true));

	def = ComponentDefinition.forId(553);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 553, false));
	def = ComponentDefinition.forId(662);
	def.setContext(new InterfaceContext(null, 548, 135, 662, true));
	// GE
	/*
	 * def = ComponentDefinition.forId(389); def.setContext(new
	 * InterfaceContext(null, 548, CHATBOX_SLOT, 389, true));
	 */
	// Dialogue interfaces.
	def = ComponentDefinition.forId(140);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 140, true));
	def = ComponentDefinition.forId(228);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 228, true));
	def = ComponentDefinition.forId(230);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 230, true));
	def = ComponentDefinition.forId(232);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 232, true));
	def = ComponentDefinition.forId(234);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 234, true));
	def = ComponentDefinition.forId(241);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 241, true));
	def = ComponentDefinition.forId(242);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 242, true));
	def = ComponentDefinition.forId(243);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 243, true));
	def = ComponentDefinition.forId(244);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 244, true));
	def = ComponentDefinition.forId(64);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 64, true));
	def = ComponentDefinition.forId(131);// one line two or one items on
					     // inter.
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 131, true));
	def = ComponentDefinition.forId(307);// cooking
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 307, true));
	def = ComponentDefinition.forId(311);// smelting
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 311, true));
	def = ComponentDefinition.forId(74);// smelting
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 74, true));
	def = ComponentDefinition.forId(65);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 65, true));
	def = ComponentDefinition.forId(66);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 66, true));
	def = ComponentDefinition.forId(67);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 67, true));
	def = ComponentDefinition.forId(210);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 210, true));
	def = ComponentDefinition.forId(211);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 211, true));
	def = ComponentDefinition.forId(212);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 212, true));
	def = ComponentDefinition.forId(213);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 213, true));
	def = ComponentDefinition.forId(192);
	def.setContext(new InterfaceContext(null, 548, 134, 192, true));
	def = ComponentDefinition.forId(193);
	def.setContext(new InterfaceContext(null, 548, 134, 193, true));
	def = ComponentDefinition.forId(430);
	def.setContext(new InterfaceContext(null, 548, 134, 430, true));
	def = ComponentDefinition.forId(621);
	def.setContext(new InterfaceContext(null, 548, SINGLE_TAB_SLOT, 621, false));
	def.setAccessMask(new AccessMaskContext(null, 1278, 0, 621, 0, 28));
	def = ComponentDefinition.forId(620);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 620, false));
	def.setAccessMask(new AccessMaskContext(null, 1278, 24, 620, 0, 40));

	int[] levelupInterfaces = new int[] { 158, 161, 175, 167, 171, 170, 168, 159, 177, 165, 164, 163, 160, 174, 169, 166, 157, 176, 173, 162, 172, 519, 395, 630 };
	for (int id : levelupInterfaces) {
	    def = ComponentDefinition.forId(id);
	    def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, id, true));
	}
	def = ComponentDefinition.forId(668);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 668, true));
	def = ComponentDefinition.forId(306);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 306, true));

	def = ComponentDefinition.forId(140);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 140, true));

	def = ComponentDefinition.forId(303);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 303, true));

	def = ComponentDefinition.forId(309);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 309, true));

	def = ComponentDefinition.forId(304);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 304, true));

	def = ComponentDefinition.forId(305);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 305, true));

	/**
	 * The musuem kudos.
	 */
	def = ComponentDefinition.forId(532);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 532, true));

	/**
	 * 94 : destroy item.
	 */
	def = ComponentDefinition.forId(94);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 94, true));

	/**
	 * 319
	 */
	def = ComponentDefinition.forId(319);
	def.setContext(new InterfaceContext(null, 548, SINGLE_TAB_SLOT, 319, false));

	def = ComponentDefinition.forId(324);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 324, false));

	def = ComponentDefinition.forId(660);
	def.setContext(new InterfaceContext(null, 548, MAIN_SLOT, 660, false));

	def = ComponentDefinition.forId(372);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 372, true));

	def = ComponentDefinition.forId(582);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 582, true));

	def = ComponentDefinition.forId(421);
	def.setContext(new InterfaceContext(null, 548, CHATBOX_SLOT, 421, true));

	// Don't remove.
	for (ComponentDefinition c : ComponentDefinition.getDefinitions().values()) {
	    if (c.getContext() != null && c.getContext().getWindowId() == 548) {
		if (c.getContext().getComponentId() == 75 && c.getContext().isWalkable()) {
		    c.setContext(new InterfaceContext(null, 548, MAIN_SLOT, c.getContext().getInterfaceId(), true));
		} else if (c.getContext().getComponentId() == 77 && !c.getContext().isWalkable()) {
		    c.setContext(new InterfaceContext(null, 548, 75, c.getContext().getInterfaceId(), false));
		}
	    }
	}
	dump();
    }

    /**
     * Dumps the component definitions.
     */
    private static void dump() {
	System.out.println("Dumping " + Cache.getInterfaceDefinitionsSize() + " components...");
	ByteBuffer buffer = ByteBuffer.allocate(2048);
	for (int id = 0; id < Cache.getInterfaceDefinitionsSize(); id++) {
	    ComponentDefinition def = ComponentDefinition.forId(id);
	    if (def == null) {
		buffer.put((byte) 0);
		continue;
	    }
	    if (def.getContext() != null) {
		System.out.println("Dumped component [id=" + id + ", child=" + def.getContext().getComponentId() + ", walk=" + def.getContext().isWalkable() + "].");
		buffer.put((byte) 1).putInt(def.getContext().getWindowId() << 16 | def.getContext().getComponentId()).putShort((short) def.getContext().getInterfaceId()).put((byte) (def.getContext().isWalkable() ? 1 : 0));

	    } else {
		System.out.println("Dumped component [id=" + id + "].");
	    }
	    if (def.getAccessMask() != null) {
		buffer.put((byte) 2).putShort((short) def.getAccessMask().getId()).putInt(def.getAccessMask().getChildId() << 16 | def.getAccessMask().getInterfaceId()).putShort((short) def.getAccessMask().getOffset()).putShort((short) def.getAccessMask().getLength());
	    }
	    if (def.getConfigContext().length > 0) {
		buffer.put((byte) 3).put((byte) def.getConfigContext().length);
		for (ConfigContext context : def.getConfigContext()) {
		    buffer.putShort((short) context.getId()).putInt(context.getValue()).put((byte) (context.isCs2() ? 1 : 0));
		}
	    }
	    if (def.getCs2ScriptContext() != null) {
		buffer.put((byte) 4).putShort((short) def.getCs2ScriptContext().getId());
		ByteBufferUtils.putString(def.getCs2ScriptContext().getString(), buffer);
		for (Object o : def.getCs2ScriptContext().getObjects()) {
		    if (o instanceof String) {
			ByteBufferUtils.putString((String) o, buffer);
		    } else {
			buffer.putInt((Integer) o);
		    }
		}
	    }
	    buffer.put((byte) 0);
	}
	buffer.flip();
	ServerStore.setArchive("component_config", buffer, false);
	ServerStore.createStaticStore(ServerConstants.STORE_PATH);
    }
}