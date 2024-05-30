package kb.gui.prefs;

import com.sun.jna.Platform;
import kb.gui.Key;
import kb.gui.OkComponentPopupMenuHolder.PasteMode;
import kb.gui.PreviewAreaPosition;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class MyPreferences {
	
	public static final MyPreferences INSTANCE = new MyPreferences();
	
	private Key autopasteKey = new Key(Platform.isMac() ? InputEvent.META_MASK : InputEvent.CTRL_MASK, KeyEvent.VK_V);

	private Key focusActiveWindowKey = new Key(InputEvent.CTRL_MASK, KeyEvent.VK_F4);

	private boolean autostart = true;
	
	private boolean autosaveImages = true;
	
	private LookAndFeelPref lookAndFeelPref = LookAndFeelPref.NIMBUS;
	
	private Size mainFrameSize = new Size(430, 510);
	
	private boolean paused = false;
	
	private boolean itemPreviewHidden = false;
	
	private PasteMode pasteMode = PasteMode.PASTE;
	
	private boolean showNameSearchColumn = false;
	
	private boolean showGroupSearchColumn = false;
	
	private boolean tabsHidden = false;
	
	private boolean buttonsHidden = false;
	
	private boolean groupComboBoxHidden = false;
	
	private boolean toolbarHidden = false;
	
	private boolean trayIconHidden = false;
	
	private boolean allGroupsManuallyDeleted = false;
	
	private int dividerLocation = 0;
	
	private PreviewAreaPosition previewAreaPosition = PreviewAreaPosition.BOTTOM;
	
	private boolean clipboardHistoryPreviewDialogLineWrap = false;
	
	private boolean namedItemsPreviewDialogLineWrap = false;
	
	private boolean previewAreaLineWrap = false;
	
	private Size clipboardHistoryPreviewDialogSize = new Size(600, 400);
	
	private Size namedItemsPreviewDialogSize = new Size(600, 400);
	
	private int maxHistorySize = 10000;

	private long installationDate;

	private boolean donateReminderOff;
	
	private MyPreferences() {
		// private constructor
	}

	public synchronized Key getAutopasteKey() {
		return autopasteKey;
	}

	public synchronized void setAutopasteKey(Key key) {
		this.autopasteKey = key;
	}

	public synchronized Key getFocusActiveWindowKey() {
		return focusActiveWindowKey;
	}

	public synchronized void setFocusActiveWindowKey(Key focusActiveWindowKey) {
		this.focusActiveWindowKey = focusActiveWindowKey;
	}

	public synchronized boolean isAutostart() {
		return autostart;
	}

	public synchronized void setAutostart(boolean autostart) {
		this.autostart = autostart;
	}

	public synchronized LookAndFeelPref getLookAndFeelPref() {
		return lookAndFeelPref;
	}

	public synchronized void setLookAndFeelPref(LookAndFeelPref lookAndFeelPref) {
		this.lookAndFeelPref = lookAndFeelPref;
	}

	public synchronized Size getMainFrameSize() {
		return mainFrameSize;
	}

	public synchronized void setMainFrameSize(Size mainFrameSize) {
		this.mainFrameSize = mainFrameSize;
	}

	public synchronized boolean isPaused() {
		return paused;
	}

	public synchronized void setPaused(boolean paused) {
		this.paused = paused;
	}

	public synchronized boolean isAutosaveImages() {
		return autosaveImages;
	}

	public synchronized void setAutosaveImages(boolean autosaveImages) {
		this.autosaveImages = autosaveImages;
	}

	public synchronized boolean isItemPreviewHidden() {
		return itemPreviewHidden;
	}

	public synchronized void setItemPreviewHidden(boolean itemPreviewHidden) {
		this.itemPreviewHidden = itemPreviewHidden;
	}

	public synchronized PasteMode getPasteMode() {
		return pasteMode;
	}

	public synchronized void setPasteMode(PasteMode pasteMode) {
		this.pasteMode = pasteMode;
	}

	public synchronized boolean isShowNameSearchColumn() {
		return showNameSearchColumn;
	}

	public synchronized void setShowNameSearchColumn(boolean showNameSearchColumn) {
		this.showNameSearchColumn = showNameSearchColumn;
	}

	public synchronized boolean isShowGroupSearchColumn() {
		return showGroupSearchColumn;
	}

	public synchronized void setShowGroupSearchColumn(boolean showGroupSearchColumn) {
		this.showGroupSearchColumn = showGroupSearchColumn;
	}

	public synchronized boolean areTabsHidden() {
		return tabsHidden;
	}

	public synchronized void setTabsHidden(boolean tabsHidden) {
		this.tabsHidden = tabsHidden;
	}

	public synchronized boolean areButtonsHidden() {
		return buttonsHidden;
	}

	public synchronized void setButtonsHidden(boolean buttonsHidden) {
		this.buttonsHidden = buttonsHidden;
	}

	public synchronized boolean isGroupComboBoxHidden() {
		return groupComboBoxHidden;
	}

	public synchronized void setGroupComboBoxHidden(boolean groupComboBoxHidden) {
		this.groupComboBoxHidden = groupComboBoxHidden;
	}

	public synchronized boolean isToolbarHidden() {
		return toolbarHidden;
	}

	public synchronized void setToolbarHidden(boolean toolbarHidden) {
		this.toolbarHidden = toolbarHidden;
	}

	public synchronized boolean isTrayIconHidden() {
		return trayIconHidden;
	}

	public synchronized void setTrayIconHidden(boolean trayIconHidden) {
		this.trayIconHidden = trayIconHidden;
	}

	public synchronized boolean areAllGroupsManuallyDeleted() {
		return allGroupsManuallyDeleted;
	}

	public synchronized void setAllGroupsManuallyDeleted(boolean allGroupsManuallyDeleted) {
		this.allGroupsManuallyDeleted = allGroupsManuallyDeleted;
	}

	public synchronized int getDividerLocation() {
		return dividerLocation;
	}

	public synchronized void setDividerLocation(int dividerLocation) {
		this.dividerLocation = dividerLocation;
	}

	public synchronized PreviewAreaPosition getPreviewAreaPosition() {
		return previewAreaPosition;
	}

	public synchronized void setPreviewAreaPosition(PreviewAreaPosition previewAreaPosition) {
		this.previewAreaPosition = previewAreaPosition;
	}

	public synchronized boolean isClipboardHistoryPreviewDialogLineWrap() {
		return clipboardHistoryPreviewDialogLineWrap;
	}

	public synchronized void setClipboardHistoryPreviewDialogLineWrap(
			boolean clipboardHistoryPreviewDialogLineWrap) {
		this.clipboardHistoryPreviewDialogLineWrap = clipboardHistoryPreviewDialogLineWrap;
	}

	public synchronized boolean isNamedItemsPreviewDialogLineWrap() {
		return namedItemsPreviewDialogLineWrap;
	}

	public synchronized void setNamedItemsPreviewDialogLineWrap(boolean namedItemsPreviewDialogLineWrap) {
		this.namedItemsPreviewDialogLineWrap = namedItemsPreviewDialogLineWrap;
	}

	public synchronized boolean isPreviewAreaLineWrap() {
		return previewAreaLineWrap;
	}

	public synchronized void setPreviewAreaLineWrap(boolean previewAreaLineWrap) {
		this.previewAreaLineWrap = previewAreaLineWrap;
	}

	public synchronized Size getClipboardHistoryPreviewDialogSize() {
		return clipboardHistoryPreviewDialogSize;
	}

	public synchronized void setClipboardHistoryPreviewDialogSize(Size clipboardHistoryPreviewDialogSize) {
		this.clipboardHistoryPreviewDialogSize = clipboardHistoryPreviewDialogSize;
	}

	public synchronized Size getNamedItemsPreviewDialogSize() {
		return namedItemsPreviewDialogSize;
	}

	public synchronized void setNamedItemsPreviewDialogSize(Size namedItemsPreviewDialogSize) {
		this.namedItemsPreviewDialogSize = namedItemsPreviewDialogSize;
	}

	public synchronized int getMaxHistorySize() {
		return maxHistorySize;
	}

	public synchronized void setMaxHistorySize(int maxHistorySize) {
		this.maxHistorySize = maxHistorySize;
	}

	public synchronized long getInstallationDate() {
		return installationDate;
	}

	public synchronized void setInstallationDate(long installationDate) {
		this.installationDate = installationDate;
	}

	public synchronized boolean isDonateReminderOff() {
		return donateReminderOff;
	}

	public synchronized void setDonateReminderOff(boolean donateReminderOff) {
		this.donateReminderOff = donateReminderOff;
	}
}
