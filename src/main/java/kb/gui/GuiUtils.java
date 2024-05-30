package kb.gui;

import java.awt.*;

public final class GuiUtils {
	private GuiUtils() {
		// private constructor
	}
	
	public static void setSize(Component component, Dimension size) {
		component.setMinimumSize(new Dimension(size));
		component.setPreferredSize(new Dimension(size));
	}
}
