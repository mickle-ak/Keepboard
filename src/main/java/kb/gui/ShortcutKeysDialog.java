package kb.gui;

import kb.utils.Observer;
import kb.utils.Utils;

import javax.swing.*;
import java.util.Map;


public class ShortcutKeysDialog extends MyInputDialog<Map<ShortcutKeyItem, Key>> {
	private static final long serialVersionUID = 1L;
	
	private static ShortcutKeysDialog INSTANCE;

	private ShortcutKeysPanel panel;
	
	private ShortcutKeysDialog() {
		setTitle("Shortcut keys");
		createPanel();
		addActions();
		addDialogComponents();
		setSize(700, 400);
	}
	
	public static Map<ShortcutKeyItem, Key> showDialog() {
		createInstanceIfNotCreated();
		INSTANCE.clearValue();
		INSTANCE.init();
		INSTANCE.setVisible(true);
		return INSTANCE.getInputValue();
	}
	
	private void init() {
		panel.init();
		checkOkButtonEnabledState();
	}
	
	private static void createInstanceIfNotCreated() {
		if (INSTANCE == null || INSTANCE.disposed) {
			INSTANCE = new ShortcutKeysDialog();
		}
	}

	private void addActions() {
		panel.getObservable().addObserver(new Observer<Map<ShortcutKeyItem,Key>>() {
			@Override
			public void update(Map<ShortcutKeyItem, Key> data) {
				checkOkButtonEnabledState();
			}
		});
	}
	
	private void checkOkButtonEnabledState() {
		getOkButton().setEnabled(isAnythingChanged());
	}

	private boolean isAnythingChanged() {
		Map<ShortcutKeyItem, Key> keys = panel.getData();
		for (ShortcutKeyItem keyItem : ShortcutKeyItem.values()) {
			if (!Utils.areEqualsNullsIncluded(keys.get(keyItem), keyItem.getShortcutKey())) {
				return true;
			}
		}
		return false;
	}

	private void createPanel() {
		panel = new ShortcutKeysPanel();
	}

	@Override
	protected Map<ShortcutKeyItem, Key> getValue() {
		if (isAnythingChanged()) {
			return panel.getData();
		}
		return null;
	}

	@Override
	protected JPanel getValuePanel() {
		return panel;
	}
}
