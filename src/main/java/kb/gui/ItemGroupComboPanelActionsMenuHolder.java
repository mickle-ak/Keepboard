package kb.gui;

import javax.swing.*;

public class ItemGroupComboPanelActionsMenuHolder {
	
	private JMenuItem miNew = new JMenuItem("New group...");
	private JMenuItem miRename = new JMenuItem("Rename selected group...");
	private JMenuItem miDelete = new JMenuItem("Delete selected group");
	
	private JPopupMenu popupMenu = new JPopupMenu();
	
	public ItemGroupComboPanelActionsMenuHolder() {
		initPopupMenu();
	}
	
	private void initPopupMenu() {
		popupMenu.add(miNew);
		popupMenu.add(miRename);
		popupMenu.add(miDelete);
	}

	public JMenuItem getMiNew() {
		return miNew;
	}

	public JMenuItem getMiRename() {
		return miRename;
	}

	public JMenuItem getMiDelete() {
		return miDelete;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}
	
	public void setGroupSelectionDependentMenuItemsEnabled(boolean enabled) {
		miRename.setEnabled(enabled);
		miDelete.setEnabled(enabled);
	}
}
