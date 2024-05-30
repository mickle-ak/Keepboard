package kb.gui;

import com.sun.jna.Platform;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import kb.ActivationHotKeyHandler;
import kb.gui.toolbar.ToolbarHandler;
import kb.utils.ObjectIO;

import javax.swing.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public enum ShortcutKeyItem {
	
	DISPLAY_MAIN_FRAME(ShortcutKeyCategory.APPLICATION, "Show Keepboard"),
	SWITCH_TO_GROUP(ShortcutKeyCategory.SAVED_ITEMS_MENU, "View group"),
	SWITCH_TO_CLIPBOARD_HISTORY(ShortcutKeyCategory.SAVED_ITEMS_MENU, "View clipboard history panel"),
	VIEW_ITEM_GROUP(ShortcutKeyCategory.SAVED_ITEMS_MENU, "View groups panel"),
	PAUSE_CLIPBOARD_TRACKING(ShortcutKeyCategory.CLIPBOARD_MENU, "Pause clipboard tracking"),
	EMPTY_CLIPBOARD(ShortcutKeyCategory.CLIPBOARD_MENU, "Clear current clipboard content"),
	SAVE_CLIPBOARD_CONTENT(ShortcutKeyCategory.CLIPBOARD_MENU, "Save current clipboard content"),
	SHORTCUT_KEYS(ShortcutKeyCategory.TOOLS_MENU, "Shortcut keys"),
	PREFERENCES(ShortcutKeyCategory.TOOLS_MENU, "Preferences"),
	SEARCH(ShortcutKeyCategory.TOOLS_MENU, "Search"),
	NEW_ITEM(ShortcutKeyCategory.MAIN_FRAME, "New item"),
	OPEN_SELECTED_ITEM(ShortcutKeyCategory.MAIN_FRAME, "Open selected item"),
	COPY_SELECTED_ITEMS(ShortcutKeyCategory.MAIN_FRAME, "Copy selected items to group"),
	CLOSE_SEARCH_TAB(ShortcutKeyCategory.MAIN_FRAME, "Close search tab");
	
	private ShortcutKeyItem(ShortcutKeyCategory category, String description) {
		this.description = description;
		this.category = category;
	}
	
	private final ShortcutKeyCategory category;
	private final String description;
	private Key shortcutKey;

	public Key getShortcutKey() {
		return shortcutKey;
	}

	public void setShortcutKey(Key shortcutKey) {
		this.shortcutKey = shortcutKey;
	}

	public String getDescription() {
		return description;
	}

	public ShortcutKeyCategory getCategory() {
		return category;
	}
	
	private static void persist() {
		List<XMLElement> elements = new ArrayList<XMLElement>();
		for (ShortcutKeyItem item : values()) {
			if (item.getShortcutKey() != null) {
				elements.add(new XMLElement(item));
			}
		}
		writeToDisk(createXStream().toXML(elements));
	}
	
	private static void writeToDisk(String xml) {
		new ObjectIO().writeText(xml, "prefs/shortcutKeys.shky");
	}
	
	private static XStream createXStream() {
		XStream result = new XStream();
		result.addPermission(AnyTypePermission.ANY);
    	result.alias("item", XMLElement.class);
    	return result;
	}
	
	public static void loadPersistedState() {
		try {
			initItems(readElementsFromDisk());
		} catch (Throwable e) {
			initItems(getDefaulValues());
		}
	}

	private static List<XMLElement> getDefaulValues() {
		return Arrays.asList(
				new XMLElement(DISPLAY_MAIN_FRAME.name(), InputEvent.CTRL_MASK, KeyEvent.VK_BACK_QUOTE),
				new XMLElement(SWITCH_TO_GROUP.name(), InputEvent.CTRL_MASK, KeyEvent.VK_1),
				new XMLElement(SWITCH_TO_CLIPBOARD_HISTORY.name(), InputEvent.CTRL_MASK, KeyEvent.VK_Q),
				new XMLElement(VIEW_ITEM_GROUP.name(), InputEvent.CTRL_MASK, KeyEvent.VK_2),
				new XMLElement(SAVE_CLIPBOARD_CONTENT.name(), InputEvent.CTRL_MASK, KeyEvent.VK_S),
				new XMLElement(SEARCH.name(), Platform.isMac() ? InputEvent.META_MASK : InputEvent.CTRL_MASK, KeyEvent.VK_F),
				new XMLElement(OPEN_SELECTED_ITEM.name(), 0, KeyEvent.VK_F4),
				new XMLElement(COPY_SELECTED_ITEMS.name(), 0, KeyEvent.VK_F5),
				new XMLElement(CLOSE_SEARCH_TAB.name(), Platform.isMac() ? InputEvent.META_MASK : InputEvent.CTRL_MASK, KeyEvent.VK_W));
	}

	@SuppressWarnings("unchecked")
	private static List<XMLElement> readElementsFromDisk() {
		return (List<XMLElement>) createXStream().fromXML(new ObjectIO().readText("prefs/shortcutKeys.shky"));
	}

	private static void initItems(List<XMLElement> elements) {
		for (XMLElement element : elements) {
			ShortcutKeyItem.valueOf(element.name).setShortcutKey(new Key(element.modifiers, element.keyCode));
		}
	}
	
	public static void reinitAndPersist() {
		reinitShortcutKeys();
		persist();
	}
	
	public static void reinitShortcutKeys() {
		reinitDisplayMainFrameShortcut();
		reinitUIRelatedShortcuts();
	}

	public static void reinitUIRelatedShortcuts() {
		reinitMainFrameMenuItems();
		reinitToolbarButtonsShortcuts();
		reinitTabClosingShortcuts();
	}

	private static void reinitToolbarButtonsShortcuts() {
		reinitButtonShortcut(ToolbarHandler.getInstance().getTbNewItem(), NEW_ITEM);
		reinitButtonShortcut(ToolbarHandler.getInstance().getTbOpenItem(), OPEN_SELECTED_ITEM);
		reinitButtonShortcut(ToolbarHandler.getInstance().getTbCopyItems(), COPY_SELECTED_ITEMS);
		reinitButtonShortcut(ToolbarHandler.getInstance().getTbSearch(), SEARCH);
	}

	private static void reinitTabClosingShortcuts() {
		reinitButtonShortcut(MainFrame.getInstance().getSearchTabComponent().getCloseButton(), CLOSE_SEARCH_TAB);
	}

	private static void reinitButtonShortcut(JButton toolbarButton, ShortcutKeyItem shortcutKeyItem) {
		Key key = shortcutKeyItem.getShortcutKey();
		
		if (key != null) {
			getMainFrameInputMap().put(key.getKeyStroke(), getActionMapKey(shortcutKeyItem));
			getMainFrameActionMap().put(getActionMapKey(shortcutKeyItem), toolbarButton.getAction());
			toolbarButton.setToolTipText(shortcutKeyItem.getDescription() + " (" + key.toText() + ")");
		} else {
			getMainFrameActionMap().put(getActionMapKey(shortcutKeyItem), null);
			toolbarButton.setToolTipText(shortcutKeyItem.getDescription());
		}
	}
	
	private static String getActionMapKey(ShortcutKeyItem shortcutKeyItem) {
		return "MY_" + shortcutKeyItem.name();
	}

	private static ActionMap getMainFrameActionMap() {
		return MainFrame.getInstance().getRootPane().getActionMap();
	}

	private static InputMap getMainFrameInputMap() {
		return MainFrame.getInstance().getRootPane().getInputMap(JRootPane.WHEN_IN_FOCUSED_WINDOW);
	}

	private static void reinitMainFrameMenuItems() {
		MainFrameMenuBarHolder menuBarHolder = MainFrame.getInstance().getMenuBarHolder();
		reinitMenuItemShortcut(menuBarHolder.getGoToItemGroupMenuItem(), SWITCH_TO_GROUP);
		reinitMenuItemShortcut(menuBarHolder.getGoToClipboardHistoryMenuItem(), SWITCH_TO_CLIPBOARD_HISTORY);
		reinitMenuItemShortcut(menuBarHolder.getViewItemGroupPanelMenuItem(), VIEW_ITEM_GROUP);
		reinitMenuItemShortcut(menuBarHolder.getPauseClipboardTrackingMenuItem(), PAUSE_CLIPBOARD_TRACKING);
		reinitMenuItemShortcut(menuBarHolder.getEmptyClipboardMenuItem(), EMPTY_CLIPBOARD);
		reinitMenuItemShortcut(menuBarHolder.getSaveClipboardContentMenuItem(), SAVE_CLIPBOARD_CONTENT);
		reinitMenuItemShortcut(menuBarHolder.getShortcutKeysMenuItem(), SHORTCUT_KEYS);
		reinitMenuItemShortcut(menuBarHolder.getPreferencesMenuItem(), PREFERENCES);
		reinitMenuItemShortcut(menuBarHolder.getSearchMenuItem(), SEARCH);
	}

	private static void reinitDisplayMainFrameShortcut() {
		Key key = DISPLAY_MAIN_FRAME.getShortcutKey();
		if (key != null) {
			ActivationHotKeyHandler.getInstance().registerActivationHotKey(KeyStroke.getKeyStroke(key.getKeyCode(),
					key.getModifiers()));
		} else {
			ActivationHotKeyHandler.getInstance().registerActivationHotKey(null);
		}
	}

	private static void reinitMenuItemShortcut(JMenuItem menuItem, ShortcutKeyItem shortcutKeyItem) {
		Key key = shortcutKeyItem.getShortcutKey();
		if (key != null) {
			menuItem.setAccelerator(KeyStroke.getKeyStroke(key.getKeyCode(), key.getModifiers()));
		} else {
			menuItem.setAccelerator(null);
		}
	}

	private static class XMLElement {
		String name;
		int modifiers;
		int keyCode;
		
		public XMLElement(ShortcutKeyItem item) {
			name = item.name();
			modifiers = item.getShortcutKey().getModifiers();
			keyCode = item.getShortcutKey().getKeyCode();
		}

		public XMLElement(String name, int modifiers, int keyCode) {
			this.name = name;
			this.modifiers = modifiers;
			this.keyCode = keyCode;
		}
	}
}
