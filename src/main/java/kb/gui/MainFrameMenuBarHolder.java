package kb.gui;

import kb.gui.listeners.*;
import kb.gui.prefs.MyPreferences;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MainFrameMenuBarHolder {
	private JMenuItem pauseClipboardTrackingMenuItem;
	private JMenuItem saveClipboardContentMenuItem;
	private JMenuItem emptyClipboardMenuItem;
	private JMenuItem goToItemGroupMenuItem;
	private JMenuItem goToClipboardHistoryMenuItem;
	private JMenuItem viewItemGroupPanelMenuItem;
	private JMenuItem shortcutKeysMenuItem;
	private JMenuItem preferencesMenuItem;
	private JMenuItem searchMenuItem;
	
	public JMenuBar createAndInitMenuBar() {
		JMenuBar result = createMenuBar();
		disablePausedStateMenuItems();
		return result;
	}
	
	private void disablePausedStateMenuItems() {
		setPausedStateMenuItemsEnabled(false);
	}
	
	private void enablePausedStateMenuItems() {
		setPausedStateMenuItemsEnabled(true);
	}
	
	private void setPausedStateMenuItemsEnabled(boolean enabled) {
		saveClipboardContentMenuItem.setEnabled(enabled);
	}
	
	public JMenuItem getGoToItemGroupMenuItem() {
		return goToItemGroupMenuItem;
	}
	
	public JMenuItem getGoToClipboardHistoryMenuItem() {
		return goToClipboardHistoryMenuItem;
	}
	
	public JMenuItem getEmptyClipboardMenuItem() {
		return emptyClipboardMenuItem;
	}
	
	public JMenuItem getSaveClipboardContentMenuItem() {
		return saveClipboardContentMenuItem;
	}
	
	public JMenuItem getPauseClipboardTrackingMenuItem() {
		return pauseClipboardTrackingMenuItem;
	}

	private JMenuBar createMenuBar() {
		JMenuBar result = new JMenuBar();
		result.add(createClipboardMenu());
		result.add(createViewMenu());
		result.add(createItemsGroupMenu());
		result.add(createToolsMenu());
		result.add(createHelpMenu());
		return result;
	}

	private JMenu createViewMenu() {
		JMenu result = new JMenu("View");
		result.setMnemonic(KeyEvent.VK_W);
		result.add(createPreviewAreaPositionMenu());
		result.addSeparator();
		result.add(createShowTabsMenu());
		result.add(createShowButtonsMenu());
		result.add(createShowGroupComboMenu());
		result.add(createShowToolbarMenu());
		result.add(createShowSystemTrayIconMenu());
		PopupMenuUtils.addGlobalPopupMenuListener(result.getPopupMenu());
		return result;
	}

	private JMenu createPreviewAreaPositionMenu() {
		JMenu result = new JMenu("Preview area location");
		ButtonGroup buttonGroup = new ButtonGroup();
		result.add(createPreviewAreaPositionMenuItem("Top", PreviewAreaPosition.TOP, buttonGroup));
		result.add(createPreviewAreaPositionMenuItem("Left", PreviewAreaPosition.LEFT, buttonGroup));
		result.add(createPreviewAreaPositionMenuItem("Bottom", PreviewAreaPosition.BOTTOM, buttonGroup));
		result.add(createPreviewAreaPositionMenuItem("Right", PreviewAreaPosition.RIGHT, buttonGroup));
		return result;
	}

	private JMenuItem createPreviewAreaPositionMenuItem(String text, PreviewAreaPosition previewAreaPosition, ButtonGroup buttonGroup) {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem(text);
		buttonGroup.add(result);
		result.setSelected(MyPreferences.INSTANCE.getPreviewAreaPosition() == previewAreaPosition);
		result.addActionListener(new PreviewAreaPositionActionListener(previewAreaPosition));
		return result;
	}

	private JMenuItem createShowSystemTrayIconMenu() {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem("Show icon in system tray");
		result.setSelected(!MyPreferences.INSTANCE.isTrayIconHidden());
		result.setEnabled(SystemTrayHandler.getInstance().isSystemTraySupported());
		result.addActionListener(new ShowSystemTrayIconActionListener(result));
		return result;
	}

	private JMenuItem createShowToolbarMenu() {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem("Show toolbar");
		result.setSelected(!MyPreferences.INSTANCE.isToolbarHidden());
		result.addActionListener(new ShowToolbarActionListener(result));
		return result;
	}

	private JMenuItem createShowTabsMenu() {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem("Show tabs");
		result.setSelected(!MyPreferences.INSTANCE.areTabsHidden());
		result.addActionListener(new ShowTabsActionListener(result));
		return result;
	}

	private JMenuItem createShowButtonsMenu() {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem("Show main frame buttons");
		result.setSelected(!MyPreferences.INSTANCE.areButtonsHidden());
		result.addActionListener(new ShowButtonsActionListener(result));
		return result;
	}

	private JMenuItem createShowGroupComboMenu() {
		JCheckBoxMenuItem result = new JCheckBoxMenuItem("Show group drop-down list");
		result.setSelected(!MyPreferences.INSTANCE.isGroupComboBoxHidden());
		result.addActionListener(new ShowGroupComboBoxActionListener(result));
		return result;
	}

	private JMenu createToolsMenu() {
		JMenu result = new JMenu("Tools");
		result.setMnemonic(KeyEvent.VK_T);
		result.add(shortcutKeysMenuItem = createKeyShortcutsMenu());
		result.add(preferencesMenuItem = createPreferencesMenu());
		result.add(searchMenuItem = createSearchMenu());
		PopupMenuUtils.addGlobalPopupMenuListener(result.getPopupMenu());
		return result;
	}

	private JMenuItem createSearchMenu() {
		JMenuItem result = new JMenuItem("Search...");
		result.addActionListener(new SearchActionListener());
		return result;
	}

	private JMenuItem createPreferencesMenu() {
		JMenuItem result = new JMenuItem("Preferences...");
		result.addActionListener(new PreferencesActionListener());
		return result;
	}

	private JMenuItem createKeyShortcutsMenu() {
		JMenuItem result = new JMenuItem("Shortcut keys...");
		result.addActionListener(new ShortcutKeysActionListener());
		return result;
	}

	private JMenu createHelpMenu() {
		JMenu result = new JMenu("Help");
		result.setMnemonic(KeyEvent.VK_H);
		result.add(createWhatsNewMenu());
		result.addSeparator();
		result.add(createDonateMenu());
		result.add(createAboutMenu());
		PopupMenuUtils.addGlobalPopupMenuListener(result.getPopupMenu());
		return result;
	}

	private JMenuItem createWhatsNewMenu() {
		JMenuItem result = new JMenuItem("What's new?");
		result.addActionListener(new WhatsNewActionListener());
		return result;
	}

	private JMenuItem createDonateMenu() {
		JMenuItem result = new JMenuItem("Donate...");
		result.addActionListener(new DonateActionListener());
		return result;
	}

	private JMenuItem createAboutMenu() {
		JMenuItem result = new JMenuItem("About Keepboard...");
		result.addActionListener(new AboutActionListener());
		return result;
	}

	private JMenu createClipboardMenu() {
		JMenu result = new JMenu("Clipboard");
		result.setMnemonic(KeyEvent.VK_P);
		result.add(pauseClipboardTrackingMenuItem = createPauseClipboardTrackingMenuItem());
		result.add(emptyClipboardMenuItem = createEmptyClipboardMenuItem());
		result.add(saveClipboardContentMenuItem = createSaveClipboardContentMenuItem());
		result.addSeparator();
		result.add(crateExitMenuItem());
		PopupMenuUtils.addGlobalPopupMenuListener(result.getPopupMenu());
		return result;
	}
	
	private JMenuItem crateExitMenuItem() {
		JMenuItem result = new JMenuItem("Exit");
		result.addActionListener(new ExitActionListener());
		return result;
	}

	private JMenu createItemsGroupMenu() {
		JMenu result = new JMenu("Saved items");
		result.setMnemonic(KeyEvent.VK_S);
		result.add(goToItemGroupMenuItem = createGoToItemGroupMenuItem());
		result.addSeparator();
		result.add(goToClipboardHistoryMenuItem = createGoToClipboardHistoryMenuItem());
		result.add(viewItemGroupPanelMenuItem = viewItemGroupPanelMenuItem());
		PopupMenuUtils.addGlobalPopupMenuListener(result.getPopupMenu());
		return result;
	}

	private JMenuItem viewItemGroupPanelMenuItem() {
		JMenuItem result = new JMenuItem("View groups panel");
		result.addActionListener(new ViewGroupsPanelActionListener());
		return result;
	}

	private JMenuItem createGoToClipboardHistoryMenuItem() {
		JMenuItem result = new JMenuItem("View clipboard history panel");
		result.addActionListener(new GoToClipboardHistoryActionListener());
		return result;
	}

	private JMenuItem createGoToItemGroupMenuItem() {
		JMenuItem result = new JMenuItem("View group...");
		result.addActionListener(new GoToItemGroupActionListener());
		return result;
	}

	private JMenuItem createPauseClipboardTrackingMenuItem() {
		JMenuItem result = new JMenuItem("Pause clipboard tracking");
		result.addActionListener(new PauseActionListener());
		return result;
	}

	private JMenuItem createEmptyClipboardMenuItem() {
		JMenuItem result = new JMenuItem("Clear current clipboard content");
		result.addActionListener(new EmptyClipboardActionListener());
		return result;
	}
	
	private JMenuItem createSaveClipboardContentMenuItem() {
		JMenuItem result = new JMenuItem("Save current clipboard content");
		result.addActionListener(new SaveClipboardContentActionListener());
		return result;
	}

	public void clipboardChangeTrackingContinued() {
		disablePausedStateMenuItems();
		pauseClipboardTrackingMenuItem.setText("Pause clipboard tracking");
	}
	
	public void clipboardChangeTrackingPaused() {
		enablePausedStateMenuItems();
		pauseClipboardTrackingMenuItem.setText("Continue clipboard tracking");
	}

	public JMenuItem getShortcutKeysMenuItem() {
		return shortcutKeysMenuItem;
	}

	public JMenuItem getPreferencesMenuItem() {
		return preferencesMenuItem;
	}

	public JMenuItem getSearchMenuItem() {
		return searchMenuItem;
	}

	public JMenuItem getViewItemGroupPanelMenuItem() {
		return viewItemGroupPanelMenuItem;
	}
}
