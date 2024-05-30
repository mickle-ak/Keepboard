package kb.gui.toolbar;

import kb.gui.GuiUtils;
import kb.gui.MainFrame;
import kb.utils.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ToolbarHandler {
	
	private static final ToolbarHandler INSTANCE = new ToolbarHandler();
	
	private ToolbarButton tbNewItem;
	private ToolbarButton tbOpenItem;
	private ToolbarButton tbDeleteItems;
	private ToolbarButton tbCopyItems;
	private ToolbarButton tbMoveUp;
	private ToolbarButton tbMoveDown;
	private ToolbarButton tbSearch;
	
	private ToolbarPanel toolbarPanel = new ToolbarPanel();
	
	private ToolbarHandler() {
		createToolbarButtons();
		addToolbarButtonsToPanel();
	}
	
	public static ToolbarHandler getInstance() {
		return INSTANCE;
	}

	private void addToolbarButtonsToPanel() {
		addToolbarPanelGroup(tbNewItem, tbOpenItem, tbDeleteItems);
		addToolbarPanelGroup(tbCopyItems, tbSearch);
		addToolbarPanelGroup(tbMoveUp, tbMoveDown);
		toolbarPanel.build();
	}

	private void addToolbarPanelGroup(ToolbarButton... buttons) {
		toolbarPanel.addToolbarButtonsGroup(Utils.toList(buttons));
	}

	private void createToolbarButtons() {
		tbNewItem = new ToolbarButton(new ImageIcon(Utils.getImage("new.png")), createTbNewItemAction(), "New item");
		tbOpenItem = new ToolbarButton(new ImageIcon(Utils.getImage("edit.png")), createTbOpenItemAction(), "Open selected item");
		tbDeleteItems = new ToolbarButton(new ImageIcon(Utils.getImage("delete.png")), createTbDeleteItemsAction(), "Delete selected items");
		tbCopyItems = new ToolbarButton(new ImageIcon(Utils.getImage("copy.png")), createTbCopyItemsAction(), "Copy selected items to group");
		tbMoveUp = new ToolbarButton(new ImageIcon(Utils.getImage("up.png")), createTbMoveUpAction(), "Move selected item up");
		tbMoveDown = new ToolbarButton(new ImageIcon(Utils.getImage("down.png")), createTbMoveDownAction(), "Move selected item down");
		tbSearch = new ToolbarButton(new ImageIcon(Utils.getImage("find.png")), createTbSearchAction(), "Search");
		
		GuiUtils.setSize(tbMoveUp, new Dimension(20, 25));
		GuiUtils.setSize(tbMoveDown, new Dimension(20, 25));
	}
	
	private Action createTbNewItemAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				MainFrame.getInstance().createNewItem();
			}
		};
	}

	private Action createTbOpenItemAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				MainFrame.getInstance().openSelectedItem();
			}
		};
	}

	private Action createTbDeleteItemsAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				MainFrame.getInstance().deleteSelectedItems();
			}
		};
	}

	private Action createTbCopyItemsAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				MainFrame.getInstance().copySelectedItemsToGroup();
			}
		};
	}

	private Action createTbSearchAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				MainFrame.getInstance().showSearchPane();
			}
		};
	}

	private Action createTbMoveDownAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				MainFrame.getInstance().moveSelectedItemDown();
			}
		};
	}

	private Action createTbMoveUpAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				MainFrame.getInstance().moveSelectedItemUp();
			}
		};
	}
	
	public Component getToolbarPanel() {
		return toolbarPanel;
	}

	public ToolbarButton getTbNewItem() {
		return tbNewItem;
	}

	public ToolbarButton getTbOpenItem() {
		return tbOpenItem;
	}

	public ToolbarButton getTbDeleteItems() {
		return tbDeleteItems;
	}

	public ToolbarButton getTbCopyItems() {
		return tbCopyItems;
	}

	public ToolbarButton getTbMoveUp() {
		return tbMoveUp;
	}

	public ToolbarButton getTbMoveDown() {
		return tbMoveDown;
	}

	public ToolbarButton getTbSearch() {
		return tbSearch;
	}
}
