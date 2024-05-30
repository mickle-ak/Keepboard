package kb.gui;

import kb.ItemGroupHolder;
import kb.NamedItemGroup;
import kb.UserException;
import kb.gui.utils.TablePanel;
import kb.gui.utils.TablePanelBuilder;
import kb.utils.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;


public class ItemGroupChooser extends MyInputDialog<String> {
	private static final long serialVersionUID = 1L;
	
	private static ItemGroupChooser INSTANCE;
	
	private TablePanel<Model> valuePanel;
	
	private JTable table;
	private Model model;
	private String value;
	
	private ItemGroupChooser() {
		valuePanel = createValuePanel();
		addActions();
		addDialogComponents();
		setSize(400, 300);
	}
	
	public static String showDialog(String title) {
		createInstanceIfNotCreated();
		INSTANCE.clearValue();
		INSTANCE.value = null;
		INSTANCE.initComponents(title);
		INSTANCE.setVisible(true);
		return INSTANCE.getInputValue();
	}

	private void initComponents(String title) {
		this.setTitle(title);
		model.setData(getItemGroupNames());
		disableOkButton();
		valuePanel.clearFilter();
		selectFirstTableRow();
		valuePanel.focusFilterField();
	}
	
	private static void createInstanceIfNotCreated() {
		if (INSTANCE == null || INSTANCE.disposed) {
			INSTANCE = new ItemGroupChooser();
		}
	}

	private void disableOkButton() {
		getOkButton().setEnabled(false);
	}

	private void selectFirstTableRow() {
		if (table.getRowCount() > 0) {
			table.getSelectionModel().setSelectionInterval(0, 0);
		}
	}

	private void addActions() {
		table.getSelectionModel().addListSelectionListener(crateTableSelectionListener());
	}
	
	private ListSelectionListener crateTableSelectionListener() {
		return new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				getOkButton().setEnabled(isGroupSelected());
			}
		};
	}

	private TablePanel<Model> createValuePanel() {
		TablePanel<Model> result = createTablePanel();
		result.getApprovalObservable().addObserver(createActionObserver());
		return result;
	}

	private TablePanel<Model> createTablePanel() {
		return new TablePanelBuilder<Model>(table = createTable())
			.setFilterFieldLabelText("Group: ")
			.setFilterFieldLabelMnemonic(KeyEvent.VK_G)
			.setCustomActionsComponent(createButtonNew())
			.build();
	}

	private Component createButtonNew() {
		JButton result = new JButton("New...");
		result.setMnemonic(KeyEvent.VK_N);
		result.addActionListener(createButtonNewActionListener());
		GuiUtils.setSize(result, new Dimension(80, 25));
		return result;
	}

	private ActionListener createButtonNewActionListener() {
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				buttonNewActionPerformed();
			}
		};
	}

	private void buttonNewActionPerformed() {
		String newGroup = ItemGroupModificationDialog.showDialog(null);
		if (newGroup != null) {
			addNewGroupAndConfirmInput(newGroup);
		}
	}

	private void addNewGroupAndConfirmInput(String newGroup) {
		try {
			ItemGroupHolder.getInstance().addNewItemGroup(newGroup);
			confirmInput(newGroup);
		} catch (UserException e) {
			MainFrame.getInstance().displayErrorMessage(e.getMessage());
		}
	}
	
	private void confirmInput(String chosenGroup) {
		value = chosenGroup;
		inputConfirmed();
	}

	private Observer<TablePanel.ApprovalData> createActionObserver() {
		return new Observer<TablePanel.ApprovalData>() {
			@Override
			public void update(TablePanel.ApprovalData data) {
				if (isGroupSelected()) {
					String chosenGroup = createValue();
					ItemGroupHolder.getInstance().moveGroupToTop(chosenGroup);
					confirmInput(chosenGroup);
				}
			}
		};
	}

	private boolean isGroupSelected() {
		return table.getSelectedRow() >= 0;
	}

	private JTable createTable() {
		JTable result = new JTable(model = new Model());
		result.setTableHeader(null);
		result.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		return result;
	}

	private List<String> getItemGroupNames() {
		List<String> result = new ArrayList<String>();
		for (NamedItemGroup namedItemGroup : ItemGroupHolder.getInstance().getItemGroups()) {
			result.add(namedItemGroup.getName());
		}
		return result;
	}

	@Override
	protected String getValue() {
		if (value == null) {
			value = createValue();
		}
		return value;
	}

	private String createValue() {
		int selectedRow = table.getSelectedRow();
		if (selectedRow >= 0) {
			return model.getItemAt(table.convertRowIndexToModel(selectedRow));
		}
		return null;
	}

	@Override
	protected JPanel getValuePanel() {
		return valuePanel;
	}
	
	private static class Model extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		public static final int ITEM_GROUP_NAME = 0;
		
		private static final String[] columnNames = new String[] { "Group" };
		
		private List<String> items;

		public Model() {
			this.items = new ArrayList<String>(0);
		}
		
		public void setData(List<String> items) {
			this.items = items;
			fireTableDataChanged();
		}

		public String getItemAt(int rowIndex) {
			return items.get(rowIndex);
		}

		@Override
		public int getColumnCount() {
			return columnNames.length;
		}

		@Override
		public int getRowCount() {
			return items.size();
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return items.get(rowIndex);
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			switch (columnIndex) {
			case ITEM_GROUP_NAME:
				return String.class;
				
			default:
				return null;
			}
		}

		@Override
		public String getColumnName(int column) {
			return columnNames[column];
		}
	}
}
