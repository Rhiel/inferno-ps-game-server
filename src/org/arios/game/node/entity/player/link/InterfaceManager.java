package org.arios.game.node.entity.player.link;

import org.arios.game.component.Component;
import org.arios.game.content.global.tutorial.TutorialSession;
import org.arios.game.node.entity.combat.equipment.WeaponInterface;
import org.arios.game.node.entity.player.Player;
import org.arios.game.world.GameWorld;
import org.arios.net.packet.PacketRepository;
import org.arios.net.packet.context.InterfaceContext;
import org.arios.net.packet.context.WindowsPaneContext;
import org.arios.net.packet.out.CloseInterface;
import org.arios.net.packet.out.Interface;
import org.arios.net.packet.out.WindowsPane;

/**
 * Manages a player's interfaces.
 * @author Emperor
 */
public final class InterfaceManager {

    /**
     * The default windows pane.
     */
    public static final int WINDOWS_PANE = 548;

    /**
     * The default chat box interface.
     */
    public static final int DEFAULT_CHATBOX = 137;

    /**
     * The default tabs.
     */
    public static final int[] DEFAULT_TABS = { 92, 320, 274, 149, 387, 271, 192, 662, 550, 551, 589, 261, 464, 187, 182 };

    /**
     * The player.
     */
    private final Player player;

    /**
     * The windows pane.
     */
    private Component windowsPane;

    /**
     * The currently opened component.
     */
    private Component opened;

    /**
     * The tabs.
     */
    private Component[] tabs = new Component[15];

    /**
     * The chatbox component.
     */
    private Component chatbox;

    /**
     * The single tab.
     */
    private Component singleTab;

    /**
     * The overlay component.
     */
    private Component overlay;

    /**
     * The currently opened tab's index.
     */
    private int currentTabIndex = 3;

    /**
     * Constructs a new {@code InterfaceManager} {@code Object}.
     * @param player The player.
     */
    public InterfaceManager(Player player) {
	this.player = player;
    }

    /**
     * Opens the windows pane.
     * @param windowsPane The windows pane.
     * @return The component instance.
     */
    public Component openWindowsPane(Component windowsPane) {
	this.windowsPane = windowsPane;
	PacketRepository.send(WindowsPane.class, new WindowsPaneContext(player, windowsPane.getId(), 0));
	windowsPane.open(player);
	return windowsPane;
    }

    /**
     * Opens a component.
     * @param componentId The component id.
     * @return The opened component.
     */
    public Component openComponent(int componentId) {
	return open(new Component(componentId));
    }

    /**
     * Opens a component.
     * @param component The component to open.
     * @return The opened component.
     */
    public Component open(Component component) {
	if (!close()) {
	    return null;
	}
	if (component.getDefinition() == null) {
	    PacketRepository.send(Interface.class, new InterfaceContext(player, WINDOWS_PANE, 77, component.getId(), false));
	    return opened = component;
	}
	// if (component.getDefinition().getContext() == null) {
	component.getDefinition().setContext(new InterfaceContext(null, WINDOWS_PANE, 77, component.getId(), false));
	// }
	component.open(player);
	return opened = component;
    }

    /**
     * Checks if a main interface.
     * @return {@code True} if so.
     */
    public boolean isOpened() {
	return opened != null;
    }

    /**
     * Checks if the player has a chat box interface opened (disregarding
     * default chat box).
     * @return {@code True} if so.
     */
    public boolean hasChatbox() {
	return chatbox != null && chatbox.getId() != DEFAULT_CHATBOX;
    }

    /**
     * Safely closes the currently opened interface.
     */
    public boolean close() {
	if (player.getAttribute("runscript", null) != null) {
	    player.removeAttribute("runscript");
	    player.getPacketDispatch().sendRunScript(101, "");
	}
	if (opened != null && opened.close(player)) {
	    if (opened != null && opened.getDefinition() != null && opened.getDefinition().getContext() != null) {
		if (!opened.getDefinition().getContext().isWalkable() || opened.getId() == 14) {
		    PacketRepository.send(CloseInterface.class, opened.getDefinition().getContext().setPlayer(player));
		}
	    }
	    opened = null;
	}
	return opened == null;
    }

    /**
     * Checks if the current interface is walkable.
     * @return <code>True</code> if so.
     */
    public boolean isWalkable() {
	if (opened != null) {
	    if (opened.getId() == 389) {
		return false;
	    }
	    if (opened.getDefinition().getContext() != null) {
		if (opened.getDefinition().getContext().isWalkable()) {
		    return true;
		}
	    }
	}
	return true;
    }

    /**
     * Safely closes the component.
     * @param component The component.
     */
    public void close(Component component) {
	if (component.close(player)) {
	    PacketRepository.send(CloseInterface.class, component.getDefinition().getContext().setPlayer(player));
	}
    }

    /**
     * Closes the chatbox interface.
     */
    public void closeChatbox() {
	if (chatbox != null && chatbox.getId() != DEFAULT_CHATBOX) {
	    if (chatbox.close(player)) {
		openChatbox(DEFAULT_CHATBOX);
		player.getPacketDispatch().sendRunScript(101, "");// closes
								  // runscript
								  // chatbox.
	    }
	}
	 
    }

    /**
     * Opens a tab and removes the other tabs.
     * @param component The component to open.
     * @return The component.
     */
    public Component openSingleTab(Component component) {
	if (component.getDefinition().getContext() == null || component.getDefinition().getContext().getComponentId() != 126) {
	    component.getDefinition().setContext(new InterfaceContext(null, WINDOWS_PANE, 126, component.getId(), false));
	}
	component.open(player);
	return singleTab = component;
    }

    /**
     * Closes the current single tab opened.
     */
    public boolean closeSingleTab() {
	if (singleTab != null) {
	    close(singleTab);
	    singleTab = null;
	}
	return true;
    }

    /**
     * Gets the currently opened single tab.
     * @return The tab opened.
     */
    public Component getSingleTab() {
	return singleTab;
    }

    /**
     * Removes the tabs.
     * @param tabs The tab indexes.
     */
    public void removeTabs(int... tabs) {
	boolean changeViewedTab = false;
	for (int slot : tabs) {
	    if (slot == currentTabIndex) {
		changeViewedTab = true;
	    }
	    Component tab = this.tabs[slot];
	    if (tab != null) {
		close(tab);
		this.tabs[slot] = null;
	    }
	}
	if (changeViewedTab) {
	    int currentIndex = -1;
	    if (this.tabs[3] == null) {
		for (int i = 0; i < this.tabs.length; i++) {
		    if (this.tabs[i] != null) {
			currentIndex = i;
			break;
		    }
		}
	    } else {
		currentIndex = 3;
	    }
	    if (currentIndex > -1) {
		setViewedTab(currentIndex);
	    }
	}
    }

    /**
     * Restores the tabs.
     */
    public void restoreTabs() {
	for (int i = 0; i < tabs.length; i++) {
	    if (tabs[i] == null) {
		switch (i) {
		case 0:
		    WeaponInterface inter = player.getExtension(WeaponInterface.class);
		    if (inter == null) {
			player.addExtension(WeaponInterface.class, inter = new WeaponInterface(player));
		    }
		    openTab(0, inter);
		    break;
		case 6:
		    openTab(6, new Component(player.getSpellBookManager().getSpellBook())); // Magic
		    break;
		case 7:
		    if (player.getFamiliarManager().hasFamiliar()) {
			openTab(7, new Component(662));
		    }
		    break;
		default:
		    openTab(i, new Component(DEFAULT_TABS[i]));

		}
	    }
	}
    }

    /**
     * Opens the default tabs.
     */
    public void openDefaultTabs() {
	player.getPacketDispatch().sendInterfaceConfig(548, 51, false);
	WeaponInterface inter = player.getExtension(WeaponInterface.class);
	if (inter == null) {
	    player.addExtension(WeaponInterface.class, inter = new WeaponInterface(player));
	}
	openTab(inter); // Attack
	openTab(new Component(320)); // Skills
	openTab(new Component(274)); // Quest
	openTab(new Component(149)); // inventory
	openTab(new Component(387)); // Equipment
	openTab(new Component(271)); // Prayer
	openTab(new Component(player.getSpellBookManager().getSpellBook())); // Magic
	if (player.getFamiliarManager().hasFamiliar()) {
	    openTab(new Component(662)); // summoning.
	}
	openTab(new Component(550)); // Friends
	openTab(new Component(551)); // Ignores
	openTab(new Component(589)); // Clan chat
	openTab(new Component(261)); // Settings
	openTab(new Component(464)); // Emotes
	openTab(new Component(187)); // Music
	openTab(new Component(182)); // Logout
	if (player.getProperties().getAutocastSpell() != null) {
	    inter.selectAutoSpell(inter.getAutospellId(player.getProperties().getAutocastSpell().getSpellId()), true);
	}
    }

    /**
     * Closes the default tabs.
     */
    public void closeDefaultTabs() {
	WeaponInterface inter = player.getExtension(WeaponInterface.class);
	if (inter != null) {
	    close(inter); // Attack
	}
	close(new Component(320)); // Skills
	close(new Component(274)); // Quest
	close(new Component(149)); // inventory
	close(new Component(387)); // Equipment
	close(new Component(271)); // Prayer
	close(new Component(player.getSpellBookManager().getSpellBook()));
	close(new Component(662)); // summoning.
	close(new Component(550)); // Friends
	close(new Component(551)); // Ignores
	close(new Component(589)); // Clan chat
	close(new Component(261)); // Settings
	close(new Component(464)); // Emotes
	close(new Component(187)); // Music
	close(new Component(182)); // Logout
    }

    /**
     * Opens a tab.
     * @param slot The tab slot;
     * @param component The component.
     */
    public void openTab(int slot, Component component) {
	if (component.getId() == 92 && !(component instanceof WeaponInterface)) {
	    throw new IllegalStateException("Attack tab can only be instanced as " + WeaponInterface.class.getCanonicalName() + "!");
	}
	if (component.getDefinition().getContext() == null) {
	    component.getDefinition().setContext(new InterfaceContext(null, 548, 128 + slot, component.getId(), false));
	}
	component.open(player);
	if (slot == 2 & !GameWorld.isEconomyWorld()) {
	    player.getSavedData().getSpawnData().drawStatsTab(player);
	}
	tabs[slot] = component;
    }

    /**
     * Opens a tab.
     * @param component The component to open.
     */
    public void openTab(Component component) {
	openTab(component.getDefinition().getContext().getComponentId() - 128, component);
    }

    /**
     * Opens a chat box interface.
     * @param componentId The component id.
     */
    public void openChatbox(int componentId) {
	openChatbox(new Component(componentId));
    }

    /**
     * Opens a chat box interface.
     * @param component The component to open.
     */
    public void openChatbox(Component component) {
	if (component.getId() == DEFAULT_CHATBOX) {
	    PacketRepository.send(Interface.class, new InterfaceContext(player, WINDOWS_PANE, 120, 0, true));
	    player.getPacketDispatch().sendInterfaceConfig(548, 121, false);
	    if (chatbox == null) {
		chatbox = component;
		chatbox.open(player);
	    }
	    chatbox = component;
	    player.getConfigManager().set(334, 1);
	} else {
	    chatbox = component;
	    player.getPacketDispatch().sendInterfaceConfig(548, 121, true);
	    chatbox.getDefinition().setContext(new InterfaceContext(null, 548, component.getId() == 389 ? 115 : 120, component.getId(), true));
	    chatbox.open(player);
	}
	if (TutorialSession.getExtension(player).getStage() < TutorialSession.MAX_STAGE) {
	    Component.setUnclosable(player, getChatbox());
	}
    }

    /**
     * Gets the component for the given component id.
     * @param componentId The component id.
     * @return The component.
     */
    public Component getComponent(int componentId) {
	if (opened != null && opened.getId() == componentId) {
	    return opened;
	}
	if (chatbox != null && chatbox.getId() == componentId) {
	    return chatbox;
	}
	if (singleTab != null && singleTab.getId() == componentId) {
	    return singleTab;
	}
	if (overlay != null && overlay.getId() == componentId) {
	    return overlay;
	}
	if (windowsPane.getId() == componentId) {
	    return windowsPane;
	}
	for (Component c : tabs) {
	    if (c != null && c.getId() == componentId) {
		return c;
	    }
	}
	return null;
    }

    /**
     * Sets the currently viewed tab.
     * @param tabIndex The tab index.
     */
    public void setViewedTab(int tabIndex) {
	if (tabs[tabIndex] == null) {
	    return;
	}
	currentTabIndex = tabIndex;
	switch (tabIndex) {
	case 0:
	    tabIndex = 1;
	    break;
	case 1:
	    tabIndex = 2;
	    break;
	case 2:
	    tabIndex = 3;
	    break;
	case 3:
	    tabIndex = 0;
	    break;
	}
	if (tabIndex > 9) {
	    tabIndex--;
	}
	player.getPacketDispatch().sendRunScript(116, "i", tabIndex);
    }

    /**
     * Checks if the main component opened matches the given component id.
     * @param id The component id.
     * @return {@code True} if so.
     */
    public boolean hasMainComponent(int id) {
	return opened != null && opened.getId() == id;
    }

    /**
     * Opens an overlay.
     * @param component The component.
     */
    public void openOverlay(Component component) {
	if (overlay != null && !overlay.close(player)) {
	    return;
	}
	overlay = component;
	// if (overlay.getDefinition().getContext() == null) {
	overlay.getDefinition().setContext(new InterfaceContext(null, 548, 75, component.getId(), true));
	// }
	overlay.open(player);
    }

    /**
     * Closes the current overlay.
     */
    public void closeOverlay() {
	if (overlay != null && overlay.close(player)) {
	    if (overlay.getDefinition().getContext() != null) {
		PacketRepository.send(CloseInterface.class, overlay.getDefinition().getContext().setPlayer(player));
	    } else {
		PacketRepository.send(CloseInterface.class, new InterfaceContext(player, 548, 75, overlay.getId(), true));
	    }
	    overlay = null;
	}
    }

    /**
     * Gets the weapon tab interface.
     * @return The weapon interface.
     */
    public WeaponInterface getWeaponTab() {
	return player.getExtension(WeaponInterface.class);
    }

    /**
     * Gets the opened.
     * @return The opened.
     */
    public Component getOpened() {
	return opened;
    }

    /**
     * Sets the opened.
     * @param opened The opened to set.
     */
    public void setOpened(Component opened) {
	this.opened = opened;
    }

    /**
     * Gets the tabs.
     * @return The tabs.
     */
    public Component[] getTabs() {
	return tabs;
    }

    /**
     * Sets the tabs.
     * @param tabs The tabs to set.
     */
    public void setTabs(Component[] tabs) {
	this.tabs = tabs;
    }

    /**
     * Gets the chatbox.
     * @return The chatbox.
     */
    public Component getChatbox() {
	return chatbox;
    }

    /**
     * Sets the chatbox.
     * @param chatbox The chatbox to set.
     */
    public void setChatbox(Component chatbox) {
	this.chatbox = chatbox;
    }

    /**
     * Gets the overlay.
     * @return The overlay.
     */
    public Component getOverlay() {
	return overlay;
    }

    /**
     * Sets the overlay.
     * @param overlay The overlay to set.
     */
    public void setOverlay(Component overlay) {
	this.overlay = overlay;
    }

    /**
     * Gets the windowsPaneId.
     * @return The windowsPane.
     */
    public static int getWindowsPaneId() {
	return WINDOWS_PANE;
    }

    /**
     * Gets the player.
     * @return The player.
     */
    public Player getPlayer() {
	return player;
    }

    /**
     * Gets the currentTabIndex.
     * @return The currentTabIndex.
     */
    public int getCurrentTabIndex() {
	return currentTabIndex;
    }

    /**
     * Sets the currentTabIndex.
     * @param currentTabIndex The currentTabIndex to set.
     */
    public void setCurrentTabIndex(int currentTabIndex) {
	this.currentTabIndex = currentTabIndex;
    }

    /**
     * Gets the windowsPane.
     * @return The windowsPane.
     */
    public Component getWindowsPane() {
	return windowsPane;
    }
}