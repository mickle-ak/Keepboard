package kb.gui.utils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class TableRootRowSelectionListener {
	private JTable table;
	private int rootSelectedRow;
	
	public TableRootRowSelectionListener(JTable table) {
		this.table = table;
		this.rootSelectedRow = table.getSelectedRow();
		
		addSelectionListener();
	}

	private void addSelectionListener() {
		table.getSelectionModel().addListSelectionListener(crateTableSelectionListener());
	}

	private ListSelectionListener crateTableSelectionListener() {
		return new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow < 0 || rootSelectedRow < 0 || table.getSelectedRowCount() == 1) {
					rootSelectedRow = selectedRow;
				}
			}
		};
	}
	
	public int getRootSelectedRow() {
		return rootSelectedRow;
	}
}
