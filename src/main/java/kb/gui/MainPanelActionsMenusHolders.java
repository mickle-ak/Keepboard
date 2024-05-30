package kb.gui;

public class MainPanelActionsMenusHolders {

	private MainPanelActionsMenuHolder actionButtonMenus = new MainPanelActionsMenuHolder();
	private MainPanelActionsMenuHolder contextMenuMenus = new MainPanelActionsMenuHolder();

	public MainPanelActionsMenuHolder getActionButtonMenus() {
		return actionButtonMenus;
	}

	public MainPanelActionsMenuHolder getContextMenuMenus() {
		return contextMenuMenus;
	}
	
	public void setTableSelectionDependentItemsEnabled(boolean enabled) {
		actionButtonMenus.setTableSelectionDependentMenusEnabled(enabled);
		contextMenuMenus.setTableSelectionDependentMenusEnabled(enabled);
	}
}
