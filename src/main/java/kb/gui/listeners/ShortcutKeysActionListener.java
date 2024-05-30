package kb.gui.listeners;

import kb.gui.Key;
import kb.gui.ShortcutKeyItem;
import kb.gui.ShortcutKeysDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

public class ShortcutKeysActionListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent arg0) {
		Map<ShortcutKeyItem, Key> newKeys = ShortcutKeysDialog.showDialog();
		if (newKeys != null) {
			setNewKeys(newKeys);
			ShortcutKeyItem.reinitAndPersist();
		}
	}

	private void setNewKeys(Map<ShortcutKeyItem, Key> newKeys) {
		for (ShortcutKeyItem shortcutKeyItem : ShortcutKeyItem.values()) {
			shortcutKeyItem.setShortcutKey(newKeys.get(shortcutKeyItem));
		}
	}
}