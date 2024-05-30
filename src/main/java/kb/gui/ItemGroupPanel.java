package kb.gui;

import kb.*;
import kb.gui.prefs.MyPreferences;
import kb.gui.utils.TablePanel;
import kb.gui.utils.TablePanelBuilder;
import kb.gui.utils.TablePanelPopupMenusHolder;
import kb.utils.Observer;

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


public class ItemGroupPanel extends MainFramePanel {
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	private NamedItemsModel namedItemsModel;
	private TablePanel<NamedItemsModel> tablePanel;
	private ItemGroupComboPanel itemGroupComboPanel;
	private ItemGroupPanelActionsMenuHolder actionsMenuHolder;
	
	public ItemGroupPanel() {
		createComponents();
		addComponents();
		initComponents();
		addActions();
	}
	
	public ItemGroupComboPanel getItemGroupComboPanel() {
		return itemGroupComboPanel;
	}
	
	private void addActions() {
		addTableApprovalObservable();
		addTableDeleteObserver();
		addTableSelectionListener();
		addItemGroupHolderListener();
	}
	
	@SuppressWarnings("unchecked")
	private void addItemGroupHolderListener() {
		ItemGroupHolder itemGroupHolder = ItemGroupHolder.getInstance();
		
		Observer<?> changeObserver = createItemGroupHolderObserver();
		itemGroupHolder.getChangeObservable().addObserver((Observer<List<NamedItemGroup>>) changeObserver);
		MainFrame.getInstance().registerObserver(changeObserver, itemGroupHolder.getChangeObservable());
		
		Observer<?> creationObserver = createItemGroupHolderObserver();
		itemGroupHolder.getGroupCreationObservable().addObserver((Observer<NamedItemGroup>) creationObserver);
		MainFrame.getInstance().registerObserver(creationObserver, itemGroupHolder.getGroupCreationObservable());
	}

	private Observer<?> createItemGroupHolderObserver() {
		return new Observer<Object>() {
			@Override
			public void update(Object data) {
				setMitCreateNewEnabledState();
			}
		};
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
				actionsMenuHolder.setTableSelectionDependentMenuItemsEnabled(isAnyTableRowSelected());
				getSelectionObservable().updateObservers(getSelectedItem());
			}
		};
	}
	
	private boolean isAnyTableRowSelected() {
		return table.getSelectedRow() >= 0;
	}

	private void addTableApprovalObservable() {
		tablePanel.getApprovalObservable().addObserver(createTableApprovalObserver());
	}
	
	@Override
	public ClipboardContent getSelectedClipboardItemContent() {
		ClipboardItem result = getSelectedItem();
		if (result != null) {
			return result.getContent();
		}
		return null;
	}

	private Observer<TablePanel.ApprovalData> createTableApprovalObserver() {
		return new Observer<TablePanel.ApprovalData>() {
			@Override
			public void update(TablePanel.ApprovalData data) {
				getApprovalObservable().updateObservers(
						new SelectedClipboardContent(getSelectedClipboardItemContent(), data.isSecondaryAction()));
			}
		};
	}

	private void selectFirstRow() {
		if (table.getRowCount() > 0) {
			selectTableRows(new int[] {0});
			scrollToTop();
		}
	}

	private void addItemsActions(ItemGroupPanelActionsMenuHolder menuHolder) {
		menuHolder.getMiNew().addActionListener(createMitNewActionListener());
		menuHolder.getMiEdit().addActionListener(createMitEditActionListener());
		menuHolder.getMiDelete().addActionListener(createMitDeleteActionListener());
		menuHolder.getMiCopy().addActionListener(crateMitCopyActionListener());
		menuHolder.getMiMove().addActionListener(createMitMoveActionListener());
	}

	private ActionListener createMitMoveActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				moveSelectedItemsToItemGroup();
			}
		};
	}

	private void moveSelectedItemsToItemGroup() {
		List<ClipboardItem> selectedItems = getSelectedItems();
		if (!selectedItems.isEmpty()) {
			moveItemsToGroup(getSelectedGroup(), selectedItems);
		}
	}

	private void moveItemsToGroup(ItemGroup fromGroup, List<ClipboardItem> selectedItems) {
		if (copyItemsToGroup(selectedItems, true)) {
			deleteItems(fromGroup, selectedItems);
		}
	}

	private ActionListener crateMitCopyActionListener() {
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
		if (!selectedItems.isEmpty()) {
			copyItemsToGroup(selectedItems, false);
		}
	}

	private boolean copyItemsToGroup(List<ClipboardItem> selectedItems, boolean move) {
		String groupName = ItemGroupChooser.showDialog(move ? "Move items to group" : "Copy items to group");
		if (groupName != null && !groupName.isEmpty()) {
			copyItemsToGroup(selectedItems, groupName);
			return !groupName.equals(itemGroupComboPanel.getSelectedNamedGroup().getName());
		}
		return false;
	}

	private void copyItemsToGroup(List<ClipboardItem> selectedItems, String groupName) {
		ItemGroup itemGroup = ItemGroupHolder.getInstance().getItemGroup(groupName);
		Collections.reverse(selectedItems);
		for (ClipboardItem clipboardItem : selectedItems) {
			itemGroup.addOrMoveItemToTop(clipboardItem.createClonedInstance());
		}
	}

	private ActionListener createMitDeleteActionListener() {
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
		if (!selectedItems.isEmpty() && confirmDeletion()) {
			deleteItems(getSelectedGroup(), selectedItems);
		}
	}

	private boolean confirmDeletion() {
		return MainFrame.getInstance().displayConfirmationMessage("Delete selected items?");
	}

	private void deleteItems(ItemGroup fromGroup, List<ClipboardItem> selectedItems) {
		for (ClipboardItem IClipboardItem : selectedItems) {
			fromGroup.deleteItem(IClipboardItem);
		}
	}

	private ActionListener createMitEditActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
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
	
	private void scrollToTop() {
		if (table.getRowCount() > 0) {
			table.scrollRectToVisible(table.getCellRect(0, 0, false));
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
		Boolean imageNameEdited = ImageItemDialog.showDialog(item, true);
		if (imageNameEdited != null && imageNameEdited) {
			getSelectedGroup().itemsChanged();
		}
	}

	private void editTextItem(TextClipboardItem item) {
		ClipboardItem newItem = NamedItemEditDialog.showDialog(item);
		if (newItem != null) {
			replaceItem(item, newItem);
		}
	}

	private void replaceItem(ClipboardItem itemToBeReplaced, ClipboardItem newItem) {
		ItemGroup group = getSelectedGroup();
		if (group != null) {
			group.replaceItem(itemToBeReplaced, newItem);
		}
	}

	@Override
	public ClipboardItem getSelectedItem() {
		List<ClipboardItem> selectedItems = getSelectedItems();
		if (!selectedItems.isEmpty()) {
			return selectedItems.get(0);
		}
		return null;
	}

	private List<ClipboardItem> getSelectedItems() {
		List<ClipboardItem> result = new ArrayList<ClipboardItem>();
		int[] selectedRows = table.getSelectedRows();
		for (int rowIndex : selectedRows) {
			result.add(namedItemsModel.getItemAt(table.convertRowIndexToModel(rowIndex)));
		}
		return result;
	}

	private ActionListener createMitNewActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewItem();
			}
		};
	}

	@Override
	public void createNewItem() {
		ClipboardItem newItem = NamedItemEditDialog.showDialog(null);
		if (newItem != null) {
			addNewItem(newItem);
		}
	}

	private void addNewItem(ClipboardItem item) {
		ItemGroup group = getSelectedGroup();
		if (group != null) {
			group.addOrMoveItemToTop(item);
		}
	}

	public void selectGroup(String name) {
		itemGroupComboPanel.selectGroup(name);
		tablePanel.clearFilter();
	}

	private void initComponents() {
		itemGroupSelectionChanged();
		setMitCreateNewEnabledState();
	}

	private void setMitCreateNewEnabledState() {
		actionsMenuHolder.getMiNew().setEnabled(!ItemGroupHolder.getInstance().getItemGroups().isEmpty());
	}

	public void itemGroupSelectionChanged() {
		ItemGroup selectedItemGroup = getSelectedGroup();
		if (selectedItemGroup != null) {
			namedItemsModel.setData(getSelectedGroup().getItems());
			selectFirstRow();
		} else {
			namedItemsModel.setData(new ArrayList<ClipboardItem>());
		}
	}

	private ItemGroup getSelectedGroup() {
		NamedItemGroup selectedGroup = itemGroupComboPanel.getSelectedNamedGroup();
		
		if (selectedGroup != null) {
			return selectedGroup.getItemGroup();
		} else {
			return null;
		}
	}

	private void createComponents() {
		createTable();
		createItemGroupComboPanel();
	}

	private void createItemGroupComboPanel() {
		itemGroupComboPanel = new ItemGroupComboPanel(this);
	}

	private void createTable() {
		table = new JTable();
		table.setModel(namedItemsModel = new NamedItemsModel());
		table.getColumnModel().getColumn(0).setPreferredWidth(100);
		table.getColumnModel().getColumn(1).setPreferredWidth(200);
		table.getTableHeader().setReorderingAllowed(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		tablePanel = createTablePanel();
	}

	private TablePanel<NamedItemsModel> createTablePanel() {
		return new TablePanelBuilder<NamedItemsModel>(table)
			.setFilterFieldLabelText("Name:")
			.setFilterFieldLabelMnemonic(KeyEvent.VK_E)
			.setPopupMenusHolder(createTablePanelPopupMenusHolder())
			.setActionsButtonMnemonic(KeyEvent.VK_A)
			.build();
	}
	
	private TablePanelPopupMenusHolder createTablePanelPopupMenusHolder() {
		actionsMenuHolder = new ItemGroupPanelActionsMenuHolder();
		addItemsActions(actionsMenuHolder);
		actionsMenuHolder.setTableSelectionDependentMenuItemsEnabled(false);
		
		ItemGroupPanelActionsMenuHolder contenxtMenuActionsMenuHolder = new ItemGroupPanelActionsMenuHolder();
		addItemsActions(contenxtMenuActionsMenuHolder);
		
		return createTablePanelPopupMenusHolder(actionsMenuHolder.getPopupMenu(), contenxtMenuActionsMenuHolder.getPopupMenu());
	}

	private TablePanelPopupMenusHolder createTablePanelPopupMenusHolder(final JPopupMenu actionsMenu, final JPopupMenu contextMenu) {
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
	
	private void addComponents() {
		setLayout(new GridBagLayout());
		
		if (!MyPreferences.INSTANCE.isGroupComboBoxHidden()) {
			addItemsGroupPanel();
		}
		
		addItemsPanel();
	}

	private void addItemsGroupPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(3, 0, 10, 0);
		add(itemGroupComboPanel, c);
	}

	private void addItemsPanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(tablePanel, c);
	}
	
	@Override
	public void panelDisplayed() {
		tablePanel.focusFilterField();
	}
	
	public void clearFilter() {
		tablePanel.clearFilter();
		selectFirstRow();
	}

	@Override
	public void moveSelectedToTop() {
		ClipboardItem selectedItem = getSelectedItem();
		if (selectedItem != null) {
			getSelectedGroup().moveExistingItemToTop(selectedItem);
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
			selectFirstRow();
		}
	}

	public void setGroupComboBoxVisible(boolean visible) {
		if (visible) {
			addItemsGroupPanel();
		} else {
			this.remove(itemGroupComboPanel);
		}
		validate();
	}

	@Override
	public void moveItemUp() {
		ClipboardItem clipboardItem = getSelectedItem();
		
		if (clipboardItem == null) {
			return;
		}
		
		getSelectedGroup().moveUp(clipboardItem);
		selectClipboardItem(clipboardItem);
	}

	private void selectClipboardItem(ClipboardItem clipboardItem) {
		int row = table.convertRowIndexToView(getModelRow(clipboardItem));
		table.getSelectionModel().setSelectionInterval(row, row);
		scrollToRow(table, row);
	}

	private int getModelRow(ClipboardItem clipboardItem) {
		for (int i = 0; i < namedItemsModel.getRowCount(); i++) {
			if (namedItemsModel.getData().get(i) == clipboardItem) {
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
		
		getSelectedGroup().moveDown(clipboardItem);
		selectClipboardItem(clipboardItem);
	}
}
