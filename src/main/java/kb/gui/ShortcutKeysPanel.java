package kb.gui;

import kb.utils.Observable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ShortcutKeysPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTable table = new JTable();
	private Model model = new Model();
	
	private List<KeyItem> data;
	
	private Key lastPressedKey;
	private boolean lastPressedKeyForbidden;
	
	private Observable<Map<ShortcutKeyItem, Key>> observable = new Observable<Map<ShortcutKeyItem,Key>>();
	
	public ShortcutKeysPanel() {
		createComponents();
		addComponents();
		addActions();
	}
	
	public void init() {
		lastPressedKey = null;
		lastPressedKeyForbidden = false;
		model.setData(data = createDataList());
		table.requestFocusInWindow();
	}
	
	private List<KeyItem> createDataList() {
		List<KeyItem> result = new ArrayList<KeyItem>();
		for (ShortcutKeyItem shortcutKeyItem : ShortcutKeyItem.values()) {
			result.add(new KeyItem(shortcutKeyItem));
		}
		return result;
	}

	public Observable<Map<ShortcutKeyItem, Key>> getObservable() {
		return observable;
	}
	
	public Map<ShortcutKeyItem, Key> getData() {
		return createKeysMap();
	}
	
	private void createComponents() {
		table.setModel(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	private void addComponents() {
		this.setLayout(new GridBagLayout());
		addDescriptionLabel();
		addTable();
	}

	private void addTable() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 0, 0, 0);
		add(new JScrollPane(table), c);
	}

	private void addDescriptionLabel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(new JLabel("Type new shortcut key (Backspace or Delete to remove shortcut):"), c);
	}

	private void addActions() {
		addTableKeyListener();
	}

	private void addTableKeyListener() {
		table.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				lastPressedKeyForbidden = isForbidden(e);
				
				if (!lastPressedKeyForbidden) {
					lastPressedKey = isShortcutKeyRemovalKey(e) ? null : new Key(e.getModifiers(), e.getKeyCode());
				}
				
				if (isFunctionKey(e)) {
					tableKeyTyped();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				tableKeyTyped();
			}
		});
	}
	
	private boolean isFunctionKey(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_F1: case KeyEvent.VK_F2: case KeyEvent.VK_F3: case KeyEvent.VK_F4: case KeyEvent.VK_F5: 
		case KeyEvent.VK_F6: case KeyEvent.VK_F7: case KeyEvent.VK_F8: case KeyEvent.VK_F9: case KeyEvent.VK_F10: 
		case KeyEvent.VK_F11: case KeyEvent.VK_F12: case KeyEvent.VK_F13: case KeyEvent.VK_F14: case KeyEvent.VK_F15: 
		case KeyEvent.VK_F16: case KeyEvent.VK_F17: case KeyEvent.VK_F18: case KeyEvent.VK_F19: case KeyEvent.VK_F20: 
		case KeyEvent.VK_F21: case KeyEvent.VK_F22: case KeyEvent.VK_F23: case KeyEvent.VK_F24:
			return true;
		default:
			return false;
		}
	}
	
	private boolean isShortcutKeyRemovalKey(KeyEvent e) {
		return e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE;
	}
	
	private void tableKeyTyped() {
		if (lastPressedKeyForbidden) {
			return;
		}
		
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			KeyItem selectedItem = model.getItemAt(selectedRow);
			selectedItem.key = lastPressedKey;
			model.fireTableCellUpdated(selectedRow, Model.KEY);
			observable.updateObservers(createKeysMap());
		}
	}


	private Map<ShortcutKeyItem, Key> createKeysMap() {
		Map<ShortcutKeyItem, Key> result = new HashMap<ShortcutKeyItem, Key>();
		for (KeyItem keyItem : data) {
			result.put(keyItem.shortcutKeyItem, keyItem.key);
		}
		return result;
	}

	private boolean isForbidden(KeyEvent e) {
		return isAlreadyChosen(e) || isReserved(e);
	}
	
	private boolean isAlreadyChosen(KeyEvent e) {
		for (KeyItem keyItem : data) {
			if (keyItem.key != null && keyItem.key.getKeyCode() == e.getKeyCode()
					&& keyItem.key.getModifiers() == e.getModifiers()) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isReserved(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_TAB:
		case KeyEvent.VK_ENTER:
			return true;
		case KeyEvent.VK_C:
		case KeyEvent.VK_V:
		case KeyEvent.VK_X:
		case KeyEvent.VK_A:
			return e.isControlDown() && !e.isAltDown() && !e.isAltGraphDown() && !e.isShiftDown();
		default:
			return false;	
		}
	}

	private static class KeyItem {
		ShortcutKeyItem shortcutKeyItem;
		Key key;
		
		KeyItem(ShortcutKeyItem shortcutKeyItem) {
			this.shortcutKeyItem = shortcutKeyItem;
			this.key = shortcutKeyItem.getShortcutKey();
		}
	}

	private static class Model extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		private static final int DESCRIPTION = 0, KEY = 1, CATEGORY = 2;
		
		private final String[] columnNames = {"Action", "Shortcut key", "Category"};
		
		private List<KeyItem> data;
		
		public Model() {
			// Empty constructor
		}
		
		public void setData(List<KeyItem> data) {
			this.data = (data == null ? new ArrayList<KeyItem>(0) : data);
			fireTableDataChanged();
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public Object getValueAt(int row, int column) {
			KeyItem item = data.get(row);
			switch (column) {
			case DESCRIPTION:
				return item.shortcutKeyItem.getDescription();
			case KEY:
				return item.key == null ? null : item.key.toText();
			case CATEGORY:
				return item.shortcutKeyItem.getCategory().getDescription();
			default:
				throw new IllegalArgumentException();	
			}
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return String.class;
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
		
		public KeyItem getItemAt(int row) {
			return data.get(row);
		}
	}
}
