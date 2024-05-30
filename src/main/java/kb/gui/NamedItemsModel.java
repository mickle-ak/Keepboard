package kb.gui;

import kb.ClipboardItem;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;


public class NamedItemsModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		
		public static final int NAME = 0, ITEM = 1;
		
		private static final String[] columnNames = new String[] { "Name", "Item" };
		
		private List<ClipboardItem> items;

		public NamedItemsModel() {
			this.items = new ArrayList<ClipboardItem>(0);
		}
		
		public void setData(List<ClipboardItem> items) {
			this.items = items;
			fireTableDataChanged();
		}
		
		public List<ClipboardItem> getData() {
			return items;
		}

		public ClipboardItem getItemAt(int rowIndex) {
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
			ClipboardItem row = items.get(rowIndex);
			
			switch (columnIndex) {
			case NAME:
				return row.getName();
			case ITEM:
				return row.getDisplayText();
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