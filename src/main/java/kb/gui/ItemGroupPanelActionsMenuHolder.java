package kb.gui;

import javax.swing.*;

public class ItemGroupPanelActionsMenuHolder {
	
	private JMenuItem miNew = new JMenuItem("New item...");
	private JMenuItem miEdit = new JMenuItem("Open selected item...");
	private JMenuItem miDelete = new JMenuItem("Delete selected items");
	private JMenuItem miMove = new JMenuItem("Move selected to group...");
	private JMenuItem miCopy = new JMenuItem("Copy selected to group...");
	
	private JPopupMenu popupMenu = new JPopupMenu();
	
	public ItemGroupPanelActionsMenuHolder() {
		initPopupMenu();
	}
	
	private void initPopupMenu() {
		popupMenu.add(miNew);
		popupMenu.add(miEdit);
		popupMenu.add(miDelete);
		popupMenu.addSeparator();
		popupMenu.add(miMove);
		popupMenu.add(miCopy);
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
	
	public JMenuItem getMiMove() {
		return miMove;
	}
	
	public JMenuItem getMiCopy() {
		return miCopy;
	}

	public JPopupMenu getPopupMenu() {
		return popupMenu;
	}
	
	public void setTableSelectionDependentMenuItemsEnabled(boolean enabled) {
		miEdit.setEnabled(enabled);
		miDelete.setEnabled(enabled);
		miMove.setEnabled(enabled);
		miCopy.setEnabled(enabled);
	}
}
