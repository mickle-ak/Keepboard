package kb.gui.utils;

import javax.swing.*;
import java.awt.*;

public interface TableCellDecorator {
	
	public void decorate(Component component, JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column);
}
