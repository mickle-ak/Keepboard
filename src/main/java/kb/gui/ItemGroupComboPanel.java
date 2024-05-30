package kb.gui;

import kb.*;
import kb.utils.Observer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;


public class ItemGroupComboPanel extends JPanel {
private static final long serialVersionUID = 1L;
	
	private JComboBox cmbItemGroup;
	private Component itemGroupActionsButton;
	private ItemGroupHolder itemGroupHolder;
	private ItemGroupComboPanelActionsMenuHolder actionsMenuHolder;
	private ItemGroupPanel itemGroupPanel;
	
	public ItemGroupComboPanel(ItemGroupPanel itemGroupPanel) {
		this.itemGroupPanel = itemGroupPanel;
		this.itemGroupHolder = ItemGroupHolder.getInstance();
		
		createComponents();
		addComponents();
		initComponents();
		addActions();
	}

	private void addActions() {
		addCmbItemGroupAction();
		addItemGroupActionsMenuActions();
		addItemGroupsObservers();
		addItemGroupHolderObserver();
	}

	private void addItemGroupHolderObserver() {
		Observer<List<NamedItemGroup>> changeObserver = createChangeObserver();
		itemGroupHolder.getChangeObservable().addObserver(changeObserver);
		MainFrame.getInstance().registerObserver(changeObserver, itemGroupHolder.getChangeObservable());
		
		Observer<NamedItemGroup> groupCreationObserver = createGroupCreationObserver();
		itemGroupHolder.getGroupCreationObservable().addObserver(groupCreationObserver);
		MainFrame.getInstance().registerObserver(groupCreationObserver, itemGroupHolder.getGroupCreationObservable());
	}

	private Observer<NamedItemGroup> createGroupCreationObserver() {
		return new Observer<NamedItemGroup>() {
			@Override
			public void update(NamedItemGroup data) {
				addItemGroupObserver(data.getItemGroup());
				setCmbItemGroupsData();
			}
		};
	}

	private Observer<List<NamedItemGroup>> createChangeObserver() {
		return new Observer<List<NamedItemGroup>>() {
			@Override
			public void update(List<NamedItemGroup> data) {
				setCmbItemGroupsData();
			}
		};
	}

	private void addItemGroupsObservers() {
		for (NamedItemGroup namedItemGroup : itemGroupHolder.getItemGroups()) {
			addItemGroupObserver(namedItemGroup.getItemGroup());
		}
	}
	
	private void addItemGroupObserver(ItemGroup itemGroup) {
		Observer<List<ClipboardItem>> observer = createItemGroupItemsObserver(itemGroup);
		itemGroup.getObservable().addObserver(observer);
		MainFrame.getInstance().registerObserver(observer, itemGroup.getObservable());
	}

	private Observer<List<ClipboardItem>> createItemGroupItemsObserver(final ItemGroup itemGroup) {
		return new Observer<List<ClipboardItem>>() {
			@Override
			public void update(List<ClipboardItem> data) {
				if (getSelectedNamedGroup().getItemGroup() == itemGroup) {
					itemGroupPanel.itemGroupSelectionChanged();
				}
			}
		};
	}

	private void addItemGroupActionsMenuActions() {
		actionsMenuHolder.getMiNew().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createNewItemGroup();
			}
		});
		
		actionsMenuHolder.getMiRename().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				renameItemGroup();
			}
		});
		
		actionsMenuHolder.getMiDelete().addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteItemGroup();
			}
		});
	}
	
	private void deleteItemGroup() {
		NamedItemGroup selectedGroup = getSelectedNamedGroup();
		
		if (selectedGroup != null) {
			if (MainFrame.getInstance().displayConfirmationMessage("Delete group '" + selectedGroup.getName() + "'?")) {
				itemGroupHolder.deleteItemGroup(selectedGroup.getName());
			}
		}
	}

	private void renameItemGroup() {
		NamedItemGroup selectedGroup = getSelectedNamedGroup();
		
		if (selectedGroup != null) {
			String itemGroupNewName = ItemGroupModificationDialog.showDialog(selectedGroup.getName());
			if (itemGroupNewName != null && !itemGroupNewName.isEmpty()) {
				renameItemGroup(selectedGroup.getName(), itemGroupNewName);
			}
		}
	}

	private void renameItemGroup(String oldName, String newName) {
		try {
			itemGroupHolder.renameItemGroup(oldName, newName);
			selectGroup(newName);
		} catch (UserException e) {
			MainFrame.getInstance().displayErrorMessage(e.getMessage());
		}
	}

	public void selectGroup(String name) {
		int groupIndex = getGroupIndex(name);
		if (groupIndex >= 0) {
			cmbItemGroup.setSelectedIndex(groupIndex);
		}
	}

	private int getGroupIndex(String name) {
		for (int i = 0, n = cmbItemGroup.getItemCount(); i < n; i++) {
			if (convert(cmbItemGroup.getItemAt(i)).getName().equals(name)) {
				return i;
			}
		}
		
		return -1;
	}
	
	private NamedItemGroup convert(Object object) {
		return (NamedItemGroup) object;
	}

	private void createNewItemGroup() {
		String newItemGroupName = ItemGroupModificationDialog.showDialog(null);
		if (newItemGroupName != null && !newItemGroupName.isEmpty()) {
			addNewItemGroup(newItemGroupName);
		}
	}

	private void addNewItemGroup(String name) {
		try {
			itemGroupHolder.addNewItemGroup(name);
			selectGroup(name);
		} catch (UserException e) {
			MainFrame.getInstance().displayErrorMessage(e.getMessage());
		}
	}

	private void addCmbItemGroupAction() {
		cmbItemGroup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				itemGroupPanel.itemGroupSelectionChanged();
				actionsMenuHolder.setGroupSelectionDependentMenuItemsEnabled(cmbItemGroup.getItemCount() > 0);
			}
		});
	}

	private void initComponents() {
		setCmbItemGroupsData();
		actionsMenuHolder.setGroupSelectionDependentMenuItemsEnabled(cmbItemGroup.getItemCount() > 0);
	}

	private void setCmbItemGroupsData() {
		NamedItemGroup seletedGroup = getSelectedNamedGroup();
		cmbItemGroup.removeAllItems();
		
		for (NamedItemGroup namedItemGroup : itemGroupHolder.getItemGroups()) {
			cmbItemGroup.addItem(namedItemGroup);
		}
		
		if (seletedGroup != null) {
			selectGroup(seletedGroup.getName());
		}
	}

	private void createComponents() {
		createCmbItemGroups();
		createItemGroupActionsButton();
	}

	private void createItemGroupActionsButton() {
		itemGroupActionsButton = new ActionsButtonBuilder((actionsMenuHolder = new ItemGroupComboPanelActionsMenuHolder()).getPopupMenu())
			.setMnemonic(KeyEvent.VK_O)
			.setText("Actions")
			.setSize(new Dimension(90, 25))
			.build();
	}

	private void createCmbItemGroups() {
		cmbItemGroup = new JComboBox();
		GuiUtils.setSize(cmbItemGroup, new Dimension(50, 25));
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addItemsGruopLabel();
		addCmbItemGroups();
		addItemGroupActionsButton();
	}

	private void addItemsGruopLabel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.insets = new Insets(0, 3, 0, 0);
		add(createCmbGroupLabel(), c);
	}

	private JLabel createCmbGroupLabel() {
		JLabel result = new JLabel("Group: ");
		result.setLabelFor(cmbItemGroup);
		result.setDisplayedMnemonic(KeyEvent.VK_G);
		return result;
	}

	private void addCmbItemGroups() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(cmbItemGroup, c);
	}

	private void addItemGroupActionsButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 2; c.gridy = 0;
		c.insets = new Insets(0, 5, 0, 0);
		add(itemGroupActionsButton, c);
	}

	public NamedItemGroup getSelectedNamedGroup() {
		return (NamedItemGroup) cmbItemGroup.getSelectedItem();
	}
}
