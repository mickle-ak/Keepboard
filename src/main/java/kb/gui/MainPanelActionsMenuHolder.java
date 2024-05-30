package kb.gui;

import javax.swing.*;

public class MainPanelActionsMenuHolder {
	
	private JMenuItem miCopy = new JMenuItem("Copy selected to group...");
	private JMenuItem miNew = new JMenuItem("New item...");
	private JMenuItem miEdit = new JMenuItem("Open selected item...");
	private JMenuItem miDelete = new JMenuItem("Delete selected items");
	
	private JPopupMenu popupMenu = new JPopupMenu();
	
	public MainPanelActionsMenuHolder() {
		initPopupMenu();
	}
	
	private void initPopupMenu() {
		popupMenu.add(miCopy);
		popupMenu.addSeparator();
		popupMenu.add(miNew);
		popupMenu.add(miEdit);
		popupMenu.add(miDelete);
	}

	public JMenuItem getMiCopy() {
		return miCopy;
	}
	
	public JMenuItem getMiNew() {
		return miNew;
	}

	public JMenuItem getMiEdit() {
		return miEdit;
	}

	public JMenuItem getMiDelete() {
		return miDelete;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}
	
	public void setTableSelectionDependentMenusEnabled(boolean enabled) {
		miCopy.setEnabled(enabled);
		miEdit.setEnabled(enabled);
		miDelete.setEnabled(enabled);
	}
}
