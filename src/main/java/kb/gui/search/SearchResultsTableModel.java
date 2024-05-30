package kb.gui.search;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class SearchResultsTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
	
	public static final int ITEM = 0, NAME = 1, GROUP = 2;
	
	private static final String[] columnNames = new String[] { "Item", "Name", "Group" };
	
	private List<SearchResultItem> items;

	public SearchResultsTableModel() {
		this.items = new ArrayList<SearchResultItem>(0);
	}
	
	public void append(List<SearchResultItem> items) {
		if (items.isEmpty()) {
			return;
		}
		
		int firstRow = this.items.size();
		this.items.addAll(items);
		fireTableRowsInserted(firstRow, firstRow + items.size() - 1); 
	}
	
	public void clear() {
		this.items.clear();
		fireTableDataChanged();
	}
	
	public List<SearchResultItem> getData() {
		return items;
	}

	public SearchResultItem getItemAt(int rowIndex) {
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
		SearchResultItem row = items.get(rowIndex);
		
		switch (columnIndex) {
		case ITEM:
			return row.getClipboardItem().getDisplayText();
		case NAME:
			return row.getClipboardItem().getName();
		case GROUP:
			return row.getGroupName();
		default:
			return null;
		}
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
}