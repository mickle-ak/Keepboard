package kb.gui.utils;

import kb.gui.GuiUtils;
import kb.gui.MyMouseListener;
import kb.utils.Observable;
import kb.utils.TableTextFieldFilter;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TablePanel<M extends TableModel> extends JPanel {
	private static final long serialVersionUID = 1L;

	public static class ApprovalData {
		private final int selectedRow;
		private final boolean secondaryAction;

		public ApprovalData(int selectedRow, boolean secondaryAction) {
			this.selectedRow = selectedRow;
			this.secondaryAction = secondaryAction;
		}

		public int getSelectedRow() {
			return selectedRow;
		}

		public boolean isSecondaryAction() {
			return secondaryAction;
		}
	}
	
	private JTable table;
	private JScrollPane scrollPane;
	private JTextField textField;
	private JLabel filterFieldLabel; 
	private Component actionsButton;
	private int columnIndex;
	private List<TableCellDecorator> decorators;
	private TableRootRowSelectionListener tableRootRowSelectionListener;
	private boolean altPressed = false;
	private boolean altNumActionScheduled = false;
	
	private Observable<ApprovalData> approvalObservable = new Observable<ApprovalData>();
	private Observable<int[]> deleteObservable = new Observable<int[]>();
	
	TablePanel(JTable table, int columnIndex, JLabel filterFieldLabel, Component actionsButton, List<TableCellDecorator> decorators) {
		this.table = table;
		this.columnIndex = columnIndex;
		this.filterFieldLabel = filterFieldLabel;
		this.actionsButton = actionsButton;
		this.decorators = decorators;
		this.tableRootRowSelectionListener = new TableRootRowSelectionListener(table);
		
		createComponents();
		addComponents();
		enableFiltering();
		addNavigationListeners(textField);
		adjustTable();
		addActions();
	}

	private void addActions() {
		table.addMouseListener(createTableMouseListener());
		table.addKeyListener(createApprovalKeyListener());
		table.addKeyListener(createDeleteKeyListener());
		textField.addKeyListener(createApprovalKeyListener());
		textField.addFocusListener(createFilterFieldFocusListener());
	}
	
	private FocusListener createFilterFieldFocusListener() {
		return new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				textField.selectAll();
			}

			@Override
			public void focusLost(FocusEvent e) {
				textField.select(0, 0);
			}
		};
	}

	private void adjustTable() {
		table.setDefaultRenderer(String.class, createTableCellRenderer());
		table.setShowGrid(false);
		table.setRowMargin(3);
	}

	private TableCellRenderer createTableCellRenderer() {
		MyTableCellRenderer result = new MyTableCellRenderer();
		
		if (table.getColumnCount() == 1) {
			result.addDecorator(createOneColumnTableDecorator());
		}
		
		addCustomDecorators(result);
		result.addDecorator(createAltNumShortcutDecorator());
		
		return result;
	}

	private TableCellDecorator createAltNumShortcutDecorator() {
		return new TableCellDecorator() {
			@Override
			public void decorate(Component component, JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				
				JLabel label = (JLabel) component;
				
				if (altPressed && column == 0 && row < 10) {
					label.setIcon(null);
					label.setText(String.format("%d)  %s", row, label.getText()));
				}
			}
		};
	}

	private void addCustomDecorators(MyTableCellRenderer renderer) {
		for (TableCellDecorator decorator : decorators) {
			renderer.addDecorator(decorator);
		}
	}

	private TableCellDecorator createOneColumnTableDecorator() {
		return new TableCellDecorator() {
			@Override
			public void decorate(Component component, JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				
				if (!isSelected) {
					component.setBackground(Color.WHITE);
				}
			}
		};
	}

	public void addNavigationListeners(JTextField textField) {
		addArrowListeners(textField);
		addAltNumShortcutListener(textField);
		addAltNumFocusListener(textField);
	}

	private void addAltNumFocusListener(JTextField textField) {
		textField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				altPressed = false;
				altNumActionScheduled = false;
				refreshFirst10Rows();
			}
		});
	}
	
	private void refreshFirst10Rows() {
		int n = Math.min(table.getRowCount(), 10);
		for (int i = 0; i < n; i++) {
			int row = table.convertRowIndexToModel(i);
			((AbstractTableModel) table.getModel()).fireTableRowsUpdated(row, row);
		}
	}

	private void addAltNumShortcutListener(JTextField textField) {
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				handleAltKeyPressed(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				handleAltKeyReleased(e);
			}
		});
	}
	
	private void handleAltKeyPressed(KeyEvent e) {
		if (!e.isAltDown() || table.getRowCount() == 0) {
			return;
		}
		
		int keyCode = e.getKeyCode();
		
		if (keyCode == KeyEvent.VK_ALT) {
			showAltNumShortcuts(e);
		} else if (keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9) {
			executeAltNumShortcut(keyCode - KeyEvent.VK_0);
		}
	}
	
	private void executeAltNumShortcut(int row) {
		if (row < table.getRowCount()) {
			selectTableRow(row);
			altNumActionScheduled = true;
		}
	}

	private void showAltNumShortcuts(KeyEvent e) {
		if (!e.isAltDown() || table.getRowCount() == 0) {
			return;
		}
		
		altPressed = true;
		refreshFirst10Rows();
	}

	private void handleAltKeyReleased(KeyEvent e) {
		if (e.getKeyCode() != KeyEvent.VK_ALT) {
			return;
		}
		
		altPressed = false;
		
		if (altNumActionScheduled) {
			altNumActionScheduled = false;
			selectionApproved(false);
		}
		
		refreshFirst10Rows();
	}

	private void addArrowListeners(JTextField textField) {
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				textFieldKeyPressed(e);
			}
		});
	}
	
	private void textFieldKeyPressed(KeyEvent e) {
		handleTableSelectionChange(e);
		scrollToSelectedRow();
	}

	private void handleTableSelectionChange(KeyEvent e) {
		handleTableSelectionArrowKeys(e);
		handleTableSelectionHomeEndKeys(e);
	}

	private void handleTableSelectionHomeEndKeys(KeyEvent e) {
		if (table.getRowCount() == 0) {
			return;
		}
		
		int newSelectedRow;
		
		switch(e.getKeyCode()) {
			case KeyEvent.VK_PAGE_UP:
				newSelectedRow = Math.max(0, table.getSelectedRow() - getNumberOfVisibleRows());
				break;
			case KeyEvent.VK_PAGE_DOWN:
				newSelectedRow = Math.min(table.getRowCount() - 1, table.getSelectedRow() + getNumberOfVisibleRows());
				break;
			case KeyEvent.VK_HOME:
				newSelectedRow = 0;
				break;
			case KeyEvent.VK_END:
				newSelectedRow = table.getRowCount() - 1;
				break;
			default:
				return;
		}
		
		if (!e.isShiftDown()) {
			selectTableRow(newSelectedRow);
			return;
		}
		
		int rootSelectedRow = tableRootRowSelectionListener.getRootSelectedRow();
		if (rootSelectedRow < 0) {
			rootSelectedRow = 0;
		}
		
		selectTableRowsIntervalAccordingToRootSelectedRow(rootSelectedRow, newSelectedRow);
	}

	public int getNumberOfVisibleRows() {
		return (int) (Math.min(table.getSize().getHeight(), scrollPane.getHeight()) / (table.getRowHeight() + 2 * table.getRowMargin()));
	}

	private void handleTableSelectionArrowKeys(KeyEvent e) {
		boolean shiftPressed = e.isShiftDown();
		switch(e.getKeyCode()) {
		case KeyEvent.VK_UP:
			moveTableSelection(-1, shiftPressed);
			break;
		case KeyEvent.VK_DOWN:
			moveTableSelection(1, shiftPressed);
			break;
		}
	}

	private void moveTableSelection(int increment, boolean shiftPressed) {
		if (table.getRowCount() == 0) {
			return;
		}
		
		if (table.getSelectedRow() < 0) {
			selectTableRow(0);
			return;
		}
		
		int rootSelectedRow = tableRootRowSelectionListener.getRootSelectedRow();
		int[] selectedRows = table.getSelectedRows();
		
		int selectedRow = selectedRows[0] < rootSelectedRow ? selectedRows[0] : selectedRows[selectedRows.length - 1];
		
		int newSelectedRow = selectedRow + increment;
		
		if (newSelectedRow > table.getRowCount() - 1 || newSelectedRow < 0) {
			return;
		}
		
		if (!shiftPressed) {
			selectTableRow(newSelectedRow);
			return;
		}
		
		selectTableRowsIntervalAccordingToRootSelectedRow(rootSelectedRow, newSelectedRow);
	}

	private void selectTableRowsIntervalAccordingToRootSelectedRow(int rootSelectedRow, int newSelectedRow) {
		int startRow = rootSelectedRow < newSelectedRow ? rootSelectedRow : newSelectedRow;
		int endRow = rootSelectedRow > newSelectedRow ? rootSelectedRow : newSelectedRow;
		selectTableRowsInterval(startRow, endRow);
	}
	
	private void scrollToSelectedRow() {
		if (table.getSelectedRow() != -1) {
			table.scrollRectToVisible(table.getCellRect(table.getSelectedRow(), 0, false));
		}
	}

	private void selectTableRow(int row) {
		table.getSelectionModel().setSelectionInterval(row, row);
	}
	
	private void selectTableRowsInterval(int startRow, int endRow) {
		table.getSelectionModel().setSelectionInterval(startRow, endRow);
	}

	private void createComponents() {
		createTextField();
	}

	private void createTextField() {
		textField = new JTextField();
		GuiUtils.setSize(textField, new Dimension(50, 25));
	}

	private void addComponents() {
		this.setLayout(new GridBagLayout());
		
		addFilterFieldLabel();
		addTextField();
		
		if (hasActionsButton()) {
			addActionsButton();
		}
			
		addTable();
	}

	private boolean hasActionsButton() {
		return actionsButton != null;
	}

	private void addFilterFieldLabel() {
		if (hasFilterFieldLabel()) {
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0;
			c.insets = new Insets(0, 3, 3, 0);
			c.fill = GridBagConstraints.HORIZONTAL;
			this.add(filterFieldLabel, c);
			filterFieldLabel.setLabelFor(textField);
		}
	}

	private boolean hasFilterFieldLabel() {
		return filterFieldLabel != null;
	}

	private void addActionsButton() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = hasFilterFieldLabel() ? 2 : 1; c.gridy = 0;
		c.insets = new Insets(0, 5, 3, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		this.add(actionsButton, c);
	}

	private void addTextField() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = hasFilterFieldLabel() ? 1 : 0; c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 3, 0);
		this.add(textField, c);
	}

	private void addTable() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 1;
		c.gridwidth = hasFilterFieldLabel() ? 3 : 2;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		this.add(scrollPane = new JScrollPane(table), c);
	}

	private void enableFiltering() {
		TableRowSorter<M> tableRowSorter = new TableRowSorter<M>(getTableModel());
		table.setRowSorter(tableRowSorter);
		disableSorting(tableRowSorter);
		new TableTextFieldFilter(textField, tableRowSorter, columnIndex);
	}

	private void disableSorting(TableRowSorter<M> tableRowSorter) {
		for (int i = 0, n = getTableModel().getColumnCount(); i < n; i++) {
			tableRowSorter.setSortable(i, false);
		}
	}

	@SuppressWarnings("unchecked")
	private M getTableModel() {
		return (M) table.getModel();
	}
	
	public void clearFilter() {
		textField.setText("");
		textField.requestFocusInWindow();
	}

	public JScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public Observable<ApprovalData> getApprovalObservable() {
		return approvalObservable;
	}
	
	public Observable<int[]> getDeleteObservable() {
		return deleteObservable;
	} 
	
	private MouseListener createTableMouseListener() {
		return new MyMouseListener() {
			@Override
			public void mouseDoubleClicked(MouseEvent e) {
				selectionApproved(e.isShiftDown());
			}
		};
	}
	
	public void selectionApproved(boolean secondaryAction) {
		altPressed = false;
		selectFirstRowIfNoRowSelected();
		if (table.getSelectedRow() >= 0) {
			approvalObservable.updateObservers(new ApprovalData(table.getSelectedRow(), secondaryAction));
		}
	}

	private void selectFirstRowIfNoRowSelected() {
		if (table.getRowCount() > 0 && table.getSelectedRow() < 0) {
			selectTableRow(0);
		}
	}

	public void focusFilterField() {
		textField.requestFocusInWindow();
	}

	private KeyAdapter createApprovalKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() ==  KeyEvent.VK_ENTER) {
					e.consume();
					selectionApproved(e.isShiftDown());
				}
			}
		};
	}
	
	private KeyAdapter createDeleteKeyListener() {
		return new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					deleteKeyPressed();
				}
			}
		};
	}

	private void deleteKeyPressed() {
		if (table.getSelectedRow() >= 0) {
			deleteObservable.updateObservers(table.getSelectedRows());
		}
	}
}
