package kb.utils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableRowSorter;

public class TableTextFieldFilter {
	private JTextField textField;
	private TableRowSorter<?> tableRowSorter;
	private int filterColumn;
	private String filterText;
	
	public TableTextFieldFilter(JTextField textField, TableRowSorter<?> tableRowSorter, int filterColumn) {
		this.textField = textField;
		this.tableRowSorter = tableRowSorter;
		this.filterColumn = filterColumn;
		tableRowSorter.setRowFilter(createRowFilter());
		addDocumentListener(textField);
	}

	private RowFilter<Object, Integer> createRowFilter() {
		return new RowFilter<Object, Integer>() {
			@Override
			public boolean include(Entry<? extends Object, ? extends Integer> entry) {
				if (filterText == null || filterText.isEmpty()) {
					return true;
				}
				
				String value = entry.getValue(filterColumn) == null ? null : entry.getValue(filterColumn).toString().trim();
				
				if (value == null || value.length() < filterText.length()) {
					return false;
				}
				
				return value.substring(0, filterText.length()).equalsIgnoreCase(filterText);
			}
			
		};
	}

	private void addDocumentListener(JTextField textField) {
		textField.getDocument().addDocumentListener(createDocumentListener());
	}

	private DocumentListener createDocumentListener() {
		return new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				filter();
			}
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				filter();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				// Not needed
			}
		};
	}
	
	private void filter() {
		this.filterText = textField.getText().trim();
		tableRowSorter.sort();
	}
}
