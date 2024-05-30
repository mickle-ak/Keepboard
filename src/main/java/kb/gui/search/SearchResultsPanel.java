package kb.gui.search;

import kb.ClipboardContent;
import kb.ClipboardItem;
import kb.PreferencesPersistor;
import kb.SelectedClipboardContent;
import kb.gui.ActionsButtonBuilder;
import kb.gui.prefs.MyPreferences;
import kb.gui.utils.TablePanel;
import kb.gui.utils.TablePanelBuilder;
import kb.utils.Observable;
import kb.utils.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private JTable table;
	private SearchResultsTableModel model;
	private TablePanel<SearchResultsTableModel> tablePanel;
	
	private TableColumn nameColumn;
	private TableColumn groupColumn;
	
	public SearchResultsPanel(SearchPanel searchPanel) {
		createComponents();
		addComponents();
		addActions(searchPanel);
	}
	
	private void addActions(SearchPanel searchPanel) {
		addTableApprovalObserver(searchPanel.getApprovalObservable());
		addTableSelectionListener(searchPanel.getSelectionObservable());
	}
	
	public void addNavigationListeners(JTextField textField) {
		tablePanel.addNavigationListeners(textField);
	}

	private void addTableApprovalObserver(final Observable<SelectedClipboardContent> approvalObservable) {
		tablePanel.getApprovalObservable().addObserver(new Observer<TablePanel.ApprovalData>() {
			@Override
			public void update(TablePanel.ApprovalData data) {
				approvalObservable.updateObservers(new SelectedClipboardContent(getSelectedClipboardItemContent(), data.isSecondaryAction()));
			}
		});
	}
	
	public ClipboardContent getSelectedClipboardItemContent() {
		ClipboardItem result = getSelectedItem();
		if (result != null) {
			return result.isDeleted() ? null : result.getContent();
		}
		return null;
	}
	
	public ClipboardItem getSelectedItem() {
		List<ClipboardItem> selectedItems = getSelectedItems();
		if (!selectedItems.isEmpty()) {
			ClipboardItem result = selectedItems.get(0);
			return result.isDeleted() ? null : result;
		}
		return null;
	}
	
	private List<ClipboardItem> getSelectedItems() {
		List<ClipboardItem> result = new ArrayList<ClipboardItem>();
		int[] selectedRows = table.getSelectedRows();
		
		for (int rowIndex : selectedRows) {
			result.add(model.getItemAt(table.convertRowIndexToModel(rowIndex)).getClipboardItem());
		}
		
		return result;
	}

	private void addTableSelectionListener(Observable<ClipboardItem> selectionObservable) {
		table.getSelectionModel().addListSelectionListener(crateTableSelectionListener(selectionObservable));
	}
	
	private ListSelectionListener crateTableSelectionListener(final Observable<ClipboardItem> selectionObservable) {
		return new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				selectionObservable.updateObservers(getSelectedItem());
			}
		};
	}

	private void createComponents() {
		createTable();
		adjustTableColumnsVisibility();
	}

	private void adjustTableColumnsVisibility() {
		nameColumn = table.getColumnModel().getColumn(1);
		groupColumn = table.getColumnModel().getColumn(2);
		
		if (!MyPreferences.INSTANCE.isShowNameSearchColumn()) {
			table.getColumnModel().removeColumn(nameColumn);
		}
		
		if (!MyPreferences.INSTANCE.isShowGroupSearchColumn()) {
			table.getColumnModel().removeColumn(groupColumn);
		}
	}

	private void createTable() {
		table = new JTable(model = new SearchResultsTableModel());
		table.getTableHeader().setReorderingAllowed(false);
		
		tablePanel = new TablePanelBuilder<SearchResultsTableModel>(table)
				.setFilterFieldLabelText("Item:")
				.setFilterColumnIndex(0)
				.setFilterFieldLabelMnemonic(KeyEvent.VK_E)
				.setCustomActionsComponent(createColumnsButton())
				.build();
	}

	private Component createColumnsButton() {
		return new ActionsButtonBuilder(createColumnsMenu())
				.setText("Columns")
				.setMnemonic(KeyEvent.VK_N)
				.setSize(new Dimension(100, 25))
				.build();
	}

	private JPopupMenu createColumnsMenu() {
		JPopupMenu result = new JPopupMenu();
		result.add(createNameMenuItem());
		result.add(createGroupMenuItem());
		return result;
	}

	private JMenuItem createNameMenuItem() {
		final JCheckBoxMenuItem result = new JCheckBoxMenuItem("Show name");
		result.setSelected(MyPreferences.INSTANCE.isShowNameSearchColumn());
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showNameMenuActionPerformed(result.isSelected());
			}
		});
		return result;
	}

	private void showNameMenuActionPerformed(boolean selected) {
		if (selected) {
			table.getColumnModel().addColumn(nameColumn);
		} else {
			table.getColumnModel().removeColumn(nameColumn);
		}
		
		MyPreferences.INSTANCE.setShowNameSearchColumn(selected);
		new PreferencesPersistor().storeToDisk();
	}

	private JMenuItem createGroupMenuItem() {
		final JCheckBoxMenuItem result = new JCheckBoxMenuItem("Show group");
		result.setSelected(MyPreferences.INSTANCE.isShowGroupSearchColumn());
		result.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showGroupMenuActionPerformed(result.isSelected());
			}
		});
		return result;
	}
	
	private void showGroupMenuActionPerformed(boolean selected) {
		if (selected) {
			table.getColumnModel().addColumn(groupColumn);
		} else {
			table.getColumnModel().removeColumn(groupColumn);
		}
		
		MyPreferences.INSTANCE.setShowGroupSearchColumn(selected);
		new PreferencesPersistor().storeToDisk();
	}

	private void addComponents() {
		setLayout(new GridBagLayout());
		addTable();
	}

	private void addTable() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(tablePanel, c);
	}
	
	public void appendResultsPart(List<SearchResultItem> resultsPart) {
		model.append(resultsPart);
	}
	
	public void clearResults() {
		model.clear();
	}
	
	public JTable getTable() {
		return table;
	}
	
	public TablePanel<SearchResultsTableModel> getTablePanel() {
		return tablePanel;
	}
}
