package kb;

import kb.clipboard.ClipboardAcessor;
import kb.gui.MainFrame;
import kb.gui.ShortcutKeyItem;
import kb.gui.prefs.MyPreferences;
import kb.utils.ObjectIO;

import javax.swing.*;

public final class ClipboardManager {
	private static ClipboardManager INSTANCE;
	
	private ItemGroup clipboardHistory;
	
	private ClipboardManager() {
		loadPrefs();
		clipboardHistory = ItemGroupHolder.getInstance().getClipboardHistory();
		ClipboardAcessor.init();
		MainFrame.initInstance();
		initCliboardListener();
		initShortcutKeys();
		createFavoritesGroupIfNeeded();
		deleteErrorFilesIfExist();
	}
	
	private void deleteErrorFilesIfExist() {
		try {
			ObjectIO.deleteErrorFilesIfExist();
		} catch (Exception ignored) {
			// Ignored
		}
	}

	private void createFavoritesGroupIfNeeded() {
		if (shouldCreateFavoritesGroup()) {
			try {
				ItemGroupHolder.getInstance().addNewItemGroup("Favorites");
			} catch (UserException ignored) {
				// Ignored
			}
		}
	}

	private boolean shouldCreateFavoritesGroup() {
		return ItemGroupHolder.getInstance().getItemGroups().isEmpty() && !MyPreferences.INSTANCE.areAllGroupsManuallyDeleted();
	}

	private void initShortcutKeys() {
		ShortcutKeyItem.reinitShortcutKeys();
	}

	private void loadPrefs() {
		ShortcutKeyItem.loadPersistedState();
	}

	public static void start() {
		if (INSTANCE == null) {
			INSTANCE = new ClipboardManager();
		}
	}
	
	public static ClipboardManager getInstance() {
		return INSTANCE;
	}
	
	private void initCliboardListener() {
		if (MyPreferences.INSTANCE.isPaused()) {
			MainFrame.getInstance().pauseClipboardChangeTracking();
		}
		ClipboardChangeListener.getInstance().startListening();
	}
	
	public void clipboardChanged(ClipboardContent clipboardContent) {
		try {
			clipboardHistory.addOrMoveItemToTop(createClipboardItem(clipboardContent));
		} catch (Throwable e) {
			// Ignored
		}
	}
	
	private ClipboardItem createClipboardItem(ClipboardContent clipboardContent) {
		if (clipboardContent instanceof TextClipboardContent) {
			return new TextClipboardItem(((TextClipboardContent) clipboardContent).getText());
		} else if (clipboardContent instanceof ImageClipboardContent) {
			return new ImageClipboardItem(new ImageIcon(((ImageClipboardContent) clipboardContent).getImage()));
		} else {
			FileListClipboardContent fileListClipboardContent = (FileListClipboardContent) clipboardContent;
			return new TextClipboardItem(fileListClipboardContent.getFiles(), fileListClipboardContent.getNativeTransferable());
		}
	}

	public void showMainFrame() {
		MainFrame.getInstance().showFrame();
	}
	
	public void copyToClipboard(String text) {
		ClipboardContent clipboardContent = new TextClipboardContent(text);
		ClipboardAcessor.writeToClipboard(clipboardContent);
		clipboardChanged(clipboardContent);
	}
}
