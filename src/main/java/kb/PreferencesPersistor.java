package kb;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;
import kb.gui.Key;
import kb.gui.OkComponentPopupMenuHolder.PasteMode;
import kb.gui.PreviewAreaPosition;
import kb.gui.prefs.LookAndFeelPref;
import kb.gui.prefs.MyPreferences;
import kb.gui.prefs.Size;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreferencesPersistor {
	
	public static volatile boolean werePreferencesSaved = false;
	
	public void storeToDisk() {
		synchronized (MyPreferences.INSTANCE) {
			doStoreToDisk();
		}
	}
	
	private void doStoreToDisk() {
		new MyPersistor().storeToDisc(createXMLElements(), "prefs/pref");
	}

	private List<XMLElement> createXMLElements() {
		return Arrays.asList(createXMLElement());
	}

	private XMLElement createXMLElement() {
		MyPreferences preferences = MyPreferences.INSTANCE;
		
		XMLElement result = new XMLElement();
		result.autosaveImages = preferences.isAutosaveImages();
		result.autopasteKeyModifiers = preferences.getAutopasteKey().getModifiers();
		result.autopasteKeyCode = preferences.getAutopasteKey().getKeyCode();
		result.focusActiveWindowKeyModifiers = preferences.getFocusActiveWindowKey().getModifiers();
		result.focusActiveWindowKeyCode = preferences.getFocusActiveWindowKey().getKeyCode();
		result.autostart = preferences.isAutostart();
		result.lookAndFeel = preferences.getLookAndFeelPref().name();
		result.mainFrameWidth = preferences.getMainFrameSize().getWidth();
		result.mainFrameHeight = preferences.getMainFrameSize().getHeight();
		result.paused = preferences.isPaused();
		result.itemPreviewHidden = preferences.isItemPreviewHidden();
		result.pasteMode = preferences.getPasteMode().name();
		result.showNameSearchColumn = preferences.isShowNameSearchColumn();
		result.showGroupSearchColumn = preferences.isShowGroupSearchColumn();
		result.tabsHidden = preferences.areTabsHidden();
		result.buttonsHidden = preferences.areButtonsHidden();
		result.groupComboBoxHidden = preferences.isGroupComboBoxHidden();
		result.toolbarHidden = preferences.isToolbarHidden();
		result.trayIconHidden = preferences.isTrayIconHidden();
		result.allGroupsManuallyDeleted = preferences.areAllGroupsManuallyDeleted();
		result.dividerLocation = preferences.getDividerLocation();
		result.previewAreaPosition = preferences.getPreviewAreaPosition().name();
		result.clipboardHistoryPreviewDialogLineWrap = preferences.isClipboardHistoryPreviewDialogLineWrap();
		result.namedItemsPreviewDialogLineWrap = preferences.isNamedItemsPreviewDialogLineWrap();
		result.previewAreaLineWrap = preferences.isPreviewAreaLineWrap();
		result.clipboardHistoryPreviewDialogWidth = preferences.getClipboardHistoryPreviewDialogSize().getWidth();
		result.clipboardHistoryPreviewDialogHeight = preferences.getClipboardHistoryPreviewDialogSize().getHeight();
		result.namedItemsPreviewDialogWidth = preferences.getNamedItemsPreviewDialogSize().getWidth();
		result.namedItemsPreviewDialogHeight = preferences.getNamedItemsPreviewDialogSize().getHeight();
		result.maxHistorySize = preferences.getMaxHistorySize();
		result.installationDate = preferences.getInstallationDate();
		result.donateReminderOff = preferences.isDonateReminderOff();
		return result;
	}
	
	public void loadFromDisk() {
		synchronized (MyPreferences.INSTANCE) {
			doLoadFromDisk();
		}
	}

	private void doLoadFromDisk() {
		List<XMLElement> list = new MyPersistor().readFromDisk("prefs/pref");
		if (!list.isEmpty()) {
			extractIntoPreferences(list.get(0));
			werePreferencesSaved = true;
		}
	}

	private void extractIntoPreferences(XMLElement xmlElement) {
		MyPreferences preferences = MyPreferences.INSTANCE;
		
		preferences.setAutopasteKey(new Key(xmlElement.autopasteKeyModifiers, xmlElement.autopasteKeyCode));
		setFocusActiveWindowKey(preferences, xmlElement);
		preferences.setAutostart(xmlElement.autostart);
		preferences.setAutosaveImages(xmlElement.autosaveImages);
		setLookAndFeelPref(xmlElement, preferences);
		setMainFrameSize(preferences, xmlElement);
		preferences.setPaused(xmlElement.paused);
		preferences.setItemPreviewHidden(xmlElement.itemPreviewHidden);
		setPasteMode(xmlElement, preferences);
		preferences.setShowNameSearchColumn(xmlElement.showNameSearchColumn);
		preferences.setShowGroupSearchColumn(xmlElement.showGroupSearchColumn);
		preferences.setTabsHidden(xmlElement.tabsHidden);
		preferences.setButtonsHidden(xmlElement.buttonsHidden);
		preferences.setGroupComboBoxHidden(xmlElement.groupComboBoxHidden);
		preferences.setToolbarHidden(xmlElement.toolbarHidden);
		preferences.setTrayIconHidden(xmlElement.trayIconHidden);
		preferences.setAllGroupsManuallyDeleted(xmlElement.allGroupsManuallyDeleted);
		preferences.setDividerLocation(xmlElement.dividerLocation);
		setPreviewAreaPosition(preferences, xmlElement);
		preferences.setClipboardHistoryPreviewDialogLineWrap(xmlElement.clipboardHistoryPreviewDialogLineWrap);
		preferences.setNamedItemsPreviewDialogLineWrap(xmlElement.namedItemsPreviewDialogLineWrap);
		preferences.setPreviewAreaLineWrap(xmlElement.previewAreaLineWrap);
		setClipboardHistoryPreviewDialogSize(xmlElement, preferences);
		setNamedItemsPreviewDialogSize(xmlElement, preferences);
		setMaxHistorySize(xmlElement, preferences);
		preferences.setInstallationDate(xmlElement.installationDate);
		preferences.setDonateReminderOff(xmlElement.donateReminderOff);
	}

	private void setFocusActiveWindowKey(MyPreferences preferences, XMLElement xmlElement) {
		if (xmlElement.focusActiveWindowKeyCode > 0) {
			preferences.setFocusActiveWindowKey(new Key(xmlElement.focusActiveWindowKeyModifiers, xmlElement.focusActiveWindowKeyCode));
		}
	}

	private void setMaxHistorySize(XMLElement xmlElement, MyPreferences preferences) {
		if (xmlElement.maxHistorySize > 0 && xmlElement.maxHistorySize <= 10000) {
			preferences.setMaxHistorySize(xmlElement.maxHistorySize);
		}
	}

	private void setClipboardHistoryPreviewDialogSize(XMLElement xmlElement, MyPreferences preferences) {
		if (xmlElement.clipboardHistoryPreviewDialogWidth > 0 && xmlElement.clipboardHistoryPreviewDialogHeight > 0) {
			preferences.setClipboardHistoryPreviewDialogSize(new Size(xmlElement.clipboardHistoryPreviewDialogWidth, xmlElement.clipboardHistoryPreviewDialogHeight));
		}
	}

	private void setNamedItemsPreviewDialogSize(XMLElement xmlElement, MyPreferences preferences) {
		if (xmlElement.namedItemsPreviewDialogWidth > 0 && xmlElement.namedItemsPreviewDialogHeight > 0) {
			preferences.setNamedItemsPreviewDialogSize(new Size(xmlElement.namedItemsPreviewDialogWidth, xmlElement.namedItemsPreviewDialogHeight));
		}
	}

	private void setPreviewAreaPosition(MyPreferences preferences, XMLElement xmlElement) {
		PreviewAreaPosition previewAreaPosition = getPreviewAreaPosition(xmlElement);
		if (previewAreaPosition != null) {
			preferences.setPreviewAreaPosition(previewAreaPosition);
		}
	}

	private PreviewAreaPosition getPreviewAreaPosition(XMLElement xmlElement) {
		try {
			return PreviewAreaPosition.valueOf(xmlElement.previewAreaPosition);
		} catch (Throwable e) {
			return null;
		}
	}

	private void setPasteMode(XMLElement xmlElement, MyPreferences preferences) {
		if (xmlElement.pasteMode != null) {
			try {
				preferences.setPasteMode(PasteMode.valueOf(xmlElement.pasteMode));
			} catch (Throwable e) {
				// Ignored
			}
		}
	}

	private void setMainFrameSize(MyPreferences preferences, XMLElement xmlElement) {
		if (xmlElement.mainFrameWidth > 0 && xmlElement.mainFrameHeight > 0) {
			preferences.setMainFrameSize(new Size(xmlElement.mainFrameWidth, xmlElement.mainFrameHeight));
		}
	}
	
	private void setLookAndFeelPref(XMLElement xmlElement, MyPreferences preferences) {
		LookAndFeelPref lookAndFeelPref = getLookAndFeelPref(xmlElement);
		if (lookAndFeelPref != null) {
			preferences.setLookAndFeelPref(lookAndFeelPref);
		}
	}

	private LookAndFeelPref getLookAndFeelPref(XMLElement xmlElement) {
		try {
			return LookAndFeelPref.valueOf(xmlElement.lookAndFeel);
		} catch (Throwable e) {
			return null;
		}
	}

	private static class MyPersistor extends ListPersistor<XMLElement> {

		@SuppressWarnings("unchecked")
		@Override
		protected List<XMLElement> convertToItems(List<?> xmlList) {
			return new ArrayList<XMLElement>((List<XMLElement>) xmlList); // Cast safety checked in superclass
		}

		@Override
		protected XStream createXStream() {
			XStream result = new XStream();
			result.addPermission(AnyTypePermission.ANY);
	    	result.alias("item", XMLElement.class);
	    	return result;
		}

		@Override
		protected List<?> createXmlList(List<XMLElement> elements) {
			return new ArrayList<XMLElement>(elements);
		}

		@Override
		protected Class<?> getXmlElementClass() {
			return XMLElement.class;
		}
	}
	
	private static class XMLElement {
		boolean autosaveImages;
		int autopasteKeyModifiers;
		int autopasteKeyCode;
		int focusActiveWindowKeyModifiers;
		int focusActiveWindowKeyCode;
		boolean autostart;
		String lookAndFeel;
		int mainFrameWidth;
		int mainFrameHeight;
		boolean paused;
		boolean itemPreviewHidden;
		String pasteMode;
		boolean showNameSearchColumn;
		boolean showGroupSearchColumn;
		boolean tabsHidden;
		boolean buttonsHidden;
		boolean groupComboBoxHidden;
		boolean toolbarHidden;
		boolean trayIconHidden;
		boolean allGroupsManuallyDeleted;
		int dividerLocation;
		String previewAreaPosition;
		boolean clipboardHistoryPreviewDialogLineWrap;
		boolean namedItemsPreviewDialogLineWrap;
		boolean previewAreaLineWrap;
		int clipboardHistoryPreviewDialogWidth;
		int clipboardHistoryPreviewDialogHeight;
		int namedItemsPreviewDialogWidth;
		int namedItemsPreviewDialogHeight;
		int maxHistorySize;
		long installationDate;
		boolean donateReminderOff;
		
		@SuppressWarnings("unused") // kept for backward compatibility
		boolean autopaste;
	}
}
