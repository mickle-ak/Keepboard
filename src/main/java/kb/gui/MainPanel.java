package kb.gui;

import kb.*;
import kb.gui.prefs.MyPreferences;
import kb.gui.utils.*;
import kb.utils.Observer;
import kb.utils.Utils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;


class MainPanel extends MainFramePanel {
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_NAMED_ITEM_LENGTH = 20;
	private static final Icon IMAGE_CONTENT_TYPE_ICON = new ImageIcon(Utils.getImage("picture.png"));
	private static final Icon FILE_CONTENT_TYPE_ICON = new ImageIcon(Utils.getImage("file.png"));
	
	private MainFrame mainFrame;
	
	private JTable table;
	private ItemsModel model;
	private TablePanel<ItemsModel> tablePanel;
	private Component donateReminderPanel;
	
	private MainPanelActionsMenusHolders actionsMenuHolders;
	
	private ItemGroup clipboardHistory;
	
	public MainPanel(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		this.clipboardHistory = ItemGroupHolder.getInstance().getClipboardHistory();
		
		createComponents();
		addComponents();
		addActions();
		
		clipboardItemGroupChanged(clipboardHistory.getItems());
	}
	
	@Override
	public ClipboardContent getSelectedClipboardItemContent() {
		ClipboardItem result = getSelectedItem();
		if (result != null) {
			return result.getContent();
		}
		return null;
	}
	
	@Override
	public ClipboardItem getSelectedItem() {
		List<ClipboardItem> selectedItems = getSelectedItems();
		if (!selectedItems.isEmpty()) {
			return selectedItems.get(0);
		}
		return null;
	}
	
	private void addActions() {
		addClipboardHistoryObserver();
		addTableApprovalObserver();
		addTableDeleteObserver();
		addTableSelectionListener();
	}

	private void addTableDeleteObserver() {
		tablePanel.getDeleteObservable().addObserver(new Observer<int[]>() {
			@Override
			public void update(int[] data) {
				deleteSelectedItems();
			}
		});
	}

	private void addTableSelectionListener() {
		table.getSelectionModel().addListSelectionListener(crateTableSelectionListener());
	}
	
	private ListSelectionListener crateTableSelectionListener() {
		return new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				actionsMenuHolders.setTableSelectionDependentItemsEnabled(isAnyTableRowSelected());
				getSelectionObservable().updateObservers(getSelectedItem());
			}
		};
	}
	
	private boolean isAnyTableRowSelected() {
		return table.getSelectedRow() >= 0;
	}

	private void addTableApprovalObserver() {
		tablePanel.getApprovalObservable().addObserver(new Observer<TablePanel.ApprovalData>() {
			@Override
			public void update(TablePanel.ApprovalData data) {
				getApprovalObservable().updateObservers(
						new SelectedClipboardContent(getSelectedClipboardItemContent(), data.isSecondaryAction()));
			}
		});
	}

	private void addClipboardHistoryObserver() {
		Observer<List<ClipboardItem>> observer = createObserver();
		clipboardHistory.getObservable().addObserver(observer);
		MainFrame.getInstance().registerObserver(observer, clipboardHistory.getObservable());
	}

	private Observer<List<ClipboardItem>> createObserver() {
		return new Observer<List<ClipboardItem>>() {
			@Override
			public void update(List<ClipboardItem> data) {
				clipboardItemGroupChanged(data);
			}
		};
	}

	private void createComponents() {
		createTable();
	}
	
	private void createTable() {
		table = new JTable();
		table.setModel(model = new ItemsModel());
		table.setTableHeader(null);
	}
	
	private void addComponents() {
		this.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.add(donateReminderPanel = createDonateReminderPanel(), c);
		
		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		this.add(tablePanel = createTablePanel(), c);
	}

	private TablePanel<ItemsModel> createTablePanel() {
		return new TablePanelBuilder<ItemsModel>(table)
			.setFilterFieldLabelText("Item:")
			.setFilterFieldLabelMnemonic(KeyEvent.VK_E)
			.setPopupMenusHolder(createTablePanelPopupMenuHolder())
			.setActionsButtonMnemonic(KeyEvent.VK_A)
			.addTableCellDecorator(createClipboardContentTypeDecorator())
			.build();
	}

	private Component createDonateReminderPanel() {
		final JPanel result = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.insets = new Insets(8, 0, 8, 0);
		JButton button = new JButton("Help Keepboard get better!");
		button.setToolTipText("Please consider making a donation to help Keepboard evolve. Thank you!");
		GuiUtils.setSize(button, new Dimension(250, 30));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				turnOffDonateReminder();
				new AboutDialog(AboutPanel.FocusedTab.DONATE);
			}
		});
		result.add(button, c);

		c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		final JButton closeButton = new JButton();
		closeButton.setIcon(new XIcon(closeButton.getModel()));
		GuiUtils.setSize(closeButton, new Dimension(30, 30));
		closeButton.setToolTipText("Hide and don't show again.");
		closeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				turnOffDonateReminder();
			}
		});
		result.add(closeButton, c);

		if (MyPreferences.INSTANCE.isDonateReminderOff()
				|| System.currentTimeMillis() - MyPreferences.INSTANCE.getInstallationDate() < 90L * 24 * 60 * 60 * 1000) {
			result.setVisible(false);
		}
		return result;
	}

	private void turnOffDonateReminder() {
		MyPreferences.INSTANCE.setDonateReminderOff(true);
		new PreferencesPersistor().storeToDisk();
		donateReminderPanel.setVisible(false);
	}

	private TableCellDecorator createClipboardContentTypeDecorator() {
		return new TableCellDecorator() {
			@Override
			public void decorate(Component component, JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				
				ClipboardItem clipboardItem = model.getItemAt(table.convertRowIndexToModel(row));
				JLabel label = (JLabel) component;
				if (clipboardItem instanceof ImageClipboardItem) {
					label.setIcon(IMAGE_CONTENT_TYPE_ICON);
				} else if (((TextClipboardItem) clipboardItem).getNativeTransferable() != null) {
					label.setIcon(FILE_CONTENT_TYPE_ICON);
				} else {
					label.setIcon(null);
				}
			}
		};
	}

	private TablePanelPopupMenusHolder createTablePanelPopupMenuHolder() {
		actionsMenuHolders = new MainPanelActionsMenusHolders();
		addActionsMenusActions(actionsMenuHolders.getActionButtonMenus());
		addActionsMenusActions(actionsMenuHolders.getContextMenuMenus());
		actionsMenuHolders.setTableSelectionDependentItemsEnabled(false);
		
		return createTablePanelPopupMenuHolder(actionsMenuHolders.getActionButtonMenus().getPopupMenu(),
				actionsMenuHolders.getContextMenuMenus().getPopupMenu());
	}
	
	private void addActionsMenusActions(MainPanelActionsMenuHolder actionsMenuHolder) {
		actionsMenuHolder.getMiCopy().addActionListener(createCopyToItemGroupActionListener());
		actionsMenuHolder.getMiNew().addActionListener(createMiNewActionListener());
		actionsMenuHolder.getMiEdit().addActionListener(createEditActionListener());
		actionsMenuHolder.getMiDelete().addActionListener(createDeleteActionListener());
	}

	private ActionListener createMiNewActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				createNewItem();
			}
		};
	}

	private TablePanelPopupMenusHolder createTablePanelPopupMenuHolder(final JPopupMenu actionsMenu, final JPopupMenu contextMenu) {
		return new TablePanelPopupMenusHolder() {
			@Override
			public JPopupMenu getContextMenu() {
				return contextMenu;
			}
			
			@Override
			public JPopupMenu getActionsMenu() {
				return actionsMenu;
			}
		};
	}
	
	private ActionListener createEditActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				openSelectedItem();
			}
		};
	}

	@Override
	public void openSelectedItem() {
		ClipboardItem item = getSelectedItem();
		if (item != null) {
			int[] selectedRows = table.getSelectedRows();
			editItem(item);
			selectTableRows(selectedRows);
			scrollToRow(table, selectedRows[0]);
		}
	}
	
	private void selectTableRows(int[] selectedRows) {
		table.clearSelection();
		for (int row : selectedRows) {
			table.getSelectionModel().addSelectionInterval(row, row);
		}
	}
	
	private void editItem(ClipboardItem item) {
		if (item instanceof TextClipboardItem) {
			editTextItem((TextClipboardItem) item);
		} else {
			editImageItem((ImageClipboardItem) item);
		}
	}

	private void editImageItem(ImageClipboardItem item) {
		ImageItemDialog.showDialog(item, false);
	}

	private void editTextItem(TextClipboardItem item) {
		TextClipboardItem newItem = ClipboardHistoryItemEditDialog.showDialog(item);
		if (newItem != null) {
			clipboardHistory.replaceItem(item, newItem);
		}
	}

	private ActionListener createDeleteActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				deleteSelectedItems();
			}
		};
	}

	@Override
	public void deleteSelectedItems() {
		List<ClipboardItem> selectedItems = getSelectedItems();
		if (selectedItems != null && !selectedItems.isEmpty()) {
			deleteItemsIfConfirmed(selectedItems);
		}
	}

	private void deleteItemsIfConfirmed(List<ClipboardItem> items) {
		if (mainFrame.displayConfirmationMessage("Delete selected items?")) {
			deleteItems(items);
		}
	}

	private void deleteItems(List<ClipboardItem> items) {
		for (ClipboardItem clipboardItem : items) {
			clipboardHistory.deleteItem(clipboardItem);
		}
	}

	private ActionListener createCopyToItemGroupActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				copySelectedItemsToGroup();
			}
		};
	}

	@Override
	public void copySelectedItemsToGroup() {
		List<ClipboardItem> selectedItems = getSelectedItems();
		if (selectedItems != null && !selectedItems.isEmpty()) {
			copyItemsToGroup(selectedItems);
		}
	}

	private void copyItemsToGroup(List<ClipboardItem> selectedItems) {
		String groupName = ItemGroupChooser.showDialog("Copy items to group");
		if (groupName != null && !groupName.isEmpty()) {
			copyItemsToGroup(selectedItems, groupName);
		}
	}

	private void copyItemsToGroup(List<ClipboardItem> selectedItems, String groupName) {
		ItemGroup itemGroup = ItemGroupHolder.getInstance().getItemGroup(groupName);
		Collections.reverse(selectedItems);
		for (ClipboardItem clipboardItem : selectedItems) {
			itemGroup.addOrMoveItemToTop(createNamedItem(clipboardItem));
		}
	}

	private ClipboardItem createNamedItem(ClipboardItem clipboardItem) {
		ClipboardItem result = clipboardItem.createClonedInstance();
		result.setName(getClipboardItemName(clipboardItem));
		return result;
	}

	private String getClipboardItemName(ClipboardItem clipboardItem) {
		String name;
		
		if (clipboardItem instanceof TextClipboardItem) {
			name = ((TextClipboardItem) clipboardItem).getContent().getText().replaceAll("\\s+", " ").trim();
		} else {
			name = "<image>";
		}
		
		return name.length() > DEFAULT_NAMED_ITEM_LENGTH ? name.substring(0, DEFAULT_NAMED_ITEM_LENGTH).trim() : name;
	}

	private List<ClipboardItem> getSelectedItems() {
		List<ClipboardItem> result = new ArrayList<ClipboardItem>();
		int[] selectedRows = table.getSelectedRows();
		for (int rowIndex : selectedRows) {
			result.add(model.getItemAt(table.convertRowIndexToModel(rowIndex)));
		}
		return result;
	}

	private void clipboardItemGroupChanged(List<ClipboardItem> items) {
		model.setData(items);
		selectDefaultPasteRow();
	}
	
	public void selectDefaultPasteRow() {
		doSelectDefaultPasteRow();
		scrollToTop();
	}

	private void doSelectDefaultPasteRow() {
		if (table.getRowCount() > 0) {
			if (table.getRowCount() == 1) {
				table.getSelectionModel().setSelectionInterval(0, 0);
			} else {
				table.getSelectionModel().setSelectionInterval(1, 1);
			}
		}
	}
	
	private void scrollToTop() {
		if (table.getRowCount() > 0) {
			table.scrollRectToVisible(table.getCellRect(0, 0, false));
		}
	}

	public void clearFilter() {
		tablePanel.clearFilter();
		selectDefaultPasteRow();
	}
	
	@Override
	public void panelDisplayed() {
		tablePanel.focusFilterField();
	}

	@Override
	public void moveSelectedToTop() {
		ClipboardItem selectedItem = getSelectedItem();
		if (selectedItem != null) {
			clipboardHistory.moveExistingItemToTop(selectedItem);
		}
	}

	@Override
	public void selectNext() {
		moveSelection(table, 1);
	}

	@Override
	public void selectPrevious() {
		moveSelection(table, -1);
	}
	
	public void selectDefaultRowIfSelectionIsCleared() {
		if (table.getSelectedRow() < 0) {
			selectDefaultPasteRow();
		}
	}

	@Override
	public void moveItemUp() {
		ClipboardItem clipboardItem = getSelectedItem();
		
		if (clipboardItem == null) {
			return;
		}
		
		clipboardHistory.moveUp(clipboardItem);
		selectClipboardItem(clipboardItem);
	}

	private void selectClipboardItem(ClipboardItem clipboardItem) {
		int row = table.convertRowIndexToView(getModelRow(clipboardItem));
		table.getSelectionModel().setSelectionInterval(row, row);
		scrollToRow(table, row);
	}

	private int getModelRow(ClipboardItem clipboardItem) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (model.getData().get(i) == clipboardItem) {
				return i;
			}
		}
		throw new NoSuchElementException();
	}

	@Override
	public void moveItemDown() {
		ClipboardItem clipboardItem = getSelectedItem();
		
		if (clipboardItem == null) {
			return;
		}
		
		clipboardHistory.moveDown(clipboardItem);
		selectClipboardItem(clipboardItem);
	}

	@Override
	public void createNewItem() {
		ClipboardItem newItem = ClipboardHistoryItemEditDialog.showDialog(null);
		if (newItem != null) {
			clipboardHistory.addOrMoveItemToTop(newItem);
		}
	}
}