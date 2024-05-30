package kb.gui.utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class MyTableCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 1L;
	
	private List<TableCellDecorator> decorators = new ArrayList<TableCellDecorator>();
	
	public MyTableCellRenderer() {
		this.putClientProperty("html.disable", Boolean.TRUE);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component result = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		addDecorations(result, table, value, isSelected, hasFocus, row, column);
		return result;
	}
	
	private void addDecorations(Component component, JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		for (TableCellDecorator decorator : decorators) {
			decorator.decorate(component, table, value, isSelected, hasFocus, row, column);
		}
	}

	public void addDecorator(TableCellDecorator decorator) {
		this.decorators.add(decorator);
	}
}